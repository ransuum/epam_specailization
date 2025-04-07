package org.epam.service;


import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;
import org.epam.models.SecurityContextHolder;

public interface AuthenticationService {
    SecurityContextHolder authenticate(String username, String password) throws NotFoundException, CredentialException;
    void logout();
}
