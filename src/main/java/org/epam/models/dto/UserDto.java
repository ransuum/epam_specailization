package org.epam.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record UserDto(String id,
                      String firstName,
                      String lastName,
                      String username,
                      Boolean isActive,
                      @JsonIgnore String password) {
}
