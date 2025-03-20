package org.epam.service.impl;

import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;
import org.epam.models.SecurityContextHolder;
import org.epam.models.enums.UserType;
import org.epam.service.AuthenticationService;
import org.epam.service.TraineeService;
import org.epam.service.TrainerService;
import org.epam.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final UserService userService;

    public AuthenticationServiceImpl(TraineeService traineeService, TrainerService trainerService, UserService userService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.userService = userService;
    }

    @Override
    public SecurityContextHolder authenticate(String username, String password) throws NotFoundException, CredentialException {
        var user = userService.findByUsername(username);
        if (user == null)
            throw new CredentialException("User with this username does not exist");

        if (!user.password().equals(password))
            throw new CredentialException("Password or username doesn't match");

        var trainee = traineeService.findById(user.id());
        if (trainee != null)
            return new SecurityContextHolder(username, trainee.id(), LocalDateTime.now(), LocalDateTime.now().plusDays(12), UserType.TRAINEE);

        var trainer = trainerService.findById(user.id());
        if (trainer != null)
            return new SecurityContextHolder(username, trainer.id(), LocalDateTime.now(), LocalDateTime.now().plusDays(12), UserType.TRAINER);

        throw new CredentialException("User role could not be determined");
    }
}
