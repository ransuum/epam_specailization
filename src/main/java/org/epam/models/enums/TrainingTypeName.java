package org.epam.models.enums;

import lombok.Getter;
import org.epam.exception.NotFoundException;

import java.util.Arrays;

@Getter
public enum TrainingTypeName {
    SELF_PLACING("Self Placing"),
    LABORATORY("Laboratory"),
    FUNDAMENTALS("Fundamentals");

    private final String val;

    TrainingTypeName(String val) {
        this.val = val;
    }

    public static TrainingTypeName getTrainingNameFromString(String val) {
        return Arrays.stream(TrainingTypeName.values())
                .filter(trainingName -> trainingName.getVal().equals(val))
                .findFirst().orElseThrow(()
                        -> new NotFoundException("Training name not found in list"));
    }
}
