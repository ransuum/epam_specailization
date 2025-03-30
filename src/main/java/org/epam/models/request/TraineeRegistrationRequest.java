package org.epam.models.request;

import jakarta.validation.constraints.NotBlank;

public record TraineeRegistrationRequest(@NotBlank(message = "Firstname is blank") String firstname,
                                         @NotBlank(message = "Lastname is blank") String lastname,
                                         String dateOfBirth,
                                         String address) {
}
