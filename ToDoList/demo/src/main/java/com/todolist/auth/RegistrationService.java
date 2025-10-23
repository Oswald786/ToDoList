package com.todolist.auth;

import com.todolist.Models.userDetailsModel;
import com.todolist.Services.TaskManagmentService;
import com.todolist.adaptors.persistence.jpa.userEntity;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class RegistrationService {
    @Inject
    private PasswordHasher passwordHasher;

    @Inject
    EntityManager entityManager;

    @Inject
    AuthAdaptorService authAdaptorService;

    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);


    public void register(userDetailsModel userDetailsModelProvided){
        //hash password
        String hashedPassword = passwordHasher.hashPassword(userDetailsModelProvided.getPassword());

        //save user
        try {
            userDetailsModel userToAdd = userDetailsModelProvided;
            userToAdd.setPassword(hashedPassword);
            authAdaptorService.createUser(userToAdd);
            //confirmation
            System.out.println("User registered");
        }catch (Exception e){
            e.printStackTrace();
            log.error(String.valueOf(e.getCause()));
            throw new IllegalArgumentException("User with username " + userDetailsModelProvided.getUserName() + " already exists");
        }

    }
}
