package org.epam.models.enums;

import lombok.Getter;

@Getter
public enum NotFoundMessages {
    TRAINER("Trainer Not Found"),
    TRAINEE("Trainee Not Found"),
    TRAINING_TYPE("Training Type Not Found"),
    TRAINING("Training Not Found");

    private final String val;

    NotFoundMessages(String val) {
        this.val = val;
    }
}
