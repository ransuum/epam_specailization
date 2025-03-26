package org.epam.models.dto;

import java.time.LocalDate;
import java.util.List;

public record TraineeDto(String id,
                         UserDto user,
                         LocalDate dateOfBirth,
                         String address,
                         List<String> trainingsIds) {
}
