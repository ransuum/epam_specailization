package org.epam.models.dto;


import java.util.List;

public record TrainerDto(String id,
                         UserDto user,
                         String specialization,
                         List<TraineeDto> trainees) {
}
