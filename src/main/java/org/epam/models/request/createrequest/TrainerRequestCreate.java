package org.epam.models.request.createrequest;

import jakarta.validation.constraints.NotBlank;

public record TrainerRequestCreate(@NotBlank(message = "user id is blank") String userId,
                                   @NotBlank(message = "specialization is blank") String specializationId) {
}
