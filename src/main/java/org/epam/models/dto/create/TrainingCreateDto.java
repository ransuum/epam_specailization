package org.epam.models.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrainingCreateDto(@NotBlank(message = "Trainee id is blank") String traineeUsername,
                                @NotBlank(message = "Trainer id is blank") String trainerUsername,
                                @NotBlank(message = "Training Name is blank") String trainingName,
                                @NotNull(message = "Training type name is null") String trainingTypeName,
                                @NotBlank(message = "Date is null") String startTime,
                                @NotNull(message = "duration is null") Long duration) {
}
