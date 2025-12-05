package com.todolist.Controllers;


import com.todolist.Models.UserDetailsModel;
import com.todolist.auth.RegistrationService;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/v1Authentication")
public class AuthController {


    @Inject
    RegistrationService registrationService;

    private final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Post("/register")
    public void register(@Body UserDetailsModel userDetailsModel){
        System.out.println("Registering user");
        //checkers to ensure the user is valid
        if (userDetailsModel.getUsername() == null || userDetailsModel.getUsername().isBlank()){
            throw new IllegalArgumentException("Username cannot be null or blank");
        }else if(userDetailsModel.getPassword() == null || userDetailsModel.getPassword().isBlank()){
            throw new IllegalArgumentException("Password cannot be null or blank");
        }else if(userDetailsModel.getPassword().length() < 8){
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        registrationService.register(userDetailsModel);
    }
    }

