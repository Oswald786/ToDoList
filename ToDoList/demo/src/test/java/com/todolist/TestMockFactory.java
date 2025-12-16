package com.todolist;

import com.todolist.Services.GameService;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.test.annotation.MockBean;
import jakarta.inject.Singleton;
import org.mockito.Mockito;

@Factory
@Requires(env = "controller-test")
public class TestMockFactory {

    @Singleton
    @Replaces(GameService.class)
    GameService gameService() {
        return Mockito.mock(GameService.class);
    }
}
