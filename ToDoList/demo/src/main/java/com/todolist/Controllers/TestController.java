package com.todolist.Controllers;

import com.todolist.exceptions.*;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Controller("/debug")
@Secured(SecurityRule.IS_ANONYMOUS)
public class TestController {

    @Get("/runtime")
    public String runtimeException() {
        throw new RuntimeException("Fake runtime exception");
    }

    @Get("/task-not-found")
    public String taskNotFound() {
        throw new TaskNotFoundException("Task not found");
    }

    @Get("/player-stats-not-found")
    public String playerStatsNotFound() {
        throw new PlayerStatsNotFoundException("Player stats not found");
    }

    @Get("/user-not-found")
    public String userNotFound() {
        throw new UserNotFoundException("User not found");
    }

    @Get("/user-already-exists")
    public String userAlreadyExists() {
        throw new UserAlreadyExistsException("User already exists");
    }

    @Get("/permission-denied")
    public String permissionDenied() {
        throw new PermissionDeniedException("Permission denied");
    }

    @Get("/player-username-missing")
    public String playerUsernameMissing() {
        throw new PlayerUsernameNotProvidedException("Player username not provided");
    }

    @Get("/mapper-failed")
    public String mapperFailed() {
        throw new MapperFailedException("Mapper failed during conversion");
    }
}
