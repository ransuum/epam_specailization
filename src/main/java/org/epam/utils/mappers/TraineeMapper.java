package org.epam.utils.mappers;

import org.epam.models.dto.AuthResponseDto;
import org.epam.models.dto.TraineeDto;
import org.epam.models.dto.TrainerDto;
import org.epam.models.dto.UserDto;
import org.epam.models.entity.Trainee;
import org.epam.models.entity.Training;
import org.epam.models.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

@Mapper(uses = {UserMapper.class})
public interface TraineeMapper {
    TraineeMapper INSTANCE = Mappers.getMapper(TraineeMapper.class);

    @Mapping(target = "users.password", ignore = true)
    @Mapping(source = "users", target = "users")
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
                        createUserDto(trainer.getUsers()),
                        trainer.getSpecialization().getTrainingTypeName().getVal(),
                        null)
                ).toList();
    }

    default UserDto createUserDto(Users users) {
        return new UserDto(
                users.getId(),
                users.getUsername(),
                users.getFirstName(),
                users.getLastName(),
                users.getIsActive(),
                null
        );
    }
}
