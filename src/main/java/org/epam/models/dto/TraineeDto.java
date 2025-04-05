package org.epam.models.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

public record TraineeDto(String id,
                         UserDto users,
                         @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
                         LocalDate dateOfBirth,
                         String address,
                         List<TrainerDto> trainers) {
}
