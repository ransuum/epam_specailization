package org.epam.utils.mappers;

import org.epam.models.dto.TrainingDto;
import org.epam.models.dto.TrainingListDto;
import org.epam.models.entity.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(uses = {TraineeMapper.class, TrainerMapper.class, TrainingTypeMapper.class})
public interface TrainingMapper {
    TrainingMapper INSTANCE = Mappers.getMapper(TrainingMapper.class);

    @Mapping(target = "trainee.users.password", ignore = true)
    @Mapping(target = "trainer.users.password", ignore = true)
    @Mapping(target = "trainer.trainees", ignore = true)
    @Mapping(target = "trainee.trainers", ignore = true)
    @Mapping(target = "trainer.specialization", expression = "java(trainer.getSpecialization().getTrainingTypeName().getVal())")
    TrainingDto toDto(Training training);

    @Mapping(target = "trainingType", source = "trainingType", qualifiedByName = "mapTrainingType")
    @Mapping(target = "trainerName", expression = "java(training.getTrainer().getUsers().getFirstName())")
    @Mapping(target = "traineeName", expression = "java(training.getTrainee().getUsers().getFirstName())")
    TrainingListDto toListDto(Training training);

    @Mapping(target = "trainingType", source = "trainingType", qualifiedByName = "mapTrainingType")
    @Mapping(target = "firstname", expression = "java(training.getTrainer().getUsers().getFirstName())")
    @Mapping(target = "lastname", expression = "java(training.getTrainer().getUsers().getLastName())")
    TrainingListDto.TrainingListDtoForUser toListDtoForTrainer(Training training);

    @Mapping(target = "trainingType", source = "trainingType", qualifiedByName = "mapTrainingType")
    @Mapping(target = "firstname", expression = "java(training.getTrainee().getUsers().getFirstName())")
    @Mapping(target = "lastname", expression = "java(training.getTrainee().getUsers().getLastName())")
    TrainingListDto.TrainingListDtoForUser toListDtoForTrainee(Training training);
}
