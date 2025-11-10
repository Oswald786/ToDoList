package com.todolist.Services;

import com.todolist.Models.taskObjectModel;
import com.todolist.Models.updateTaskRequestPackage;
import com.todolist.adaptors.persistence.jpa.TaskEntity;
import com.todolist.adaptors.web.AdaptorService;
import com.todolist.adaptors.web.TaskMapper;
import com.todolist.adaptors.web.TaskMapperImpl;
import com.todolist.auth.AuthenticationService;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@MicronautTest
@DisplayName("TaskManagmentService Authentication Tests")
class TaskManagmentServiceTest {

    taskObjectModel valid;

    taskObjectModel invalidNameNull;
    taskObjectModel invalidNameEmpty;

    taskObjectModel invalidTypeNull;
    taskObjectModel invalidTypeEmpty;

    taskObjectModel invalidLevelNull;
    taskObjectModel invalidLevelEmpty;

    taskObjectModel invalidDescNull;
    taskObjectModel invalidDescEmpty;

    java.util.List<taskObjectModel> allInvalid;

    Authentication mockAuthentication;


    @BeforeEach
    void setUp() {
        // Valid baseline
        valid = new taskObjectModel(1L,"ETHAN", "Test Task", "Test Type", "Test Level", "Test Description");

        // Invalid: task name
        invalidNameNull  = new taskObjectModel(2L,"ETHAN", null,        "Test Type", "Test Level", "Test Description");
        invalidNameEmpty = new taskObjectModel(3L,"ETHAN", "",          "Test Type", "Test Level", "Test Description");

        // Invalid: task type
        invalidTypeNull  = new taskObjectModel(4L,"ETHAN", "Test Task", null,        "Test Level", "Test Description");
        invalidTypeEmpty = new taskObjectModel(5L,"ETHAN", "Test Task", "",          "Test Level", "Test Description");

        // Invalid: task level
        invalidLevelNull  = new taskObjectModel(6L,"ETHAN", "Test Task", "Test Type", null,        "Test Description");
        invalidLevelEmpty = new taskObjectModel(7L,"ETHAN", "Test Task", "Test Type", "",          "Test Description");

        // Invalid: task description
        invalidDescNull  = new taskObjectModel(8L,"ETHAN", "Test Task", "Test Type", "Test Level", null);
        invalidDescEmpty = new taskObjectModel(9L,"ETHAN", "Test Task", "Test Type", "Test Level", "");

        // Convenience collection
        allInvalid = java.util.List.of(
                invalidNameNull, invalidNameEmpty,
                invalidTypeNull, invalidTypeEmpty,
                invalidLevelNull, invalidLevelEmpty,
                invalidDescNull, invalidDescEmpty
        );

        // Authentication Examples
        this.mockAuthentication =  Authentication.build("ETHAN",List.of("ADMIN","USER"));
    }

