package com.example.androidproject.util;

import android.util.Patterns;
import java.util.regex.Pattern;

/**
 * Utility class for validating user input such as email, password, and username.
 */
public class ValidationUtils {

    // Email pattern validation
    public static boolean isValidEmail(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Password strength validation
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

    public static boolean isStrongPassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    // Username validation - letters, numbers, periods
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9.]{6,30}$");

    public static boolean isValidUsername(String username) {
        return USERNAME_PATTERN.matcher(username).matches();
    }
}