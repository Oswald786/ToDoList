package com.todolist.auth;

import com.todolist.Models.UserDetailsModel;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.HttpRequestAuthenticationProvider;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Singleton
public class AuthenticationService<B> implements HttpRequestAuthenticationProvider<B>{

//    Register → hash password → save user.
//
//    Login → verify password → generate JWT.
//
//    Protected route → check isAuthenticated → allow or deny.

    @Inject
    PasswordHasher passwordHasher;

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    AuthAdaptorService authAdaptorService;



    @Override
    public @NonNull AuthenticationResponse authenticate(@Nullable HttpRequest<B> httpRequest, @NonNull AuthenticationRequest<String, String> authRequest) {
        try{
            System.out.println("Authenticating user");
            //Get the Authentication Information
            String username = authRequest.getIdentity();
            System.out.println("Authenticating user");
            String password = authRequest.getSecret();


            //Find the user based on the username provided
            System.out.println("Authenticating user");
            UserDetailsModel user = authAdaptorService.findUser(username);
            System.out.println("Authenticating user");

            //Check if user exists
            if(user == null){
                System.out.println("Authenticating user is null");
                return AuthenticationResponse.failure();
            }

            //Check the password provided matches the hashed password stored in the database
            if(passwordHasher.checkPassword(password, user.getPassword())){
                //If the password matches then return a success response
                System.out.println("Authenticating user success");
                return AuthenticationResponse.success(username, List.of(user.getRole()));
            }else{
                //If the password does not match then return a failure response
                System.out.println("Authenticating user failure");
                System.out.println("passeword given: " + password);
                System.out.println("password stored: " + user.getPassword());
                return AuthenticationResponse.failure();
            }
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Authenticating user failure catch block");
            return AuthenticationResponse.failure();
        }
    }
}

