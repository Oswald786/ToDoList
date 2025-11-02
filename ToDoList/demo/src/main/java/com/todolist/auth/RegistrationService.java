package com.todolist.auth;

import com.todolist.Models.userDetailsModel;
import com.todolist.Services.TaskManagmentService;
import com.todolist.adaptors.persistence.jpa.userEntity;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
@Getter
@Setter
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
        if (hashedPassword == null || hashedPassword.equals(userDetailsModelProvided.getPassword())) {
            log.error("Password could not be hashed");
            throw new IllegalArgumentException("Password could not be hashed");
        }

        //save user
        try {
            userDetailsModel userToAdd = userDetailsModelProvided;
            userToAdd.setPassword(hashedPassword);
            userEntity userEntityInUseAlready = entityManager.find(userEntity.class, userToAdd.getUserName());
            if (userEntityInUseAlready != null) {
                log.error("User with username ${userToAdd.getUserName()} already exists");
                throw new IllegalArgumentException("User with username " + userToAdd.getUserName() + " already exists");
            }

            authAdaptorService.createUser(userToAdd);
            //confirmation
            System.out.println("User registered");
        }catch (Exception e){
            log.warn("Error registering user");
            log.error(String.valueOf(e.getCause()));
            throw new IllegalArgumentException("Error registering user");
        }
    }
}
