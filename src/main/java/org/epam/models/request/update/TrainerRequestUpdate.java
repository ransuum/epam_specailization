package org.epam.models.request.update;

import jakarta.validation.constraints.NotBlank;

public record TrainerRequestUpdate(String specializationId,
                                   @NotBlank String firstname,
                                   @NotBlank String lastname,
                                   @NotBlank String username,
                                   @NotBlank Boolean isActive) {
}
