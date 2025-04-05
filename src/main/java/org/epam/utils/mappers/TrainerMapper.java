package org.epam.utils.mappers;

import org.epam.models.dto.AuthResponseDto;
import org.epam.models.dto.TraineeDto;
import org.epam.models.dto.TrainerDto;
import org.epam.models.dto.UserDto;
import org.epam.models.entity.Trainer;
import org.epam.models.entity.Training;
import org.epam.models.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;


@Mapper(uses = {UserMapper.class, TrainingTypeMapper.class})
public interface TrainerMapper {
    TrainerMapper INSTANCE = Mappers.getMapper(TrainerMapper.class);

    @Mapping(target = "users.password", ignore = true)
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
                .map(trainee -> new TraineeDto(
                            trainee.getId(),
                            createUserDto(trainee.getUsers()),
                            trainee.getDateOfBirth(),
                            trainee.getAddress(),
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

    @Mapping(target = "username", expression = "java(trainer.getUsers().getUsername())")
    @Mapping(target = "password", expression = "java(trainer.getUsers().getPassword())")
    AuthResponseDto toAuthResponseDto(Trainer trainer);
}
