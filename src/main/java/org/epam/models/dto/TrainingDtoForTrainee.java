package org.epam.models.dto;

import java.time.LocalDate;

public record TrainingDtoForTrainee(String id,
                                    TrainerDto trainer,
                                    String trainingName,
                                    TrainingTypeDto trainingType,
                                    LocalDate startTime,
                                    Long duration) {
}
