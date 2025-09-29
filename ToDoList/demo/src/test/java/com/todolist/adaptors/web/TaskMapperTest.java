package com.todolist.adaptors.web;

import com.todolist.Models.taskObjectModel;
import com.todolist.adaptors.persistence.jpa.TaskEntity;
import io.micronaut.test.annotation.MockBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskMapperTest {


    TaskMapper taskMapper = new TaskMapperImpl();



    @Test
    @DisplayName("Tests mapper converts model tasks objects to entity's")
    void toEntity() {
        //Arrange
        taskObjectModel taskObjectModel = new taskObjectModel();
        taskObjectModel.setTaskName("Write unit tests");
        taskObjectModel.setTaskType("Engineering");
        taskObjectModel.setTaskLevel("Medium");
        taskObjectModel.setTaskDescription("Cover service layer failure scenarios");

        //Act
        TaskEntity taskEntity = taskMapper.toEntity(taskObjectModel);

        //Assert
        assertEquals("Write unit tests", taskEntity.getTaskName());
        assertEquals("Engineering", taskEntity.getTaskType());
        assertEquals("Medium", taskEntity.getTaskLevel());
        assertEquals("Cover service layer failure scenarios", taskEntity.getTaskDescription());
        Assertions.assertInstanceOf(TaskEntity.class, taskEntity);

    }

    @Test
    @DisplayName("Tests mapper converts tasks entity's to model objects")
    void toModel() {
        //Arrange
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(55L);
        taskEntity.setTaskName("Map to model");
        taskEntity.setTaskType("Engineering");
        taskEntity.setTaskLevel("High");
        taskEntity.setTaskDescription("Verify TaskEntity -> taskObjectModel mapping");

        //Act
        taskObjectModel model = taskMapper.toModel(taskEntity);

        //Assert
        assertEquals(55L, model.getId());
        assertEquals("Map to model", model.getTaskName());
        assertEquals("Engineering", model.getTaskType());
        assertEquals("High", model.getTaskLevel());
        assertEquals("Verify TaskEntity -> taskObjectModel mapping", model.getTaskDescription());
        Assertions.assertInstanceOf(taskObjectModel.class, model);


    }
}