package org.epam.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

import static org.epam.util.subcontroller.SubControllerMenu.existingUsernames;

public class CredentialsGenerator {
    private final static SecureRandom random = new SecureRandom();
    private static final Log log = LogFactory.getLog(CredentialsGenerator.class);

    public static String generatePassword(String username) {
        String hash = UUID.nameUUIDFromBytes(username.getBytes()).toString();
        byte PASSWORD_LENGTH = 10;
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(hash.length());
            password.append(hash.charAt(index));
        }

        log.info("Generated password.....");
        return password.toString();
    }

    public static String generateUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        int serial = 0;
        String newUsername = baseUsername;

        while (existingUsernames.contains(newUsername)) {
            serial++;
            newUsername = baseUsername + serial;
        }

        log.info("Generated username.....");

        return newUsername;
    }
}
