package org.epam.utils.mappers;

import org.epam.models.dto.TrainingTypeDto;
import org.epam.models.entity.TrainingType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TrainingTypeMapper {
    TrainingTypeMapper INSTANCE = Mappers.getMapper(TrainingTypeMapper.class);

    @Mapping(target = "trainingName", expression = "java(trainingType.getTrainingName().getVal())")
    TrainingTypeDto toDto(TrainingType trainingType);

    @Named("mapTrainingType")
    default String mapTrainingType(TrainingType trainingType) {
        return trainingType.getTrainingName().getVal();
    }
}
