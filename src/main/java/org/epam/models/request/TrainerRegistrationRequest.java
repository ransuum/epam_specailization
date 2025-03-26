package org.epam.models.request;

import jakarta.validation.constraints.NotBlank;

public record TrainerRegistrationRequest(@NotBlank String firstname,
                                         @NotBlank String lastname,
                                         @NotBlank String specializationId) {
}
