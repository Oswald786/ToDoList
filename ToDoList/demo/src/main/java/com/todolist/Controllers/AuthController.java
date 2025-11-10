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

    private Logger log = LoggerFactory.getLogger(AuthController.class);

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Post("/register")
    public void register(@Body UserDetailsModel userDetailsModel){
        System.out.println("Registering user");
        //checkers to ensure the user is valid
        if (userDetailsModel.getUserName() == null || userDetailsModel.getUserName().isBlank()){
            throw new IllegalArgumentException("Username cannot be null or blank");
        }else if(userDetailsModel.getPassword() == null || userDetailsModel.getPassword().isBlank()){
            throw new IllegalArgumentException("Password cannot be null or blank");
        }else if(userDetailsModel.getPassword().length() < 8){
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        registrationService.register(userDetailsModel);


        //turn the data object model into an entity

        //persist the entity into the table

        //give confirmation
    }

//    @Post("/login")
//    public AuthenticationResponse login(@Body HttpRequest<?> request){
//        //attempt to log in using authentication service
//        try{
//            LoginRequestModel loginRequestModel = request.getBody().orElseThrow(() -> new HttpStatusException(HttpStatus.BAD_REQUEST,"Missing the valid request parameters"));
//
//            if(loginRequestModel.getUsername() == null || loginRequestModel.getPassword() == null){
//                throw new IllegalArgumentException("Username and password cannot be null");
//            }
//
//            AuthenticationRequest<String,String> authRequest = new AuthenticationRequest<>() {
//                @Override
//                public String getIdentity() {
//                    return loginRequestModel.getUsername();
//                }
//
//                @Override
//                public String getSecret() {
//                    return loginRequestModel.getPassword();
//                }
//            };
//
//            return authenticationService.authenticate(request,authRequest);
//        }catch (Exception e){
//            log.warn(String.valueOf(e.getCause()));
//            log.warn("Login failed");
//            return AuthenticationResponse.failure();
//        }


    }

