package org.epam.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
@RequiredArgsConstructor
@Log4j2
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final SecurityContextHolder securityContextHolder;

    @Override
    public void authenticate(String username, String password) throws NotFoundException, CredentialException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CredentialException("Username or password is incorrect"));

        if (!user.getPassword().equals(password))
            throw new CredentialException("Password or username doesn't match");

        try {
            var trainee = traineeRepository.findById(user.getId())
                    .orElseThrow(() -> new CredentialException("User doesn't exist or cannot authorize by this credentials"));
            if (trainee != null) {
                securityContextHolder.newBuild(new SecurityContextHolder(
                        username, trainee.getId(),
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(12),
                        UserType.TRAINEE)
                );
                return;
            }
        } catch (Exception e) {
            log.info("You are not trainee");
        }

        var trainer = trainerRepository.findById(user.getId())
                .orElseThrow(() -> new CredentialException("User doesn't exist or cannot authorize by this credentials"));
        if (trainer != null) {
            securityContextHolder.newBuild(new SecurityContextHolder(
                    username, trainer.getId(),
                    LocalDateTime.now(),
                    LocalDateTime.now().plusDays(12),
                    UserType.TRAINER)
            );
            return;
        }

        throw new CredentialException("User role could not be determined");
    }

    @Override
    public void logout() {
        this.securityContextHolder.setUsername(null);
        this.securityContextHolder.setUserId(null);
        this.securityContextHolder.setGenerateAt(null);
        this.securityContextHolder.setExpiredAt(null);
        this.securityContextHolder.setUserType(UserType.NOT_AUTHORIZE);
    }
}
