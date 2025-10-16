package com.todolist.auth;

import com.todolist.adaptors.persistence.jpa.userEntity;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.HttpRequestAuthenticationProvider;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

public class authenticationService implements HttpRequestAuthenticationProvider {

//    Register → hash password → save user.
//
//    Login → verify password → generate JWT.
//
//    Protected route → check isAuthenticated → allow or deny.


    PasswordHasher passwordHasher = new PasswordHasher();

    @Inject
    EntityManager entityManager;


    @Override
    public @NonNull AuthenticationResponse authenticate(Object requestContext, @NonNull AuthenticationRequest authRequest) {
        try{
            //Get the Authentication Information
            String username = authRequest.getIdentity().toString();
            String password = authRequest.getSecret().toString();
            String hashedPassword = passwordHasher.hashPassword(password);

            //Find the suer based on the username provided
            userEntity user = entityManager.createQuery("SELECT u FROM userEntity u WHERE u.userName = :username", userEntity.class)
                    .setParameter("username", username)
                    .getSingleResult();

            //Check the password provided matches the hashed password stored in the database
            if(passwordHasher.checkPassword(hashedPassword, user.getPassword())){
                //If the password matches then return a success response
                return AuthenticationResponse.success(username);
            }else{

                //If the password does not match then return a failure response
                return AuthenticationResponse.failure();
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return AuthenticationResponse.failure();
        }
    }
}
