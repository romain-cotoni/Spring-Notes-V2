package com.project.notes_v2.security;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

import java.util.Random;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class PasswordGenerator {

    // Define the characters used for the password generation
    private static final String UPPERCASE          = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE          = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS             = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_=+[]{}|;:,.<>?";
    private static final String ALL_CHARACTERS     = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARACTERS;
    private static final int    PASSWORD_LENGTH    = 12;  // Define password length
    private static final Random RANDOM = new SecureRandom();


    public String generateRandomPassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        // Ensure at least one character from each category
        password.append(UPPERCASE.charAt(RANDOM.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(RANDOM.nextInt(LOWERCASE.length())));
        password.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARACTERS.charAt(RANDOM.nextInt(SPECIAL_CHARACTERS.length())));

        // Fill the remaining characters randomly
        for (int i = 4; i < PASSWORD_LENGTH; i++) {
            password.append(ALL_CHARACTERS.charAt(RANDOM.nextInt(ALL_CHARACTERS.length())));
        }

        return password.toString();
    }

}
