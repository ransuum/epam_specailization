package org.epam.models.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class Trainer extends User {
    private String specialization;

    private static final AtomicInteger counter = new AtomicInteger(1);
    public Trainer(String specialization, String firstName, String lastName, String username, String password, Boolean isActive) {
        super(counter.getAndIncrement(), firstName, lastName, username, password, isActive);
        this.specialization = specialization;
    }

    public Trainer(String specialization, String firstName, String lastName, Boolean isActive) {
        super(firstName, lastName, isActive);
        this.specialization = specialization;
    }
}
