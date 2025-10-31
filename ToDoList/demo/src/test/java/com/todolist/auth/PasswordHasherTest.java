package com.todolist.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {

    PasswordHasher passwordHasher = new PasswordHasher();

    @Test
    @DisplayName("hashPassword should return a non-null, non-equal hashed string")
    void hashPassword() {
        // Arrange
        String rawPassword = "SuperSecret123!";

        // Act

        String hashedPassword = passwordHasher.hashPassword(rawPassword);

        // Assert
        assertNotNull(hashedPassword);
        assertNotEquals(rawPassword, hashedPassword, "Hashed password should not match the plain password");
        assertTrue(hashedPassword.startsWith("$2"), "Hashed password should be a valid BCrypt hash");
    }

    @Test
    @DisplayName("checkPassword should return true for valid password match")
    void checkPassword_valid() {
        // Arrange
        String rawPassword = "SuperSecret123!";
        String hashedPassword = passwordHasher.hashPassword(rawPassword);

        // Act
        boolean result = passwordHasher.checkPassword(rawPassword, hashedPassword);

        // Assert
        assertTrue(result, "PasswordHasher should validate correct password successfully");
    }

    @Test
    @DisplayName("checkPassword should return false for invalid password")
    void checkPassword_invalid() {
        // Arrange
        String rawPassword = "SuperSecret123!";
        String wrongPassword = "NotTheSame123!";
        String hashedPassword = passwordHasher.hashPassword(rawPassword);

        // Act
        boolean result = passwordHasher.checkPassword(wrongPassword, hashedPassword);

        // Assert
        assertFalse(result, "PasswordHasher should return false for incorrect passwords");
    }
}