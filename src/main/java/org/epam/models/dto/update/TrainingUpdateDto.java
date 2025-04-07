package org.epam.models.dto.update;

public record TrainingUpdateDto(String traineeUsername,
                                String trainerUsername,
                                String trainingName,
                                String trainingTypeId,
                                String trainingStartDate,
                                Long duration) {
}

