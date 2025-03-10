package org.epam.models.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.epam.models.enums.TrainingType;

import java.time.LocalDate;

public record TrainingDto(Integer id,
                          TraineeDto trainee,
                          TrainerDto trainer,
                          String trainingName,
                          TrainingType trainingType,
                          LocalDate trainingDate,
                          Integer trainingDuration) {

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
