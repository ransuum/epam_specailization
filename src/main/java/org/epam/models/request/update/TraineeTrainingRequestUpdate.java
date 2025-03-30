package org.epam.models.request.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TraineeTrainingRequestUpdate(@NotBlank(message = "Trainer id is blank") String trainerUsername,
                                           @NotBlank(message = "Training Name is blank") String trainingName,
                                           @NotNull(message = "Type is null") String trainingTypeName,
                                           @NotBlank(message = "Date is null") String startTime,
                                           @NotNull(message = "duration is null") Long duration) {
}
