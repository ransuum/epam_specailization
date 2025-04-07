package org.epam.models.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TraineeCreateDto(@NotBlank(message = "Firstname is blank") String firstname,
                               @NotBlank(message = "Lastname is blank") String lastname,
                               @NotNull(message = "dateOfBirth is null") String dateOfBirth,
                               @NotBlank(message = "address is blank") String address) {
}
