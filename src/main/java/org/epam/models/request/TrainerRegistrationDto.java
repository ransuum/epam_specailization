package org.epam.models.request;

import jakarta.validation.constraints.NotBlank;

public record TrainerRegistrationDto(@NotBlank String firstname,
                                     @NotBlank String lastname,
                                     @NotBlank String specializationId) {
}
