package org.epam.models.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthDto(@NotBlank(message = "username is blank") String username,
                      @NotBlank(message = "password is blank") String password) {
}
