package org.epam.models.dto;


import java.util.List;

public record TrainerDto(String id,
                         UserDto users,
                         String specialization,
                         List<TraineeDto> trainees) {
}
