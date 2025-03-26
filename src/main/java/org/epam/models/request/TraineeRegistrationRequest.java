package org.epam.models.request;

import jakarta.validation.constraints.NotBlank;

public record TraineeRegistrationRequest(@NotBlank String firstname,
                                         @NotBlank String lastname,
                                         String dateOfBirth,
                                         String address) {
}
