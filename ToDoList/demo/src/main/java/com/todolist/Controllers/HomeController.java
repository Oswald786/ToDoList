package com.todolist.Controllers;

import com.todolist.Models.UserDetailsModel;
import com.todolist.Services.GameService;
import com.todolist.auth.RegistrationService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/v1Home")
public class HomeController {

    @Inject
    RegistrationService registrationService;

    @Inject
    GameService gameService;

    private final Logger log = LoggerFactory.getLogger(HomeController.class);

    @Post("/v1Register")
    public HttpResponse<?> register(@Body UserDetailsModel userDetailsModel){
        System.out.println("Received register request");
        System.out.println("Body: " + userDetailsModel.getUserName() + " | " + userDetailsModel.getEMAIL());
        registrationService.register(userDetailsModel);
        log.info("User registered successfully");
        log.info("Creating player stats profile for user");
        gameService.createPlayerStatsProfile(userDetailsModel);
        return HttpResponse.ok("User registered successfully");
    }

}

//Security Side to be worked on down the line
//https://guides.micronaut.io/latest/micronaut-security-jwt-gradle-java.html