package com.todolist.Controllers;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;

@Secured({"USER"})
@Controller("/test-secured")
public class TestController {

    @Get("/test")
    public String secured(Authentication authentication) {
        // Returns the authenticated user's name, e.g., "sherlock"
        return authentication.getName();
    }
}
