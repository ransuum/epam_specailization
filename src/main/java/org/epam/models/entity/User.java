package org.epam.models.entity;

import lombok.Data;

@Data
public abstract class User {
    private Integer id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private Boolean isActive;

    public User(Integer id, String firstName, String lastName, String username, String password, Boolean isActive) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.isActive = isActive;
    }
}
