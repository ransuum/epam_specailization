package org.epam.utils.mappers;

import org.epam.models.dto.TrainingDto;
import org.epam.models.dto.TrainingDtoForTrainee;
import org.epam.models.dto.TrainingDtoForTrainer;
import org.epam.models.entity.Training;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(uses = {TraineeMapper.class, TrainerMapper.class, TrainingViewMapper.class})
public interface TrainingMapper {
    TrainingMapper INSTANCE = Mappers.getMapper(TrainingMapper.class);

    TrainingDto toDto(Training training);

    TrainingDtoForTrainee toDtoForTrainee(Training training);

    TrainingDtoForTrainer toDtoForTrainer(Training training);
}
