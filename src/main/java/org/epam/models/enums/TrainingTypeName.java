package org.epam.models.enums;

import lombok.Getter;
import org.epam.exception.NotFoundException;

import java.util.Arrays;

@Getter
public enum TrainingName {
    SELF_PLACING("Self Placing"),
    LABORATORY("Laboratory"),
    FUNDAMENTALS("Fundamentals");

    private final String val;

    TrainingName(String val) {
        this.val = val;
    }

    public static TrainingName getTrainingNameFromString(String val) {
        return Arrays.stream(TrainingName.values())
                .filter(trainingName -> trainingName.getVal().equals(val))
                .findFirst().orElseThrow(()
                        -> new NotFoundException("Training name not found in list"));
    }

}
