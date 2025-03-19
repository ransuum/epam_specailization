package org.epam.service.impl;

import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epam.exception.CredentialException;
import org.epam.models.SecurityContextHolder;
import org.epam.models.enums.UserType;
import org.epam.service.AuthenticationService;
import org.epam.service.TraineeService;
import org.epam.service.TrainerService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final Logger log = LogManager.getLogger(AuthenticationServiceImpl.class);
    private final TraineeService traineeService;
    private final TrainerService trainerService;

    public AuthenticationServiceImpl(TraineeService traineeService, TrainerService trainerService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    @Override
    public SecurityContextHolder authenticate(String username, String password) {
        var byUsername = traineeService.findByUsername(username);
        if (byUsername == null) {
            var byUsername1 = trainerService.findByUsername(username);
            if (byUsername1.user().password().equals(password))
                return new SecurityContextHolder(username, byUsername1.id(), LocalDateTime.now(), LocalDateTime.now().plusDays(12), UserType.TRAINER);
            else throw new CredentialException("Password or username doesn't match");
        }

        if (byUsername.user().password().equals(password))
            return new SecurityContextHolder(username, byUsername.id(), LocalDateTime.now(), LocalDateTime.now().plusDays(12), UserType.TRAINEE);

        throw new CredentialException("Password or username doesn't match");
    }
}
