package org.epam.models.dto;

import java.time.LocalDate;

public record TrainingListDto(String trainingName,
                              LocalDate startTime,
                              String trainingType,
                              Long duration,
                              String trainerName,
                              String traineeName) {

    public record TrainingListDtoForUser(String trainingName,
                                            LocalDate startTime,
                                            String trainingType,
                                            Long duration,
                                            String firstname,
                                            String lastname) {

    }
}
