package org.epam.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.SneakyThrows;

public record UserDto(String id,
                      String firstName,
                      String lastName,
                      String username,
                      Boolean isActive,
                      @JsonIgnore String password) {
    @SneakyThrows
    @Override
    public String toString() {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(this);
    }
}
