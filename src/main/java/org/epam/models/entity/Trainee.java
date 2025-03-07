package org.epam.models.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.*;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class Trainee extends User {
    private LocalDate dateOfBirth;
    private String address;
    private static final AtomicInteger counter = new AtomicInteger(1);

    public Trainee(String address, LocalDate dateOfBirth, String firstName, String lastName, String username, String password, Boolean isActive) {
        super(counter.getAndIncrement(), firstName, lastName, username, password, isActive);
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

    public Trainee(Integer id, String address, LocalDate dateOfBirth, String firstName, String lastName, String username, String password, Boolean isActive) {
        super(id, firstName, lastName, username, password, isActive);
        this.address = address;
        this.dateOfBirth = dateOfBirth;
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
