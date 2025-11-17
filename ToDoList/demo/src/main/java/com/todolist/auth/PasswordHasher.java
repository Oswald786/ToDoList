package com.todolist.auth;

import jakarta.inject.Singleton;
import org.springframework.security.crypto.bcrypt.*;
@Singleton
public class PasswordHasher {

    private static final BCryptPasswordEncoder passwordHasher = new BCryptPasswordEncoder();

    public String hashPassword(String password){
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        password = passwordHasher.encode(password);
        return password;
    }

    public boolean checkPassword(String password, String hashedPassword){
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }else if(hashedPassword == null || hashedPassword.isEmpty()){
            throw new IllegalArgumentException("Hashed password cannot be null or empty");
        }
        return passwordHasher.matches(password, hashedPassword);
    }
}
