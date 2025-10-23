package com.todolist.auth;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.springframework.security.crypto.bcrypt.*;
@Singleton
public class PasswordHasher {

    private static final BCryptPasswordEncoder passwordHasher = new BCryptPasswordEncoder();

    public String hashPassword(String password){
        password = passwordHasher.encode(password);
        System.out.println(password);
        return password;
    }

    public boolean checkPassword(String password, String hashedPassword){
        System.out.println(hashedPassword);
        return passwordHasher.matches(password, hashedPassword);
    }
}
