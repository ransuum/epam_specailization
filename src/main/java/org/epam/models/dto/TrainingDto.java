package org.epam.models.dto;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record TrainingDto(String id,
                          TraineeDto trainee,
                          TrainerDto trainer,
                          String trainingName,
                          TrainingTypeDto trainingType,
                          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
                          LocalDate startTime,
                          Long duration) {
}
