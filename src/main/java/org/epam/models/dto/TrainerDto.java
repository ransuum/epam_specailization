package org.epam.models.dto;

import java.util.List;

public record TrainerDto(String id,
                         UserDto user,
                         List<String> trainingsIds,
                         TrainingTypeDto specialization) {
}
