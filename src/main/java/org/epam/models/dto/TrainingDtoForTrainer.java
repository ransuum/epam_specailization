package org.epam.models.dto;

import java.time.LocalDate;

public record TrainingDtoForTrainer(String id,
                                   TraineeDto trainee,
                                   String trainingName,
                                   TrainingTypeDto trainingType,
                                   LocalDate startTime,
                                   Long duration) {
}
