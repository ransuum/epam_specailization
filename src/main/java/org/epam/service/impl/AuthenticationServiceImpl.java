package org.epam.service.impl;

import org.epam.exception.CredentialException;
import org.epam.exception.NotFoundException;
import org.epam.models.SecurityContextHolder;
import org.epam.models.enums.UserType;
import org.epam.repository.TraineeRepository;
import org.epam.repository.TrainerRepository;
import org.epam.repository.UserRepository;
import org.epam.service.AuthenticationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public AuthenticationServiceImpl(UserRepository userRepository, TraineeRepository traineeRepository, TrainerRepository trainerRepository) {
        this.userRepository = userRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
    }

    @Override
    public SecurityContextHolder authenticate(String username, String password) throws NotFoundException, CredentialException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CredentialException("Username or password is incorrect"));

        if (!user.getPassword().equals(password))
            throw new CredentialException("Password or username doesn't match");

        var trainee = traineeRepository.findById(user.getId())
                .orElseThrow(() -> new CredentialException("User doesn't exist or cannot authorize by this credentials"));
        if (trainee != null)
            return new SecurityContextHolder(username, trainee.getId(), LocalDateTime.now(), LocalDateTime.now().plusDays(12), UserType.TRAINEE);

        var trainer = trainerRepository.findById(user.getId())
                .orElseThrow(() -> new CredentialException("User doesn't exist or cannot authorize by this credentials"));
        if (trainer != null)
            return new SecurityContextHolder(username, trainer.getId(), LocalDateTime.now(), LocalDateTime.now().plusDays(12), UserType.TRAINER);

        throw new CredentialException("User role could not be determined");
    }
}
