package com.todolist.adaptors.web;

import com.todolist.Models.TaskObjectModel;
import com.todolist.Models.UpdateTaskRequestPackage;
import com.todolist.adaptors.persistence.Jpa.TaskEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdaptorServiceTest {


    @Test
    @DisplayName("createTask should map DTO to entity and persist via EntityManager")
    void createTask() {
        AdaptorService adaptorService = new AdaptorService();
        //needs to create a new task from a data object to an entity object
        EntityManager fakeEM = mock(EntityManager.class);
        TaskMapper fakeMapper =  mock(TaskMapper.class);
        adaptorService.setEntityManager(fakeEM);
        adaptorService.setTaskMapper(fakeMapper);
        TaskObjectModel model = new TaskObjectModel(
                1L,
                "ETHAN",
                "Clean Desk",
                "Chore",
                "Easy",
                "Dust and organize workspace"
        );
        TaskEntity entity = new TaskEntity();
        entity.setId(1L);
        entity.setTaskOwnerId("ETHAN");
        entity.setTaskName("Clean Desk");
        entity.setTaskType("Chore");
        entity.setTaskLevel("Easy");
        entity.setTaskDescription("Dust and organize workspace");
        when(fakeMapper.toEntity(model)).thenReturn(entity);
        //act
        adaptorService.createTask(model);


        //assert
        verify(fakeMapper,times(1)).toEntity(model);
        verify(fakeEM,times(1)).persist(entity);
    }

    @Test
    void fetchAllTaskModels() {
        //creat empty list --> retrieve tasks using the query --> map to TaskObjectModel --> add to list --> return list

        //arrange
        AdaptorService adaptorService = new AdaptorService();
        adaptorService.entityManager = mock(EntityManager.class);
        adaptorService.taskMapper = mock(TaskMapper.class);
        TaskEntity task1 = new TaskEntity();
        task1.setId(1L);
        task1.setTaskOwnerId("ETHAN");
        task1.setTaskName("Organize Workspace");
        task1.setTaskType("Chore");
        task1.setTaskLevel("Medium");
        task1.setTaskDescription("Sort cables, clean monitor, and clear desk clutter.");

        TaskEntity task2 = new TaskEntity();
        task2.setId(2L);
        task2.setTaskOwnerId("ETHAN");
        task2.setTaskName("Write Project Summary");
        task2.setTaskType("Work");
        task2.setTaskLevel("Hard");
        task2.setTaskDescription("Summarize weekly progress and next sprint objectives.");
        List<TaskEntity> taskEntities = new ArrayList<>(List.of(task1, task2));

        TypedQuery<TaskEntity> query = mock(TypedQuery.class);
        when(adaptorService.entityManager.createQuery("SELECT t FROM TaskEntity t", TaskEntity.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(taskEntities);

        //Mocking returned objects
        when(adaptorService.taskMapper.toModel(task1)).thenReturn(new TaskObjectModel(1L, "ETHAN", "Organize Workspace",
                "Chore", "Medium", "Sort cables, clean monitor, and clear desk clutter."));
        when(adaptorService.taskMapper.toModel(task2)).thenReturn(new TaskObjectModel(2L, "ETHAN", "Write Project Summary",
                "Work", "Hard", "Summarize weekly progress and next sprint objectives."));
        //act
        ArrayList<TaskObjectModel> returnedObjects = adaptorService.fetchAllTaskModels();

        //assert
        verify(adaptorService.taskMapper, times(2)).toModel(any(TaskEntity.class));
        verify(query, times(1)).getResultList();
        Assertions.assertEquals(2, adaptorService.fetchAllTaskModels().size());
        TaskObjectModel first = returnedObjects.get(0);
        TaskObjectModel second = returnedObjects.get(1);

        Assertions.assertEquals(1L, first.getId());
        Assertions.assertEquals("ETHAN", first.getTaskOwnerId());
        Assertions.assertEquals("Organize Workspace", first.getTaskName());
        Assertions.assertEquals("Chore", first.getTaskType());
        Assertions.assertEquals("Medium", first.getTaskLevel());
        Assertions.assertEquals("Sort cables, clean monitor, and clear desk clutter.", first.getTaskDescription());

        Assertions.assertEquals(2L, second.getId());
        Assertions.assertEquals("ETHAN", second.getTaskOwnerId());
        Assertions.assertEquals("Write Project Summary", second.getTaskName());
        Assertions.assertEquals("Work", second.getTaskType());
        Assertions.assertEquals("Hard", second.getTaskLevel());
        Assertions.assertEquals("Summarize weekly progress and next sprint objectives.", second.getTaskDescription());
    }

    @Test
    @DisplayName("retrieveTask successfully returns a mapped task when the entity exists")
    void retrieveTaskDoesntThrowErrorWhenTaskFound() {
        //arrange
        AdaptorService adaptorService = new AdaptorService();
        adaptorService.entityManager = mock(EntityManager.class);
        adaptorService.taskMapper = mock(TaskMapper.class);
        TaskEntity task1 = new TaskEntity();
        task1.setId(1L);
        task1.setTaskOwnerId("ETHAN");
        task1.setTaskName("Organize Workspace");
        task1.setTaskType("Chore");
        task1.setTaskLevel("Medium");

        when(adaptorService.entityManager.find(TaskEntity.class, 1L)).thenReturn(task1);
        when(adaptorService.taskMapper.toModel(task1)).thenReturn(new TaskObjectModel(1L, "ETHAN", "Organize Workspace","Chore", "Medium", "Sort cables, clean monitor, and clear desk clutter."));

        //Act
        TaskObjectModel result = adaptorService.retrieveTask(1L);

        //Assert
        assertNotNull(result);
        verify(adaptorService.entityManager, times(1)).find(TaskEntity.class, 1L);
        verify(adaptorService.taskMapper, times(1)).toModel(task1);
        assertEquals(1L, result.getId());
        assertEquals("ETHAN", result.getTaskOwnerId());
        assertEquals("Organize Workspace", result.getTaskName());
        assertEquals("Chore", result.getTaskType());
        assertEquals("Medium", result.getTaskLevel());
        assertEquals("Sort cables, clean monitor, and clear desk clutter.", result.getTaskDescription());
        Assertions.assertInstanceOf(TaskObjectModel.class, result);
    }

    @Test
    @DisplayName("Tests retrieveTask returns the task when task exists")
    void retrieveTaskDoesThrowErrorWhenTaskNotFound() {
        //arrange
        AdaptorService adaptorService = new AdaptorService();
        adaptorService.entityManager = mock(EntityManager.class);
        adaptorService.taskMapper = mock(TaskMapper.class);

        when(adaptorService.entityManager.find(TaskEntity.class, 1L)).thenReturn(null);
        //Act
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            adaptorService.retrieveTask(1L);
        });
        verify(adaptorService.entityManager, times(1)).find(TaskEntity.class, 1L);
        verify(adaptorService.taskMapper, never()).toModel(any(TaskEntity.class));
    }

    @Test
    @DisplayName("updateTask updates taskName when valid ID and field provided")
    void updateTask_updatesTaskNameSuccessfully() {
        AdaptorService adaptorService = new AdaptorService();
        adaptorService.entityManager = mock(EntityManager.class);

        TaskEntity entity = new TaskEntity();
        entity.setId(1L);
        entity.setTaskName("Old Task");

        UpdateTaskRequestPackage req = new UpdateTaskRequestPackage(1L, "New Task", "taskName");
        when(adaptorService.entityManager.find(TaskEntity.class, 1L)).thenReturn(entity);

        adaptorService.updateTask(req);

        assertEquals("New Task", entity.getTaskName());
        verify(adaptorService.entityManager, times(1)).find(TaskEntity.class, 1L);
    }

    @Test
    @DisplayName("updateTask throws exception when fieldToUpdate is null or blank")
    void updateTask_throwsWhenFieldToUpdateInvalid() {
        AdaptorService adaptorService = new AdaptorService();
        adaptorService.entityManager = mock(EntityManager.class);

        TaskEntity entity = new TaskEntity();
        entity.setId(1L);
        when(adaptorService.entityManager.find(TaskEntity.class, 1L)).thenReturn(entity);

        UpdateTaskRequestPackage req = new UpdateTaskRequestPackage(1L, "Value", "");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> adaptorService.updateTask(req));

        assertEquals("Field to update cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("updateTask throws exception when replacementValue is null or blank")
    void updateTask_throwsWhenReplacementValueInvalid() {
        AdaptorService adaptorService = new AdaptorService();
        adaptorService.entityManager = mock(EntityManager.class);

        TaskEntity entity = new TaskEntity();
        entity.setId(1L);
        when(adaptorService.entityManager.find(TaskEntity.class, 1L)).thenReturn(entity);

        UpdateTaskRequestPackage req = new UpdateTaskRequestPackage(1L, "", "taskName");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> adaptorService.updateTask(req));

        assertEquals("Replacement value cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("updateTask throws exception when provided fieldToUpdate does not exist")
    void updateTask_throwsWhenFieldDoesNotExist() {
        AdaptorService adaptorService = new AdaptorService();
        adaptorService.entityManager = mock(EntityManager.class);

        TaskEntity entity = new TaskEntity();
        entity.setId(1L);
        when(adaptorService.entityManager.find(TaskEntity.class, 1L)).thenReturn(entity);

        UpdateTaskRequestPackage req = new UpdateTaskRequestPackage(1L, "Value", "invalidField");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> adaptorService.updateTask(req));

        assertEquals("Field invalidField does not exist", exception.getMessage());
    }

    @Test
    @DisplayName("deleteTask removes entity when task exists")
    void deleteTaskremovesEntityWhenTaskExists() {
        // Arrange
        AdaptorService adaptorService = new AdaptorService();
        adaptorService.entityManager = mock(EntityManager.class);

        TaskEntity fakeEntity = new TaskEntity();
        fakeEntity.setId(1L);

        when(adaptorService.entityManager.find(TaskEntity.class, 1L)).thenReturn(fakeEntity);

        // Act
        adaptorService.deleteTask(1L);

        // Assert
        verify(adaptorService.entityManager, times(1)).remove(fakeEntity);
        verify(adaptorService.entityManager, times(1)).find(TaskEntity.class, 1L);
    }

    @Test
    @DisplayName("deleteTask throws exception when task does not exist")
    void deleteTaskthrowsWhenTaskDoesNotExist() {
        // Arrange
        AdaptorService adaptorService = new AdaptorService();
        adaptorService.entityManager = mock(EntityManager.class);

        when(adaptorService.entityManager.find(TaskEntity.class, 99L)).thenReturn(null);

        // Act + Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> adaptorService.deleteTask(99L)
        );

        assertEquals("Task with id 99 does not exist", exception.getMessage());
        verify(adaptorService.entityManager, times(0)).remove(any());
    }
}