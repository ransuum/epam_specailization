package org.epam.models.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record TrainingListDto(String trainingName,
                              @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
                              LocalDate startTime,
                              String trainingType,
                              Long duration,
                              String trainerName,
                              String traineeName) {

    public record TrainingListDtoForUser(String trainingName,
                                         @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
                                         LocalDate startTime,
                                         String trainingType,
                                         Long duration,
                                         String firstname,
                                         String lastname) {

    }
}
