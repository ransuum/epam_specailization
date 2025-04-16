package org.epam.models.dto.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrainerUpdateDto(String specialization,
                               @NotBlank(message = "Firstname is blank") String firstname,
                               @NotBlank(message = "Lastname is blank") String lastname,
                               @NotBlank(message = "Username is blank") String username,
                               @NotNull(message = "Is account active?") Boolean isActive) {
}
