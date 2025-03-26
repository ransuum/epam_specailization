package org.epam.models.dto;

import java.util.List;

public record TrainingTypeDto(String id,
                              String trainingName,
                              List<String> trainingsIds,
                              List<String> specializationIds) {
}
