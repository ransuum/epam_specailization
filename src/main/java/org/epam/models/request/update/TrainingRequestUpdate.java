package org.epam.models.request.update;

public record TrainingRequestUpdate(String traineeId,
                                    String trainerId,
                                    String trainingName,
                                    String trainingViewId,
                                    String trainingStartDate,
                                    Long duration) {
}

