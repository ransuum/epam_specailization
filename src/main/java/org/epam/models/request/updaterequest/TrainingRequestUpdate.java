package org.epam.models.request.updaterequest;


import java.time.LocalDate;

public record TrainingRequestUpdate(String traineeId,
                                    String trainerId,
                                    String trainingName,
                                    String trainingViewId,
                                    LocalDate trainingStartDate,
                                    Long duration) {
}

