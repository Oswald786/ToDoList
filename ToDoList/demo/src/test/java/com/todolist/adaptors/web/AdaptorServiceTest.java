package com.todolist.adaptors.web;

import com.todolist.Models.TaskObjectModel;
import com.todolist.Models.UpdateTaskRequestPackage;
import com.todolist.adaptors.persistence.Jpa.TaskEntity;
import com.todolist.exceptions.MapperFailedException;
import com.todolist.exceptions.TaskNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class AdaptorServiceTest {

    @Mock
    EntityManager entityManager;

    @Mock
    TaskMapper taskMapper;

    @InjectMocks
    AdaptorService adaptorService;

    // --------------------- CREATE ---------------------

    @Test
    @DisplayName("createTask should map DTO to entity and persist via EntityManager")
    void createTask() {
        TaskObjectModel model = new TaskObjectModel(
                1L,"ETHAN","Clean Desk","Chore","Easy","Dust and organize workspace"
        );

        TaskEntity entity = new TaskEntity();
        entity.setId(1L);

        when(taskMapper.toEntity(model)).thenReturn(entity);

        adaptorService.createTask(model);

        verify(taskMapper).toEntity(model);
        verify(entityManager).persist(entity);
    }

    @Test
    @DisplayName("createTask throws when Mapper returns null")
    void createTask_throws_whenMapperNull() {
        TaskObjectModel model = new TaskObjectModel(
                1L,"ETHAN","Clean Desk","Chore","Easy","Dust and organize workspace"
        );

        when(taskMapper.toEntity(model)).thenReturn(null);

        assertThrows(MapperFailedException.class, () -> adaptorService.createTask(model));
    }

    @Test
    @DisplayName("createTask throws when model is null")
    void createTask_throws_nullModel() {
        assertThrows(IllegalArgumentException.class, () -> adaptorService.createTask(null));
    }

    // --------------------- FETCH ALL ---------------------

    @Test
    @DisplayName("fetchAllTaskModels retrieves tasks and maps them correctly")
    void fetchAllTaskModels() {
        TaskEntity task1 = new TaskEntity();
        task1.setId(1L);
        task1.setTaskName("Organize Workspace");

        TaskEntity task2 = new TaskEntity();
        task2.setId(2L);
        task2.setTaskName("Write Summary");

        List<TaskEntity> list = List.of(task1, task2);

        TypedQuery<TaskEntity> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(TaskEntity.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(list);

        when(taskMapper.toModel(task1)).thenReturn(new TaskObjectModel(1L, "ETHAN", "Organize Workspace","Chore","Medium","desc"));
        when(taskMapper.toModel(task2)).thenReturn(new TaskObjectModel(2L, "ETHAN", "Write Summary","Work","Hard","desc"));

        var result = adaptorService.fetchAllTaskModels();

        verify(taskMapper, times(2)).toModel(any());
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("fetchAllTaskModels throws MapperFailedException when mapping fails")
    void fetchAllTaskModels_mapperFails() {
        TaskEntity task = new TaskEntity();
        task.setId(1L);

        List<TaskEntity> list = List.of(task);

        TypedQuery<TaskEntity> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(TaskEntity.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(list);

        when(taskMapper.toModel(task)).thenReturn(null);

        assertThrows(MapperFailedException.class, () -> adaptorService.fetchAllTaskModels());
    }

    @Test
    @DisplayName("fetchAllTaskByOwnerId throws illegal argument exception when task owner provided is null")
    void fetchAllTaskByOwnerId_throws_whenOwnerNull() {
        //Arrange
        Long ownerId = null;

        //Act + Assert
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> adaptorService.fetchAllTasksByOwner("")
        );
    }

    @Test
    @DisplayName("fetchAllTasksByOwner fetches tasks with matching owner ID")
    void fetchAllTasksByOwner_fetchesCorrectOwnerTasks() {

        // Arrange
        String ownerId = "ETHAN";

        // Mock tasks
        TaskEntity task1 = new TaskEntity();
        task1.setId(1L);
        task1.setTaskOwnerId("ETHAN");

        TaskEntity task2 = new TaskEntity();
        task2.setId(2L);
        task2.setTaskOwnerId("ETHAN");

        List<TaskEntity> resultList = List.of(task1, task2);

        // Mock query
        TypedQuery<TaskEntity> fakeQuery = mock(TypedQuery.class);

        when(adaptorService.entityManager.createQuery(
                "SELECT t FROM TaskEntity t WHERE t.taskOwnerId = :owner",
                TaskEntity.class
        )).thenReturn(fakeQuery);

        when(fakeQuery.setParameter("owner", ownerId)).thenReturn(fakeQuery);
        when(fakeQuery.getResultList()).thenReturn(resultList);

        // Mock mapper
        when(adaptorService.taskMapper.toModel(task1))
                .thenReturn(new TaskObjectModel(1L, "ETHAN", "Task1", "Chore", "1", "desc"));

        when(adaptorService.taskMapper.toModel(task2))
                .thenReturn(new TaskObjectModel(2L, "ETHAN", "Task2", "Work", "2", "desc"));

        // Act
        List<TaskObjectModel> results = adaptorService.fetchAllTasksByOwner(ownerId);

        // Assert
        verify(adaptorService.entityManager, times(1))
                .createQuery("SELECT t FROM TaskEntity t WHERE t.taskOwnerId = :owner", TaskEntity.class);

        verify(fakeQuery, times(1)).setParameter("owner", ownerId);
        verify(fakeQuery, times(1)).getResultList();
        verify(adaptorService.taskMapper, times(2)).toModel(any(TaskEntity.class));

        assertEquals(2, results.size());
        assertEquals(ownerId, results.get(0).getTaskOwnerId());
        assertEquals(ownerId, results.get(1).getTaskOwnerId());
    }

    @Test
    @DisplayName("fetchAllTasksByOwner throws MapperFailedException when one of the tasks returned is null")
    void fetchAllTasksByOwner_throws_MapperFailedException_when_Task_Null() {

        // Arrange
        String ownerId = "ETHAN";

        // Mock tasks
        TaskEntity task1 = new TaskEntity();
        task1.setId(1L);
        task1.setTaskOwnerId("ETHAN");

        TaskEntity task2 = new TaskEntity();
        task2.setId(2L);
        task2.setTaskOwnerId("ETHAN");

        List<TaskEntity> resultList = List.of(task1, task2);

        // Mock query
        TypedQuery<TaskEntity> fakeQuery = mock(TypedQuery.class);

        when(adaptorService.entityManager.createQuery(
                "SELECT t FROM TaskEntity t WHERE t.taskOwnerId = :owner",
                TaskEntity.class
        )).thenReturn(fakeQuery);

        when(fakeQuery.setParameter("owner", ownerId)).thenReturn(fakeQuery);
        when(fakeQuery.getResultList()).thenReturn(resultList);

        // Mock mapper
        when(adaptorService.taskMapper.toModel(task1))
                .thenReturn(new TaskObjectModel(1L, "ETHAN", "Task1", "Chore", "1", "desc"));

        when(adaptorService.taskMapper.toModel(task2))
                .thenReturn(null);

        // Act
        Assertions.assertThrows(MapperFailedException.class,() -> adaptorService.fetchAllTasksByOwner(ownerId));

        // Assert
        verify(adaptorService.entityManager, times(1))
                .createQuery("SELECT t FROM TaskEntity t WHERE t.taskOwnerId = :owner", TaskEntity.class);

        verify(fakeQuery, times(1)).setParameter("owner", ownerId);
        verify(fakeQuery, times(1)).getResultList();
        verify(adaptorService.taskMapper, times(2)).toModel(any(TaskEntity.class));
    }

    // --------------------- RETRIEVE ---------------------

    @Test
    @DisplayName("retrieveTask returns mapped model when task exists")
    void retrieveTask_success() {
        TaskEntity entity = new TaskEntity();
        entity.setId(1L);
        TaskObjectModel fakeModel = new TaskObjectModel(1L, "ETHAN", "Organize Workspace","Chore","Medium","desc");

        when(entityManager.find(TaskEntity.class, 1L)).thenReturn(entity);
        when(taskMapper.toModel(entity)).thenReturn(fakeModel);

        var result = adaptorService.retrieveTask(1L);

        assertNotNull(result);
        Assertions.assertEquals(fakeModel, result);
        verify(entityManager).find(TaskEntity.class, 1L);
    }

    @Test
    @DisplayName("retrieveTask throws when entity not found")
    void retrieveTask_notFound() {
        when(entityManager.find(TaskEntity.class, 1L)).thenReturn(null);

        assertThrows(TaskNotFoundException.class, () -> adaptorService.retrieveTask(1L));
        verify(taskMapper, never()).toModel(any());
    }

    @Test
    @DisplayName("retrieveTask throws illegal argument exception when task id is null")
    void retrieveTask_nullEntity() {
        //Arrange
        Long taskId = null;

        //Act + Assert
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> adaptorService.retrieveTask(taskId)
        );
    }

    // --------------------- UPDATE ---------------------

    @Test
    @DisplayName("updateTask updates taskName when valid")
    void updateTask_success() {
        TaskEntity entity = new TaskEntity();
        entity.setId(1L);
        entity.setTaskName("Old");

        when(entityManager.find(TaskEntity.class, 1L)).thenReturn(entity);
        when(taskMapper.toModel(entity)).thenReturn(
                new TaskObjectModel(1L, "ETHAN","Old","Chore","Medium","desc")
        );

        UpdateTaskRequestPackage req = new UpdateTaskRequestPackage(1L, "New Name", "taskName");

        adaptorService.updateTask(req);

        assertEquals("New Name", entity.getTaskName());
    }

    @Test
    @DisplayName("updateTask throws when field invalid")
    void updateTask_invalidField() {
        TaskEntity entity = new TaskEntity();

        UpdateTaskRequestPackage req = new UpdateTaskRequestPackage(1L, "Value", "");

        assertThrows(IllegalArgumentException.class, () -> adaptorService.updateTask(req));
    }

    @Test
    @DisplayName("updateTask throws when replacementValue is null or blank")
    void updateTask_throwsWhenReplacementValueNullOrBlank() {
        // Arrange
        UpdateTaskRequestPackage req = new UpdateTaskRequestPackage(1L, "", "taskName");

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> adaptorService.updateTask(req));
    }

    @Test
    @DisplayName("updateTask throws when fieldToUpdate does not match allowed fields")
    void updateTask_throwsWhenFieldDoesNotExist() {
        // Arrange
        TaskEntity entity = new TaskEntity();
        entity.setId(1L);

        when(entityManager.find(TaskEntity.class, 1L)).thenReturn(entity);
        when(taskMapper.toModel(entity)).thenReturn(
                new TaskObjectModel(1L, "ETHAN","Old","Chore","Medium","desc")
        );

        UpdateTaskRequestPackage req = new UpdateTaskRequestPackage(1L, "Value", "invalidField");

        // Act + Assert
        assertThrows(TaskNotFoundException.class, () -> adaptorService.updateTask(req));
    }

    @Test
    @DisplayName("updateTask throws when task does not exist")
    void updateTask_throwsWhenTaskDoesNotExist() {
        // Arrange
        when(entityManager.find(TaskEntity.class, 1L)).thenReturn(null);

        UpdateTaskRequestPackage req = new UpdateTaskRequestPackage(1L, "New", "taskName");

        // Act + Assert
        assertThrows(TaskNotFoundException.class, () -> adaptorService.updateTask(req));
    }

    @Test
    @DisplayName("updateTask throws when taskLevel is non-numeric")
    void updateTask_throwsWhenTaskLevelNonNumeric() {
        // Arrange
        TaskEntity entity = new TaskEntity();
        entity.setId(1L);

        when(entityManager.find(TaskEntity.class, 1L)).thenReturn(entity);
        when(taskMapper.toModel(entity)).thenReturn(
                new TaskObjectModel(1L, "ETHAN","Old","Chore","Medium","desc")
        );

        UpdateTaskRequestPackage req = new UpdateTaskRequestPackage(1L, "abc", "taskLevel");

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> adaptorService.updateTask(req));
    }

    @Test
    @DisplayName("updateTask updates taskType when valid")
    void updateTask_updatesTaskType() {
        // Arrange
        TaskEntity entity = new TaskEntity();
        entity.setId(1L);
        entity.setTaskType("OldType");

        when(entityManager.find(TaskEntity.class, 1L)).thenReturn(entity);
        when(taskMapper.toModel(entity)).thenReturn(
                new TaskObjectModel(1L, "ETHAN","Old","Chore","Medium","desc")
        );

        UpdateTaskRequestPackage req = new UpdateTaskRequestPackage(1L, "NewType", "taskType");

        // Act
        adaptorService.updateTask(req);

        // Assert
        assertEquals("NewType", entity.getTaskType());
    }

    // --------------------- DELETE ---------------------

    @Test
    @DisplayName("deleteTask removes entity when exists")
    void deleteTask_success() {
        // Arrange
        long id = 1L;

        // Create a spy of your service so we can stub retrieveTask()
        AdaptorService spyService = spy(adaptorService);

        // Mock the model returned by retrieveTask()
        TaskObjectModel model = new TaskObjectModel(
                id, "ETHAN", "Test", "Chore", "1", "desc"
        );
        doReturn(model).when(spyService).retrieveTask(id);

        // Mock the entity retrieved for deletion
        TaskEntity entity = new TaskEntity();
        entity.setId(id);

        when(entityManager.find(TaskEntity.class, id)).thenReturn(entity);

        // Act
        spyService.deleteTask(id);

        // Assert
        verify(spyService, times(1)).retrieveTask(id);
        verify(entityManager, times(1)).find(TaskEntity.class, id);
        verify(entityManager, times(1)).remove(entity);
    }

    @Test
    @DisplayName("deleteTask throws when entity missing")
    void deleteTask_missing() {
        when(entityManager.find(TaskEntity.class, 99L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> adaptorService.deleteTask(99L));
        verify(entityManager, never()).remove(any());
    }

    @Test
    @DisplayName("deleteTask throws when id is zero or negative")
    void deleteTask_throwsWhenIdInvalid() {
        // Arrange
        long invalidId = 0;

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> adaptorService.deleteTask(invalidId));
    }

    @Test
    @DisplayName("deleteTask throws when task does not exist")
    void deleteTask_throwsWhenTaskNotFound() {
        // Arrange
        long id = 1L;
        when(entityManager.find(TaskEntity.class, id)).thenReturn(null);

        // Act + Assert
        assertThrows(TaskNotFoundException.class, () -> adaptorService.deleteTask(id));

        // ensure remove never called
        verify(entityManager, never()).remove(any());
    }

}