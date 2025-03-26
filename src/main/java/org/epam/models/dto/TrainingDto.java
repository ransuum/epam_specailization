package org.epam.models.dto;


import java.time.LocalDate;

public record TrainingDto(String id,
                          TraineeDto trainee,
                          TrainerDto trainer,
                          String trainingName,
                          TrainingTypeDto trainingType,
                          LocalDate startTime,
                          Long duration) {
}
