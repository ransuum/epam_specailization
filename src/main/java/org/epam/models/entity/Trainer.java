package org.epam.models.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

    public Trainer(Integer id, String specialization, String firstName, String lastName, String username, String password, Boolean isActive) {
        super(id, firstName, lastName, username, password, isActive);
        this.specialization = specialization;
    }

    public Trainer(boolean update, String specialization, String firstName, String lastName, String username, String password, Boolean isActive) {
        super(firstName, lastName, username, password, isActive);
        this.specialization = specialization;
    }



    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
