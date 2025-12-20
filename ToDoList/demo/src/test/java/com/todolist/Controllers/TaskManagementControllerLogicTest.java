package com.todolist.Controllers;

import com.todolist.Models.TaskObjectModel;
import com.todolist.Models.UpdateTaskRequestPackage;
import com.todolist.Services.TaskManagementService;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@MicronautTest(environments = "controller-test")
@Property(name = "micronaut.security.enabled", value = "false")
class TaskManagementControllerLogicTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    TaskManagementService taskManagementService;

    private Authentication mockAuth;

    @BeforeEach
    void setup() {
        Mockito.reset(taskManagementService);

        // Set up the fake mock authentication
        mockAuth = Mockito.mock(Authentication.class);
        Mockito.when(mockAuth.getName()).thenReturn("TestUser");
        Mockito.when(mockAuth.getRoles()).thenReturn(java.util.List.of("USER"));
        Mockito.when(mockAuth.getAttributes()).thenReturn(java.util.Collections.emptyMap());
    }

    @Test
    @DisplayName("Create task calls service and returns OK")
    void createTask() {
        // Arrange
        TaskObjectModel task = new TaskObjectModel();
        task.setTaskName("New Task");
        task.setTaskType("Chore");
        task.setTaskLevel("9");
        task.setTaskDescription("Task Description");
        task.setTaskOwnerId("TestUser");

        Mockito.doNothing().when(taskManagementService)
                .createTask(any(TaskObjectModel.class), any());

        HttpRequest<TaskObjectModel> request =
                HttpRequest.POST("/v1taskManagementController/createTask", task);

        // Act
        HttpResponse<?> response = client.toBlocking().exchange(request);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatus());
        Mockito.verify(taskManagementService)
                .createTask(any(TaskObjectModel.class), ArgumentMatchers.isNull());
    }

    @Test
    @DisplayName("Get tasks returns list of tasks from service")
    void getTasks() {
        // Arrange
        ArrayList<TaskObjectModel> tasks = new ArrayList<>();

        tasks.add(new TaskObjectModel());

        Mockito.when(taskManagementService.fetchAllTasks(any()))
                .thenReturn(tasks);

        HttpRequest<?> request = HttpRequest.GET("/v1taskManagementController/getTasks");

        // Act
        HttpResponse<List<TaskObjectModel>> response = client.toBlocking().exchange(request, Argument.listOf(TaskObjectModel.class)
        );

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatus());
        Assertions.assertNotNull(response.body());
        Assertions.assertEquals(1, response.body().size());
        Mockito.verify(taskManagementService).fetchAllTasks(ArgumentMatchers.isNull());
    }

    @Test
    @DisplayName("Get task with id returns specific task from service")
    void getTaskWithId() {
        // Arrange
        TaskObjectModel task = new TaskObjectModel();
        task.setId(1L);

        Mockito.when(taskManagementService.fetchTaskWithId(anyLong(), any()))
                .thenReturn(task);

        HttpRequest<?> request = HttpRequest.GET("/v1taskManagementController/getTaskWithId/1");

        // Act
        HttpResponse<TaskObjectModel> response = client.toBlocking().exchange(request, TaskObjectModel.class);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatus());
        Assertions.assertNotNull(response.body());
        Assertions.assertEquals(1L, response.body().getId());
        Mockito.verify(taskManagementService).fetchTaskWithId(ArgumentMatchers.eq(1L), ArgumentMatchers.isNull());
    }

    @Test
    @DisplayName("Update task calls service and returns OK")
    void updateTask() {
        // Arrange
        UpdateTaskRequestPackage updatePackage = new UpdateTaskRequestPackage(1L, "New Value", "taskName");

        Mockito.doNothing().when(taskManagementService)
                .updateTask(any(UpdateTaskRequestPackage.class), any());

        HttpRequest<UpdateTaskRequestPackage> request =
                HttpRequest.POST("/v1taskManagementController/updateTask", updatePackage);

        // Act
        HttpResponse<?> response = client.toBlocking().exchange(request);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatus());
        Mockito.verify(taskManagementService)
                .updateTask(any(UpdateTaskRequestPackage.class), ArgumentMatchers.isNull());
    }

    @Test
    @DisplayName("Delete task calls service and returns OK")
    void deleteTask() {
        // Arrange
        Mockito.doNothing().when(taskManagementService).deleteTask(anyLong(), any());

        HttpRequest<?> request = HttpRequest.DELETE("/v1taskManagementController/deleteTask?id=1");

        // Act
        HttpResponse<?> response = client.toBlocking().exchange(request);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatus());
        Mockito.verify(taskManagementService).deleteTask(ArgumentMatchers.eq(1L), ArgumentMatchers.isNull());
    }

}