package org.epam.utils.mappers;

import org.epam.models.dto.TrainingViewDto;
import org.epam.models.entity.Training;
import org.epam.models.entity.TrainingView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface TrainingViewMapper {
    TrainingViewMapper INSTANCE = Mappers.getMapper(TrainingViewMapper.class);

    @Mapping(target = "trainingsIds", source = "trainings", qualifiedByName = "toTrainingIds")
    TrainingViewDto toDto(TrainingView trainingView);

    @Named("toTrainingIds")
    default List<String> toTrainingIds(List<Training> trainings) {
        if (trainings == null) return Collections.emptyList();
        return trainings.stream()
                .map(Training::getId)
                .collect(Collectors.toList());
    }
}
