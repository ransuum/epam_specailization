package org.epam.models.request.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TrainingRequestCreate(@NotBlank(message = "Trainee id is blank") String traineeId,
                                    @NotBlank(message = "Trainer id is blank") String trainerId,
                                    @NotBlank(message = "Training Name is blank") String trainingName,
                                    @NotBlank(message = "TrainingType id is blank") String trainingViewId,
                                    @NotBlank(message = "Date is null") String startTime,
                                    @NotNull(message = "duration is null") Long duration) {
}
