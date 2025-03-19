package org.epam.models.request.traineeRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TraineeRequestCreate(@NotBlank(message = "userId is blank") String userId,
                                   @NotNull(message = "dateOfBirth is null") LocalDate dateOfBirth,
                                   @NotBlank(message = "address is blank") String address) {
}
