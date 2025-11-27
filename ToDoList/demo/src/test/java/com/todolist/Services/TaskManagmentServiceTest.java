package com.todolist.Services;

import com.todolist.Models.TaskObjectModel;
import com.todolist.Models.UpdateTaskRequestPackage;
import com.todolist.adaptors.persistence.Jpa.TaskEntity;
import com.todolist.adaptors.web.AdaptorService;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@MicronautTest
@ExtendWith(MockitoExtension.class)
@DisplayName("TaskManagementService Authentication Tests")
class TaskManagmentServiceTest {

    TaskObjectModel valid;

    TaskObjectModel invalidNameNull;
    TaskObjectModel invalidNameEmpty;

    TaskObjectModel invalidTypeNull;
    TaskObjectModel invalidTypeEmpty;

    TaskObjectModel invalidLevelNull;
    TaskObjectModel invalidLevelEmpty;

    TaskObjectModel invalidDescNull;
    TaskObjectModel invalidDescEmpty;

    java.util.List<TaskObjectModel> allInvalid;


    @Mock
    AdaptorService adaptorService;

    @Mock
    GameService gameService;

    @Mock
    Authentication mockAuthentication;

    @InjectMocks
    TaskManagementService service;


    @BeforeEach
    void setUp() {
        // Valid baseline
        valid = new TaskObjectModel(1L,"ETHAN", "Test Task", "Test Type", "Test Level", "Test Description");

        // Invalid: task name
        invalidNameNull  = new TaskObjectModel(2L,"ETHAN", null,        "Test Type", "Test Level", "Test Description");
        invalidNameEmpty = new TaskObjectModel(3L,"ETHAN", "",          "Test Type", "Test Level", "Test Description");

        // Invalid: task type
        invalidTypeNull  = new TaskObjectModel(4L,"ETHAN", "Test Task", null,        "Test Level", "Test Description");
        invalidTypeEmpty = new TaskObjectModel(5L,"ETHAN", "Test Task", "",          "Test Level", "Test Description");

        // Invalid: task level
        invalidLevelNull  = new TaskObjectModel(6L,"ETHAN", "Test Task", "Test Type", null,        "Test Description");
        invalidLevelEmpty = new TaskObjectModel(7L,"ETHAN", "Test Task", "Test Type", "",          "Test Description");

        // Invalid: task description
        invalidDescNull  = new TaskObjectModel(8L,"ETHAN", "Test Task", "Test Type", "Test Level", null);
        invalidDescEmpty = new TaskObjectModel(9L,"ETHAN", "Test Task", "Test Type", "Test Level", "");

        // Convenience collection
        allInvalid = java.util.List.of(
                invalidNameNull, invalidNameEmpty,
                invalidTypeNull, invalidTypeEmpty,
                invalidLevelNull, invalidLevelEmpty,
                invalidDescNull, invalidDescEmpty
        );
    }
    @Test
    @DisplayName("Create Task shows illegal state exception when authentication is null and doesnt create task")
    void createTask_throwsIllegalState_whenAuthenticationIsNull() {
        //Arrange
        Authentication authentication = null;
        TaskObjectModel taskObjectModel = this.valid;

        //Assert
        assertThrows(IllegalStateException.class, () -> service.createTask(taskObjectModel, authentication));
        verifyNoInteractions(service.adaptorService);
    }

    @DisplayName("Create task throws illegal state exception when task object model is null and test doesnt create task ")
    @Test
    void createTask_throwsIllegalState_whenTaskObjectModelIsNull() {
        //Arrange
        Authentication authentication = this.mockAuthentication;
        TaskObjectModel taskObjectModel = null;

        //Assert
        assertThrows(IllegalStateException.class, () -> service.createTask(taskObjectModel, authentication));
        verifyNoInteractions(service.adaptorService);

    }


    @DisplayName("Test createTask interacts with entity manager as expected when valid authentication and task model passed")
    @Test
    void createTask() {
        //Arrange
        TaskObjectModel taskObjectModel = valid;
        when(mockAuthentication.getName()).thenReturn("ETHAN");
        //Act

        service.createTask(taskObjectModel, mockAuthentication);
        //Assert
        Assertions.assertEquals("ETHAN",taskObjectModel.getTaskOwnerId());
        Mockito.verify(adaptorService ,times(1)).createTask(taskObjectModel);
    }