    @Test
    @DisplayName("Test createTask interacts with entity manager as expected")
    void createTask() {
        //Arrange
        TaskManagmentService service = mock(TaskManagmentService.class);
        taskObjectModel taskObjectModel = new taskObjectModel();
        taskObjectModel.setTaskName("Test Task");
        taskObjectModel.setTaskType("Test Type");
        taskObjectModel.setTaskDescription("Test Description");
        taskObjectModel.setTaskLevel("Test Level");
        //Act

        service.createTask(taskObjectModel, mockAuthentication);
        //Assert
        Mockito.verify(service).createTask(taskObjectModel, mockAuthentication);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("Test createTask validates task correctly")
    void createTask_ValidatesTaskCorrectly() {
        //Arrange
        TaskManagmentService service = spy(new TaskManagmentService());
        taskObjectModel CorrecttaskObjectModel = new taskObjectModel();
        CorrecttaskObjectModel.setTaskName("Test Task");
        CorrecttaskObjectModel.setTaskType("Test Type");
        CorrecttaskObjectModel.setTaskDescription("Test Description");
        CorrecttaskObjectModel.setTaskLevel("Test Level");



        //Act
        boolean result = service.validateTaskObjectModel(CorrecttaskObjectModel,this.mockAuthentication);
        boolean result2 = service.validateTaskObjectModel(invalidNameNull,this.mockAuthentication);
        boolean result3 = service.validateTaskObjectModel(invalidNameEmpty,this.mockAuthentication);
        boolean result4 = service.validateTaskObjectModel(invalidTypeNull,this.mockAuthentication);
        boolean result5 = service.validateTaskObjectModel(invalidTypeEmpty,this.mockAuthentication);
        boolean result6 = service.validateTaskObjectModel(invalidLevelNull,this.mockAuthentication);
        boolean result7 = service.validateTaskObjectModel(invalidLevelEmpty,this.mockAuthentication);
        boolean result8 = service.validateTaskObjectModel(invalidDescNull,this.mockAuthentication);
        boolean result9 = service.validateTaskObjectModel(invalidDescEmpty,this.mockAuthentication);
        //Assert
        assertTrue(result);
        assertFalse(result2);
        assertFalse(result3);
        assertFalse(result4);
        assertFalse(result5);
        assertFalse(result6);
        assertFalse(result7);
        assertFalse(result8);
        assertFalse(result9);

        Mockito.verify(service,Mockito.atMost(9)).validateTaskObjectModel(CorrecttaskObjectModel,this.mockAuthentication);

    }

    @Test
    @DisplayName("Test throws Error when entity not found")
    void entityNotFoundWhenTryingToUpdateTask() {
        //Arrange
        TaskManagmentService service = new TaskManagmentService();
        service.adaptorService = new AdaptorService();
        EntityManager entityManager = mock(EntityManager.class);
        service.adaptorService.setEntityManager(entityManager);
        updateTaskRequestPackage updateTaskRequestPackage = mock(updateTaskRequestPackage.class);


        when(entityManager.find(eq(TaskEntity.class), anyLong())).thenReturn(null);


        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.updateTask(updateTaskRequestPackage,this.mockAuthentication);
        });

    }

    @Test
    @DisplayName("Test throws Error when field not found")
    void testThrowsErrorWhenFieldNotFound() {
        //Arrange
        TaskManagmentService service = new TaskManagmentService();
        service.adaptorService = new AdaptorService();
        EntityManager entityManager = mock(EntityManager.class);
        service.adaptorService.setEntityManager(entityManager);
        updateTaskRequestPackage req = new updateTaskRequestPackage(1L, "taskName", null);
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
        TaskManagmentService service = new TaskManagmentService();
        service.adaptorService = new AdaptorService();
        EntityManager entityManager = mock(EntityManager.class);
        service.adaptorService.setEntityManager(entityManager);
        updateTaskRequestPackage req = new updateTaskRequestPackage(1L, null, "Task Type");
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
        TaskManagmentService service = new TaskManagmentService();
        AdaptorService adaptorService = mock(AdaptorService.class);
        service.adaptorService = adaptorService;

        updateTaskRequestPackage req = new updateTaskRequestPackage(1, "Tidy Room", "taskName");

        taskObjectModel fakeTask = new taskObjectModel();
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
        TaskManagmentService service = new TaskManagmentService();
        AdaptorService adaptorService = mock(AdaptorService.class);
        service.adaptorService = adaptorService;

        long taskId = 1L;
        taskObjectModel fakeTask = new taskObjectModel();
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
        TaskManagmentService service = new TaskManagmentService();
        AdaptorService adaptorService = mock(AdaptorService.class);
        service.adaptorService = adaptorService;

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("ETHAN");

        ArrayList<taskObjectModel> expectedList = new ArrayList<>();
        expectedList.add(new taskObjectModel(1L, "ETHAN", "Tidy Room", "Chore", "Easy", "Pick up clothes and make the bed"));
        expectedList.add(new taskObjectModel(2L, "ETHAN", "Grocery Shopping", "Errand", "Medium", "Buy milk, eggs, bread"));
        expectedList.add(new taskObjectModel(3L, "ETHAN", "Write Report", "Work", "Hard", "Summarize Q3 project outcomes"));

        when(adaptorService.fetchAllTasksByOwner("ETHAN")).thenReturn(expectedList);

        // Act
        ArrayList<taskObjectModel> result = service.fetchAllTasks(authentication);

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
        TaskManagmentService service = new TaskManagmentService();
        AdaptorService adaptorService = mock(AdaptorService.class);
        service.adaptorService = adaptorService;

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("ETHAN");
        when(authentication.getRoles()).thenReturn(List.of("USER"));

        taskObjectModel expectedTask = new taskObjectModel(
                2L, "ETHAN", "Grocery Shopping", "Errand", "Medium", "Buy milk, eggs, bread");

        when(adaptorService.retrieveTask(2L)).thenReturn(expectedTask);

        // Act
        taskObjectModel result = service.fetchTaskWithId(2L, authentication);

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
        TaskManagmentService service = new TaskManagmentService();
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

        TaskManagmentService service = new TaskManagmentService();
        Authentication authenticationRole = mock(Authentication.class);
        Authentication authenticationOwner = mock(Authentication.class);

        taskObjectModel ethanTask = new taskObjectModel(
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