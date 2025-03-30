package org.epam.models.request.update;

import jakarta.validation.constraints.NotBlank;

public record TrainerRequestUpdate(String specializationId,
                                   @NotBlank(message = "Firstname is blank") String firstname,
                                   @NotBlank(message = "Lastname is blank") String lastname,
                                   @NotBlank(message = "Username is blank") String username,
                                   @NotBlank(message = "Is account active?") Boolean isActive) {
}
