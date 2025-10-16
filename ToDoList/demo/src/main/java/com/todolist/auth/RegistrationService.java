package com.todolist.auth;

import com.todolist.adaptors.persistence.jpa.userEntity;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;


public class RegistrationService {
    private static PasswordHasher passwordHasher;

    @Inject
    EntityManager entityManager;


    public String register(String username, String password,String email){
        //hash password

        String hashedPassword = passwordHasher.hashPassword(password);

        //save user
        userEntity user = new userEntity();
        user.setUserName(username);
        user.setPassword(hashedPassword);
        user.setRole("user");
        user.setEMAIL(email);
        entityManager.persist(user);


        //confirmation
        System.out.println("User registered");

        return hashedPassword;
    }
}
