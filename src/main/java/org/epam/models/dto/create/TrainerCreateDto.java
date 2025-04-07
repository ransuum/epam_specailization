package org.epam.models.dto.create;

import jakarta.validation.constraints.NotBlank;

public record TrainerCreateDto(@NotBlank(message = "Firstname is blank") String firstname,
                               @NotBlank(message = "Lastname is blank") String lastname,
                               @NotBlank(message = "specialization is blank") String specialization) {
}
