package org.epam.utils.mappers;

import org.epam.models.dto.TrainerDto;
import org.epam.models.entity.Trainer;
import org.epam.models.entity.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Mapper(uses = {UserMapper.class})
public interface TrainerMapper {
    TrainerMapper INSTANCE = Mappers.getMapper(TrainerMapper.class);

    @Mapping(target = "trainingsIds", source = "trainings", qualifiedByName = "toTrainingIds")
    TrainerDto toDto(Trainer trainer);

    @Named("toTrainingIds")
    default List<String> toTrainingIds(List<Training> trainings) {
        if (trainings == null) return Collections.emptyList();
        return trainings.stream()
                .map(Training::getId)
                .collect(Collectors.toList());
    }
}
