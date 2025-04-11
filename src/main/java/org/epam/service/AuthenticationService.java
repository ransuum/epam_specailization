package org.epam.service;


import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;

public interface AuthenticationService {
    void authenticate(String username, String password) throws NotFoundException, CredentialException;
    void logout();
}
