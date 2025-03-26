package org.epam.models.request;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(@NotBlank(message = "username is blank") String username,
                          @NotBlank(message = "password is blank") String password) {
}
