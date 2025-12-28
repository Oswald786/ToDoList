package com.todolist;

import com.todolist.Services.GameService;
import com.todolist.Services.TaskManagementService;
import com.todolist.auth.RegistrationService;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.test.annotation.MockBean;
import jakarta.inject.Singleton;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Replace;
import org.mockito.Mockito;

@Factory
@Requires(env = "controller-test")
public class TestMockFactory {

    @Singleton
    @Replaces(GameService.class)
    GameService gameService() {
        return Mockito.mock(GameService.class);
    }

    @Singleton
    @Replaces(RegistrationService.class)
    RegistrationService registrationService() {
        return Mockito.mock(RegistrationService.class);
    }

    @Singleton
    @Replaces(TaskManagementService.class)
    TaskManagementService TaskManagmentService(){
        return Mockito.mock(TaskManagementService.class);
    }
}
