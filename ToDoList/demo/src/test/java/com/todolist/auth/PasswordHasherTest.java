package com.todolist.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {

    PasswordHasher passwordHasher = new PasswordHasher();

    @Test
    @DisplayName("Throws illegal argument exception when apssword is null or empty")
    void hashPassword_throws_When_Password_Null() {
        //Arrange
        String rawPassword = "";

        //Act + Assert
        assertThrows(IllegalArgumentException.class, () -> passwordHasher.hashPassword(rawPassword));
    }

    @Test
    @DisplayName("Throws illegal argument exception when password is empty")
    void hashPassword_throws_When_Password_Empty() {
        //Arrange
        String rawPassword = "";

        //Act + Assert
        assertThrows(IllegalArgumentException.class, () -> passwordHasher.hashPassword(rawPassword));
    }

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

    @Test
    @DisplayName("Throws illegal argument exception when checking password if password null")
    void checkPassword_throws_When_Password_Null() {
        //Arrange
        String rawPassword = null;
        String hashedPassword = "fakehashedpassword";

        //Act + Assert
        assertThrows(IllegalArgumentException.class, () -> passwordHasher.checkPassword(rawPassword, hashedPassword));
    }

    @Test
    @DisplayName("Throws illegal argument exception when checking password if password null")
    void checkPassword_throws_When_Password_Empty() {
        //Arrange
        String rawPassword = "";
        String hashedPassword = "fakehashedpassword";

        //Act + Assert
        assertThrows(IllegalArgumentException.class, () -> passwordHasher.checkPassword(rawPassword, hashedPassword));
    }

    @Test
    @DisplayName("Throws illegal argument exception when checking password if hashed password empty")
    void checkPassword_throws_When_Hashed_Password_Empty() {
        //Arrange
        String rawPassword = "fakepassword";
        String hashedPassword = "";

        //Act + Assert
        assertThrows(IllegalArgumentException.class, () -> passwordHasher.checkPassword(rawPassword, hashedPassword));
    }

    @Test
    @DisplayName("Throws illegal argument exception when checking password if hashed password null")
    void checkPassword_throws_When_Hashed_Password_Null() {
        //Arrange
        String rawPassword = "fakepassword";
        String hashedPassword = null;

        //Act + Assert
        assertThrows(IllegalArgumentException.class, () -> passwordHasher.checkPassword(rawPassword, hashedPassword));
    }

}