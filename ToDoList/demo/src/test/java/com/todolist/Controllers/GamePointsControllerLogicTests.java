package com.todolist.Controllers;

import com.todolist.Models.PlayerStatsModel;
import com.todolist.Models.TaskObjectModel;
import com.todolist.Services.GameService;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
@MicronautTest(environments = "controller-test")
@Property(name = "micronaut.security.enabled", value = "false")
public class GamePointsControllerLogicTests {


    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    GameService gameService;

    private Authentication mockAuth;

    @BeforeEach
    void setup() {
        Mockito.reset(gameService);

        // Set up the fake mock authentication
        mockAuth = Mockito.mock(Authentication.class);
        Mockito.when(mockAuth.getName()).thenReturn("TestUser");
        Mockito.when(mockAuth.getRoles()).thenReturn(java.util.List.of("USER"));
        Mockito.when(mockAuth.getAttributes()).thenReturn(java.util.Collections.emptyMap());
    }


    /* -------------------------------------------------
       HAPPY PATH TESTS (NO SECURITY)
       ------------------------------------------------- */

    @Test
    @DisplayName("Retrieve player stats returns stats from service")
    void retrievePlayerStatsReturnsPlayerStats() {

        // Arrange
        PlayerStatsModel mockStats = new PlayerStatsModel();
        mockStats.setPlayerLevel(5);
        mockStats.setPlayerXp(1200);

        Mockito.when(gameService.getPlayerStats(ArgumentMatchers.isNull()))
                .thenReturn(mockStats);


        HttpRequest<?> request =
                HttpRequest.GET("/v1GamePoints/RetrievePlayerStats");

        // Act
        HttpResponse<PlayerStatsModel> response =
                client.toBlocking().exchange(request, PlayerStatsModel.class);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatus());
        Assertions.assertNotNull(response.body());
        Assertions.assertEquals(5, response.body().getPlayerLevel());
        Assertions.assertEquals(1200, response.body().getPlayerXp());

    }

    @Test
    @DisplayName("Add XP calls service for task completion")
    void addXpCallsService() {

        // Arrange
        TaskObjectModel task = new TaskObjectModel();
        task.setId(1L);
        task.setTaskLevel("50");

        HttpRequest<TaskObjectModel> request =
                HttpRequest.POST("/v1GamePoints/AddXP", task);

        Mockito.doNothing().when(gameService)
                .addXPForTaskCompletion(any(TaskObjectModel.class), any());


        // Act
        HttpResponse<?> response =
                client.toBlocking().exchange(request);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatus());
        Mockito.verify(gameService)
                .addXPForTaskCompletion(any(TaskObjectModel.class), ArgumentMatchers.isNull());
    }
}
