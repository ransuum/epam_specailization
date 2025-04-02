package org.epam.utils;

import lombok.extern.log4j.Log4j2;
import org.epam.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.UUID;

@Component
@Log4j2
public class CredentialsGenerator {
    private final SecureRandom random = new SecureRandom();
    private final UserRepository userRepository;
    private static final byte PASSWORD_LENGTH = 10;

    public CredentialsGenerator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generatePassword(String username) {
        String hash = UUID.nameUUIDFromBytes(username.getBytes()).toString();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(hash.length());
            password.append(hash.charAt(index));
        }

        log.info("Generated password.....");
        return password.toString();
    }

    public String generateUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        int serial = 0;
        String newUsername = baseUsername;

        while (userRepository.existsByUsername(newUsername)) {
            serial++;
            newUsername = baseUsername + serial;
        }

        log.info("Generated username.....");

        return newUsername;
    }
}
