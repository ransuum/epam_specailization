package org.epam.utils.mappers;

import org.epam.models.dto.AuthResponseDto;
import org.epam.models.dto.TraineeDto;
import org.epam.models.dto.TrainerDto;
import org.epam.models.dto.UserDto;
import org.epam.models.entity.Trainee;
import org.epam.models.entity.Training;
import org.epam.models.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

@Mapper(uses = {UserMapper.class})
public interface TraineeMapper {
    TraineeMapper INSTANCE = Mappers.getMapper(TraineeMapper.class);

    @Mapping(target = "user.password", ignore = true)
    @Mapping(source = "user", target = "user")
    @Mapping(source = "trainings", target = "trainers", qualifiedByName = "mapTrainersForTrainee")
    TraineeDto toDto(Trainee trainee);

    @Named("mapTrainersForTrainee")
    default List<TrainerDto> mapTrainersForTrainee(List<Training> trainings) {
        if (trainings == null) {
            return Collections.emptyList();
        }

        return trainings.stream()
                .map(Training::getTrainer)
                .distinct()
                .map(trainer -> new TrainerDto(trainer.getId(),
                        createUserDto(trainer.getUser()),
                        trainer.getSpecialization().getTrainingTypeName().getVal(),
                        null)
                ).toList();
    }

    default UserDto createUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getIsActive(),
                null
        );
    }

    @Mapping(target = "username", expression = "java(trainee.getUser().getUsername())")
    @Mapping(target = "password", expression = "java(trainee.getUser().getPassword())")
    AuthResponseDto toAuthResponseDto(Trainee trainee);
}