    @Test
    @DisplayName("Test throws Error when entity not found")
    void entityNotFoundWhenTryingToUpdateTask() {
        //Arrange
        TaskManagementService service = new TaskManagementService();
        service.adaptorService = new AdaptorService();
        EntityManager entityManager = mock(EntityManager.class);
        service.adaptorService.setEntityManager(entityManager);
        UpdateTaskRequestPackage updateTaskRequestPackage = mock(UpdateTaskRequestPackage.class);


        when(entityManager.find(eq(TaskEntity.class), anyLong())).thenReturn(null);


        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.updateTask(updateTaskRequestPackage,this.mockAuthentication);
        });

    }

    @Test
    @DisplayName("Test throws Error when field not found")
    void testThrowsErrorWhenFieldNotFound() {
        //Arrange
        TaskManagementService service = new TaskManagementService();
        service.adaptorService = new AdaptorService();
        EntityManager entityManager = mock(EntityManager.class);
        service.adaptorService.setEntityManager(entityManager);
        UpdateTaskRequestPackage req = new UpdateTaskRequestPackage(1L, "taskName", null);
        TaskEntity taskEntity = mock(TaskEntity.class);


        when(entityManager.find(eq(TaskEntity.class), anyLong())).thenReturn(taskEntity);


        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.updateTask(req,this.mockAuthentication);
        });
    }


    @Test
    @DisplayName("Test throws Error when replacement not found")
    void testThrowsErrorWhenReplacementNotFound() {
        //Arrange
        TaskManagementService service = new TaskManagementService();
        service.adaptorService = new AdaptorService();
        EntityManager entityManager = mock(EntityManager.class);
        service.adaptorService.setEntityManager(entityManager);
        UpdateTaskRequestPackage req = new UpdateTaskRequestPackage(1L, null, "Task Type");
        TaskEntity taskEntity = mock(TaskEntity.class);


        when(entityManager.find(eq(TaskEntity.class), anyLong())).thenReturn(taskEntity);


        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.updateTask(req,this.mockAuthentication);
        });

    }


    @Test
    @DisplayName("Test updateTask updates task name correctly")
    void testUpdateTaskUpdatesTaskNameCorrectly() {
        // Arrange
        TaskManagementService service = new TaskManagementService();
        AdaptorService adaptorService = mock(AdaptorService.class);
        service.adaptorService = adaptorService;

        UpdateTaskRequestPackage req = new UpdateTaskRequestPackage(1, "Tidy Room", "taskName");

        TaskObjectModel fakeTask = new TaskObjectModel();
        fakeTask.setId(1L);
        fakeTask.setTaskOwnerId("ETHAN");
        fakeTask.setTaskName("Old Task");

        when(adaptorService.retrieveTask(1L)).thenReturn(fakeTask);
        doNothing().when(adaptorService).updateTask(req);

        // Act
        service.updateTask(req, this.mockAuthentication);

        // Assert
        verify(adaptorService, times(1)).retrieveTask(1L);
        verify(adaptorService, times(1)).updateTask(req);

    }

    @Test
    @DisplayName("Test updateTask throws error when entity not found")
    void deleteTask() {
        // Arrange
        TaskManagementService service = new TaskManagementService();
        AdaptorService adaptorService = mock(AdaptorService.class);
        GameService gameService = mock(GameService.class);
        service.gameService = gameService;
        service.adaptorService = adaptorService;

        long taskId = 1L;
        TaskObjectModel fakeTask = new TaskObjectModel();
        fakeTask.setId(taskId);
        fakeTask.setTaskOwnerId("ETHAN");

        when(adaptorService.retrieveTask(taskId)).thenReturn(fakeTask);
        doNothing().when(adaptorService).deleteTask(taskId);

        // Act
        service.deleteTask(taskId, this.mockAuthentication);

        // Assert
        verify(adaptorService, times(1)).retrieveTask(taskId);
        verify(adaptorService, times(1)).deleteTask(taskId);
    }

    @Test
    @DisplayName("Tests all tasks are fetched correctly")
    void fetchAllTasks() {
        // Arrange
        TaskManagementService service = new TaskManagementService();
        AdaptorService adaptorService = mock(AdaptorService.class);
        service.adaptorService = adaptorService;

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("ETHAN");

        ArrayList<TaskObjectModel> expectedList = new ArrayList<>();
        expectedList.add(new TaskObjectModel(1L, "ETHAN", "Tidy Room", "Chore", "Easy", "Pick up clothes and make the bed"));
        expectedList.add(new TaskObjectModel(2L, "ETHAN", "Grocery Shopping", "Errand", "Medium", "Buy milk, eggs, bread"));
        expectedList.add(new TaskObjectModel(3L, "ETHAN", "Write Report", "Work", "Hard", "Summarize Q3 project outcomes"));

        when(adaptorService.fetchAllTasksByOwner("ETHAN")).thenReturn(expectedList);

        // Act
        ArrayList<TaskObjectModel> result = service.fetchAllTasks(authentication);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Tidy Room", result.get(0).getTaskName());
        assertEquals("Grocery Shopping", result.get(1).getTaskName());
        assertEquals("Write Report", result.get(2).getTaskName());

        verify(adaptorService, times(1)).fetchAllTasksByOwner("ETHAN");
    }

    private static List<TaskEntity> getTaskEntities() {
        List<TaskEntity> resultList = new ArrayList<>();

        TaskEntity t1 = new TaskEntity();
        t1.setId(1L);
        t1.setTaskOwnerId("ETHAN");
        t1.setTaskName("Tidy Room");
        t1.setTaskType("Chore");
        t1.setTaskLevel("Easy");
        t1.setTaskDescription("Pick up clothes and make the bed");
        resultList.add(t1);

        TaskEntity t2 = new TaskEntity();
        t2.setId(2L);
        t2.setTaskOwnerId("ETHAN");
        t2.setTaskName("Grocery Shopping");
        t2.setTaskType("Errand");
        t2.setTaskLevel("Medium");
        t2.setTaskDescription("Buy milk, eggs, bread");
        resultList.add(t2);

        TaskEntity t3 = new TaskEntity();
        t3.setId(3L);
        t3.setTaskOwnerId("ETHAN");
        t3.setTaskName("Write Report");
        t3.setTaskType("Work");
        t3.setTaskLevel("Hard");
        t3.setTaskDescription("Summarize Q3 project outcomes");
        resultList.add(t3);
        return resultList;
    }

    @Test
    @DisplayName("Tests task is fetched correctly Based on id")
    void fetchTaskWithId() {
        // Arrange
        TaskManagementService service = new TaskManagementService();
        AdaptorService adaptorService = mock(AdaptorService.class);
        service.adaptorService = adaptorService;

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("ETHAN");
        when(authentication.getRoles()).thenReturn(List.of("USER"));

        TaskObjectModel expectedTask = new TaskObjectModel(
                2L, "ETHAN", "Grocery Shopping", "Errand", "Medium", "Buy milk, eggs, bread");

        when(adaptorService.retrieveTask(2L)).thenReturn(expectedTask);

        // Act
        TaskObjectModel result = service.fetchTaskWithId(2L, authentication);

        // Assert
        assertNotNull(result);
        assertEquals(expectedTask.getTaskName(), result.getTaskName());
        assertEquals(expectedTask.getTaskType(), result.getTaskType());
        assertEquals(expectedTask.getTaskLevel(), result.getTaskLevel());
        assertEquals(expectedTask.getTaskDescription(), result.getTaskDescription());
        assertEquals(expectedTask.getId(), result.getId());

        verify(adaptorService, times(1)).retrieveTask(2L);
    }

    @Test
    @DisplayName("Task throws and logs error when nothing is returned")
    void taskThrowsErrorWhenEntityManagerFailsToReturnAnything(){
        // Arrange
        TaskManagementService service = new TaskManagementService();
        AdaptorService adaptorService = mock(AdaptorService.class);
        service.adaptorService = adaptorService;

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("ETHAN");
        when(authentication.getRoles()).thenReturn(List.of("USER"));

        when(adaptorService.retrieveTask(2L)).thenThrow(new IllegalArgumentException("Task not found"));


        // Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.fetchTaskWithId(2L, authentication);
        });
        verify(adaptorService, times(1)).retrieveTask(2L);
    }

    @Test
    @DisplayName("Validate that task access is granted to owner or authorized user")
    void validateTaskOwnershipAndAuthority() {
        //method checks whether user either has authority as a suer or is the owner of the task
        //arrange

        TaskManagementService service = new TaskManagementService();
        Authentication authenticationRole = mock(Authentication.class);
        Authentication authenticationOwner = mock(Authentication.class);

        TaskObjectModel ethanTask = new TaskObjectModel(
                1L,                     // ID
                "ETHAN",                // Task Owner
                "Organize Workspace",   // Task Name
                "Chore",                // Task Type
                "Medium",               // Task Level
                "Sort cables, dust off desk, and arrange items neatly" // Description
        );

        // Mock a user who owns the task
        when(authenticationOwner.getName()).thenReturn("ETHAN");

        // Mock a different user with admin privileges
        when(authenticationRole.getName()).thenReturn("notAnOwner");
        when(authenticationRole.getRoles()).thenReturn(List.of("ADMIN"));

        //Act
        boolean resultAsTaskOwner = service.validateTaskOwnershipAndAuthority(ethanTask,authenticationOwner,List.of("USER"));

        boolean resultAsAdmin = service.validateTaskOwnershipAndAuthority(ethanTask,authenticationRole,List.of("ADMIN"));

        Assertions.assertTrue(resultAsTaskOwner);
        Assertions.assertTrue(resultAsAdmin);

    }
}