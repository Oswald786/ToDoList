package com.todolist.auth;

import com.todolist.Models.UserDetailsModel;
import com.todolist.Services.GameService;
import com.todolist.adaptors.persistence.Jpa.UserEntity;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @Inject
    GameService gameService;
    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);


    public void register(UserDetailsModel userDetailsModelProvided){
        //hash password
        String hashedPassword = passwordHasher.hashPassword(userDetailsModelProvided.getPassword());
        if (hashedPassword == null || hashedPassword.equals(userDetailsModelProvided.getPassword())) {
            log.error("Password could not be hashed");
            throw new IllegalArgumentException("Password could not be hashed");
        }

        //save user
        try {
            UserDetailsModel userToAdd = userDetailsModelProvided;
            userToAdd.setPassword(hashedPassword);
            System.out.println("===== USER REGISTRATION DEBUG =====");
            System.out.println("Username: " + userToAdd.getUserName());
            System.out.println("Email: " + userToAdd.getEMAIL());
            System.out.println("Role (before set): " + userToAdd.getRole());
            System.out.println("Hashed Password: " + hashedPassword);
            System.out.println("===================================");
            UserEntity exsistingUser = null;




            if (exsistingUser != null) {
                log.error("User with username ${userToAdd.getUserName()} already exists");
                throw new IllegalArgumentException("User with username " + userToAdd.getUserName() + " already exists");
            }
            userToAdd.setRole("USER");


            authAdaptorService.createUser(userToAdd);
//            gameService.createPlayerStatsProfile(userToAdd);
            //confirmation
            System.out.println("User registered");
        }catch (Exception e){
            log.warn("Error registering user");
            log.error(String.valueOf(e.getCause()));
            throw new IllegalArgumentException("Error registering user");
        }
    }
}
