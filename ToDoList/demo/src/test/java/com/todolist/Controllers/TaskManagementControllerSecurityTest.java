package com.todolist.Controllers;

import com.todolist.Models.PlayerStatsModel;
import com.todolist.Models.TaskObjectModel;
import com.todolist.Models.UpdateTaskRequestPackage;
import com.todolist.Services.GameService;
import com.todolist.Services.TaskManagementService;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@MicronautTest(environments = "controller-test")
public class TaskManagementControllerSecurityTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    TaskManagementService taskManagementService;


    @BeforeAll
    static void setup() {
        System.setProperty("micronaut.security.enabled", "true");
    }

    @Test
    @DisplayName("Unauthenticated user cannot create Tasks")
    void createTaskWithoutAuthIsUnauthorized() {
        HttpRequest<?> request =
                HttpRequest.GET("/v1taskManagementController/createTask");

        HttpClientResponseException exception =
                Assertions.assertThrows(
                        HttpClientResponseException.class,
                        () -> client.toBlocking().exchange(request)
                );

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        Mockito.verifyNoInteractions(taskManagementService);
    }

    @Test
    @DisplayName("Unauthenticated user cannot fetch all tasks")
    void fetchAllTasksWithoutAuthIsUnauthorized() {
        HttpRequest<?> request = HttpRequest.GET("/v1taskManagementController/getTasks");

        HttpClientResponseException exception = Assertions.assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, TaskObjectModel[].class)
        );

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        Mockito.verifyNoInteractions(taskManagementService);
    }

    @Test
    @DisplayName("Unauthenticated user cannot fetch task with id")
    void fetchTaskWithIdWithoutAuthIsUnauthorized() {
        HttpRequest<?> request = HttpRequest.GET("/v1taskManagementController/getTaskWithId/1");

        HttpClientResponseException exception = Assertions.assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request,TaskObjectModel.class)
        );

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        Mockito.verifyNoInteractions(taskManagementService);
    }

    @Test
    @DisplayName("Unauthenticated user cannot update task")
    void updateTaskWithoutAuthIsUnauthorized() {
        HttpRequest<?> request = HttpRequest.POST("/v1taskManagementController/updateTask", new UpdateTaskRequestPackage());

        HttpClientResponseException exception = Assertions.assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request)
        );

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        Mockito.verifyNoInteractions(taskManagementService);
    }

    @Test
    @DisplayName("Unauthenticated user cannot delete task")
    void deleteTaskWithoutAuthIsUnauthorized() {
        HttpRequest<?> request = HttpRequest.DELETE("/v1taskManagementController/deleteTask?id=1");

        HttpClientResponseException exception = Assertions.assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request)
        );

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        Mockito.verifyNoInteractions(taskManagementService);
    }

}
