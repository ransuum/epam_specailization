package org.epam.models.request.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TrainingRequestUpdate(@NotBlank(message = "Trainee id is blank") String traineeId,
                                    @NotBlank(message = "Trainer id is blank") String trainerId,
                                    @NotBlank(message = "Training Name is blank") String trainingName,
                                    @NotBlank(message = "TrainingType id is blank") String trainingViewId,
                                    @NotNull(message = "Date is null") LocalDate startTime,
                                    @NotNull(message = "duration is null") Long duration) {
}
