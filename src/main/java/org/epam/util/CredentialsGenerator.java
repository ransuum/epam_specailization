package org.epam.util;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Set;
import java.util.UUID;

@Service
public class CredentialsGenerator {
    private final static SecureRandom random = new SecureRandom();

    public static String generatePassword(String username) {
        String hash = UUID.nameUUIDFromBytes(username.getBytes()).toString();
        byte PASSWORD_LENGTH = 10;
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(hash.length());
            password.append(hash.charAt(index));
        }
        return password.toString();
    }

    public static String generateUsername(Set<String> existingUsernames, String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        if (!existingUsernames.contains(baseUsername)) return baseUsername;

        int serial = 1;
        String newUsername;
        do {
            newUsername = baseUsername + serial;
            serial++;
        } while (existingUsernames.contains(newUsername));
        return newUsername;
    }
}
