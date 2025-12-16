package com.todolist.Controllers;

import com.todolist.Models.PlayerStatsModel;
import com.todolist.Services.GameService;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;

@MicronautTest(environments = "controller-test")
class GamePointsControllerSecurityTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    GameService gameService;


    @BeforeAll
    static void setup() {
        System.setProperty("micronaut.security.enabled", "true");
    }

    /* -------------------------------------------------
       SECURITY TESTS
       ------------------------------------------------- */

    @Test
    @DisplayName("Unauthenticated user cannot retrieve player stats")
    void retrievePlayerStatsWithoutAuthIsUnauthorized() {

        HttpRequest<?> request =
                HttpRequest.GET("/v1GamePoints/RetrievePlayerStats");

        HttpClientResponseException exception =
                Assertions.assertThrows(
                        HttpClientResponseException.class,
                        () -> client.toBlocking().exchange(request, PlayerStatsModel.class)
                );

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        Mockito.verifyNoInteractions(gameService);
    }
}