package org.epam.models.request;

import lombok.Builder;
import org.epam.models.enums.TrainingType;

import java.time.LocalDate;

@Builder
public record TrainingRequest(Integer id,
                              Integer traineeId,
                              Integer trainerId,
                              String trainingName,
                              TrainingType trainingType,
                              LocalDate trainingDate,
                              Integer trainingDuration) {
}
