package org.epam.utils.mappers;

import org.epam.models.dto.AuthResponseDto;
import org.epam.models.dto.TraineeDto;
import org.epam.models.dto.TrainerDto;
import org.epam.models.dto.UserDto;
import org.epam.models.entity.Trainer;
import org.epam.models.entity.Training;
import org.epam.models.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Mapper(uses = {UserMapper.class, TrainingTypeMapper.class})
public interface TrainerMapper {
    TrainerMapper INSTANCE = Mappers.getMapper(TrainerMapper.class);

    @Mapping(target = "user.password", ignore = true)
    @Mapping(target = "specialization", expression = "java(trainer.getSpecialization().getTrainingTypeName().getVal())")
    @Mapping(target = "trainees", source = "trainings", qualifiedByName = "mapTraineesForTrainer")
    TrainerDto toDto(Trainer trainer);

    @Named("mapTraineesForTrainer")
    default List<TraineeDto> mapTraineesForTrainer(List<Training> trainings) {
        if (trainings == null) {
            return Collections.emptyList();
        }
        return trainings.stream()
                .map(Training::getTrainee)
                .distinct()
                .map(trainee -> {
                    return new TraineeDto(
                            trainee.getId(),
                            createUserDto(trainee.getUser()),
                            trainee.getDateOfBirth(),
                            trainee.getAddress(),
                            null
                    );
                })
                .collect(Collectors.toList());
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

    @Mapping(target = "username", expression = "java(trainer.getUser().getUsername())")
    @Mapping(target = "password", expression = "java(trainer.getUser().getPassword())")
    AuthResponseDto toAuthResponseDto(Trainer trainer);
}
