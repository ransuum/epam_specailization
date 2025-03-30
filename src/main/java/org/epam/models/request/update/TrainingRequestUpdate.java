package org.epam.models.request.update;

public record TrainingRequestUpdate(String traineeId,
                                    String trainerId,
                                    String trainingName,
                                    String trainingTypeId,
                                    String trainingStartDate,
                                    Long duration) {
}

