package org.epam.service;


import org.epam.models.SecurityContextHolder;

public interface AuthenticationService {
    SecurityContextHolder authenticate(String username, String password);
}
