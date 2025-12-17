package com.todolist.Controllers;

import io.micronaut.context.annotation.Property;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
@MicronautTest(environments = "controller-test")
@Property(name = "micronaut.security.enabled", value = "false")
class TaskManagementControllerLogicTest {

    @Test
    void createTask() {
    }

    @Test
    void getTasks() {
    }

    @Test
    void getTaskWithId() {
    }

    @Test
    void updateTask() {
    }

    @Test
    void deleteTask() {
    }
}