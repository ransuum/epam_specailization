package org.epam.utils.mappers;

import org.epam.models.dto.TrainingTypeDto;
import org.epam.models.entity.Trainer;
import org.epam.models.entity.Training;
import org.epam.models.entity.TrainingType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface TrainingTypeMapper {
    TrainingTypeMapper INSTANCE = Mappers.getMapper(TrainingTypeMapper.class);

    @Mapping(target = "trainingsIds", source = "trainings", qualifiedByName = "toTrainingIds")
    @Mapping(target = "specializationIds", source = "trainers", qualifiedByName = "toSpecializationIds")
    @Mapping(target = "trainingName", expression = "java(trainingType.getTrainingName().getVal())")
    TrainingTypeDto toDto(TrainingType trainingType);

    @Named("toTrainingIds")
    default List<String> toTrainingIds(List<Training> trainings) {
        if (trainings == null) return Collections.emptyList();
        return trainings.stream()
                .map(Training::getId)
                .collect(Collectors.toList());
    }

    @Named("toSpecializationIds")
    default List<String> toSpecializationIds(List<Trainer> trainers) {
        if (trainers == null) return Collections.emptyList();
        return trainers.stream()
                .map(Trainer::getId)
                .collect(Collectors.toList());
    }
}
