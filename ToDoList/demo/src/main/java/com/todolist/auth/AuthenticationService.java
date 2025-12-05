package com.todolist.auth;

import com.todolist.Models.UserDetailsModel;
import com.todolist.exceptions.UserNotFoundException;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.HttpRequestAuthenticationProvider;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);



    @Override
    public @NonNull AuthenticationResponse authenticate(@Nullable HttpRequest<B> httpRequest, @NonNull AuthenticationRequest<String, String> authRequest) {
            log.info("Authenticating user");
            try {
                //Get the Authentication Information
                String username = authRequest.getIdentity();
                String password = authRequest.getSecret();
                log.info("Extracted username: {} and password", username);
                UserDetailsModel user = authAdaptorService.findUser(username);
                log.info("Found user with username: {}", username);

                //Check the password provided matches the hashed password stored in the database
                if (passwordHasher.checkPassword(password, user.getPassword())) {
                    //If the password matches, then return a success response
                    log.info("Authenticating user success");
                    return AuthenticationResponse.success(username, List.of(user.getRole()));
                } else if (!passwordHasher.checkPassword(password, user.getPassword())){
                    //If the password does not match, then return a failure response
                    log.error("Authenticating user failure");
                    return AuthenticationResponse.failure("Invalid credentials");
                }
                return AuthenticationResponse.failure("Invalid credentials");
            }catch (UserNotFoundException e){
                log.error("User not found");
                return AuthenticationResponse.failure("Invalid credentials");
            }
    }
}

