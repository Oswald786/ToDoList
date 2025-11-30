package com.todolist.Services;

import com.todolist.Models.PlayerStatsModel;
import com.todolist.Models.TaskObjectModel;
import com.todolist.Models.UpdateTaskRequestPackage;
import com.todolist.adaptors.persistence.Jpa.PlayerStatsEntity;
import com.todolist.adaptors.persistence.Jpa.TaskEntity;
import com.todolist.adaptors.web.AdaptorService;
import com.todolist.adaptors.web.AdaptorServicePlayerStats;
import com.todolist.auth.AuthenticationService;
import com.todolist.exceptions.PermissionDeniedException;
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

    private final AuthenticationService authenticationService;
    private final TaskManagementService taskManagementService;
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

    @Mock
    AdaptorServicePlayerStats mockAdaptorServicePlayerStats;


    @InjectMocks
    TaskManagementService service;

    TaskManagmentServiceTest(AuthenticationService authenticationService, TaskManagementService taskManagementService) {
        this.authenticationService = authenticationService;
        this.taskManagementService = taskManagementService;
    }


    @BeforeEach
    void setUp() {
        // Valid baseline
        valid = new TaskObjectModel(1L, "ETHAN", "Test Task", "Test Type", "Test Level", "Test Description");

        // Invalid: task name
        invalidNameNull = new TaskObjectModel(2L, "ETHAN", null, "Test Type", "Test Level", "Test Description");
        invalidNameEmpty = new TaskObjectModel(3L, "ETHAN", "", "Test Type", "Test Level", "Test Description");

        // Invalid: task type
        invalidTypeNull = new TaskObjectModel(4L, "ETHAN", "Test Task", null, "Test Level", "Test Description");
        invalidTypeEmpty = new TaskObjectModel(5L, "ETHAN", "Test Task", "", "Test Level", "Test Description");

        // Invalid: task level
        invalidLevelNull = new TaskObjectModel(6L, "ETHAN", "Test Task", "Test Type", null, "Test Description");
        invalidLevelEmpty = new TaskObjectModel(7L, "ETHAN", "Test Task", "Test Type", "", "Test Description");

        // Invalid: task description
        invalidDescNull = new TaskObjectModel(8L, "ETHAN", "Test Task", "Test Type", "Test Level", null);
        invalidDescEmpty = new TaskObjectModel(9L, "ETHAN", "Test Task", "Test Type", "Test Level", "");

        // Convenience collection
        allInvalid = java.util.List.of(
                invalidNameNull, invalidNameEmpty,
                invalidTypeNull, invalidTypeEmpty,
                invalidLevelNull, invalidLevelEmpty,
                invalidDescNull, invalidDescEmpty
        );
    }

    //Below is create Task method Testing
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
        Assertions.assertEquals("ETHAN", taskObjectModel.getTaskOwnerId());
        Mockito.verify(adaptorService, times(1)).createTask(taskObjectModel);
    }

    //Below is update Task method Testing
    @Test
    @DisplayName("Update task will throw illegal state exception when authentication is null and task wont update")
    void updateTask_throwsIllegalState_whenAuthenticationIsNull() {
        //Arrange
        Authentication authentication = null;
        UpdateTaskRequestPackage updateTaskRequestPackage = mock(UpdateTaskRequestPackage.class);

        //Assert
        assertThrows(IllegalStateException.class, () -> service.updateTask(updateTaskRequestPackage, authentication));
        verifyNoInteractions(service.adaptorService);
    }

    @Test
    @DisplayName("UpdateTask will throw an IllegalStateException if the update request is null therefore the task fails to update.")
    void updateTask_throwsIllegalState_whenUpdateTaskRequestPackageIsNull() {
        //Arrange
        Authentication authentication = this.mockAuthentication;
        UpdateTaskRequestPackage updateTaskRequestPackage = null;

        //Assert
        assertThrows(IllegalStateException.class, () -> service.updateTask(updateTaskRequestPackage, authentication));
        verifyNoInteractions(service.adaptorService);
    }

    @Test
    @DisplayName("UpdateTask will throw an IllegalArgumentException if the target field or replacement value is null or blank.")
    void updateTask_throwsIllegalArgumentException_whenUpdateTaskRequestPackageContainsNullOrBlankFields() {
        //Arrange
        Authentication authentication = this.mockAuthentication;
        UpdateTaskRequestPackage updatePackage_ReplacementNull = new UpdateTaskRequestPackage(1L, null, "taskType");
        UpdateTaskRequestPackage updatePackage_ReplacementBlank = new UpdateTaskRequestPackage(1L, "", "taskType");
        UpdateTaskRequestPackage updatePackage_FieldNull = new UpdateTaskRequestPackage(1L, "New Value", null);
        UpdateTaskRequestPackage updatePackage_FieldBlank = new UpdateTaskRequestPackage(1L, "New Value", "");

        //Act and Assert
        for (UpdateTaskRequestPackage updatePackage : List.of(updatePackage_ReplacementNull, updatePackage_ReplacementBlank, updatePackage_FieldNull, updatePackage_FieldBlank)) {
            Assertions.assertThrows(IllegalArgumentException.class, () -> service.updateTask(updatePackage, authentication));
        }
        verifyNoInteractions(service.adaptorService);

    }

    @Test
    @DisplayName("UpdateTask throws PermissionDeniedException when the authentication does not contain an allowed role and is not task owner")
    void updateTask_throwsPermissionDeniedException_whenAuthenticationLacksAllowedRole() {
        //Arrange
        TaskObjectModel taskOwnedByUser = new TaskObjectModel(1L, "user123", "Test Task", "GENERAL", "LOW", "This is a sample task");
        when(adaptorService.retrieveTask(1L)).thenReturn(taskOwnedByUser);
        when(mockAuthentication.getRoles()).thenReturn(List.of("Invalid role"));
        when(mockAuthentication.getName()).thenReturn("user123456");
        UpdateTaskRequestPackage updateTaskRequestPackage = new UpdateTaskRequestPackage(1L, "New Value", "taskType");

        //Act and Assert
        Assertions.assertThrows(PermissionDeniedException.class, () -> service.updateTask(updateTaskRequestPackage, this.mockAuthentication));
        verify(adaptorService, times(1)).retrieveTask(1L);
        verify(adaptorService, never()).updateTask(eq(updateTaskRequestPackage));
    }

    @Test
    @DisplayName("UpdateTask updates task when valid parameters and permissions are present")
    void updateTask_Updates_Task_When_Valid_Parameters_And_Permissions_Are_Present() {
        //Arrange
        TaskObjectModel taskOwnedByUser = new TaskObjectModel(1L, "user123", "Test Task", "GENERAL", "LOW", "This is a sample task");
        when(adaptorService.retrieveTask(1L)).thenReturn(taskOwnedByUser);
        when(mockAuthentication.getRoles()).thenReturn(List.of("USER"));
        when(mockAuthentication.getName()).thenReturn("user123");
        UpdateTaskRequestPackage updateTaskRequestPackage = new UpdateTaskRequestPackage(1L, "New Value", "taskType");

        //Act
        service.updateTask(updateTaskRequestPackage, this.mockAuthentication);
        //Assert
        verify(adaptorService, times(1)).retrieveTask(1L);
        verify(adaptorService, times(1)).updateTask(eq(updateTaskRequestPackage));
        verify(adaptorService, never()).createTask(any());
    }

    //Delete Task method testing

    @Test
    @DisplayName("deleteTask throws IllegalStateException when authentication is null")
    void delete_Task_throws_IllegalState_when_Authentication_Is_Null() {
        // Arrange
        this.mockAuthentication = null;
        //Act and Assert
        assertThrows(IllegalStateException.class, () -> service.deleteTask(1L, mockAuthentication));
    }

    @Test
    @DisplayName("deleteTask denies access when user is not owner and lacks allowed roles")
    void deleteTask_denies_access_when_user_is_not_owner_and_lacks_allowed_roles() {
        this.mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getName()).thenReturn("user123456");
        when(mockAuthentication.getRoles()).thenReturn(List.of("INVALID ROLE"));
        when(adaptorService.retrieveTask(1L)).thenReturn(new TaskObjectModel(1L, "user123", "Test Task", "GENERAL", "LOW", "This is a sample task"));

        //Act and Assert
        Assertions.assertThrows(PermissionDeniedException.class, () -> service.deleteTask(1L, this.mockAuthentication));
    }

    @Test
    @DisplayName("deleteTask deletes task and awards XP when user has valid permissions")
    void deleteTask_deletes_task_and_awards_XP_when_user_has_valid_permissions() {
        //Arrange
        this.mockAuthentication = mock(Authentication.class);
        TaskObjectModel taskOwnedByUser = new TaskObjectModel(1L, "user123456", "Test Task", "GENERAL", "LOW", "This is a sample task");

        when(mockAuthentication.getName()).thenReturn("user123456");
        when(mockAuthentication.getRoles()).thenReturn(List.of("USER"));
        when(adaptorService.retrieveTask(1L)).thenReturn(taskOwnedByUser);

        // Mock the gameService method directly instead of the nested field
        doNothing().when(gameService).addXPForTaskCompletion(taskOwnedByUser, this.mockAuthentication);

        // Act
        service.deleteTask(1L, this.mockAuthentication);

        // Assert
        verify(adaptorService, times(1)).retrieveTask(1L);
        verify(adaptorService, times(1)).deleteTask(1L);
        verify(gameService, times(1)).addXPForTaskCompletion(taskOwnedByUser, this.mockAuthentication);

    }

    //Fetch all tasks method testing
    @Test
    @DisplayName("fetchAllTasks throws IllegalStateException when authentication is null")
    void fetchAllTasks_throws_IllegalState_when_Authentication_Is_Null() {
        // Arrange
        this.mockAuthentication = null;
        //Act and Assert
        assertThrows(IllegalStateException.class, () -> service.fetchAllTasks(mockAuthentication));

    }

    @Test
    @DisplayName("fetchAllTasks calls adaptor and returns tasks when authentication is valid")
    void fetchAllTasks_calls_adaptor_and_returns_tasks_when_authentication_is_valid() {
        //Arrange
        when(mockAuthentication.getName()).thenReturn("user123456");
        when(adaptorService.fetchAllTasksByOwner(anyString())).thenReturn(new ArrayList<>());

        //Act
        List<TaskObjectModel> result = service.fetchAllTasks(mockAuthentication);
        verify(adaptorService, times(1)).fetchAllTasksByOwner(anyString());
        verifyNoMoreInteractions(adaptorService);

    }

    //Fetch task with id method testing

    @Test
    @DisplayName("fetchTaskWithId denies access when user lacks ownership or valid role")
    void fetchTaskWithId_denies_access_when_user_lacks_ownership_or_valid_role() {
        //Arrange
        this.mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getName()).thenReturn("user123456");
        when(mockAuthentication.getRoles()).thenReturn(List.of("INVALID ROLE"));
        when(adaptorService.retrieveTask(1L)).thenReturn(new TaskObjectModel(1L, "user123", "Test Task", "GENERAL", "LOW", "This is a sample task"));

        //Act and Assert
        Assertions.assertThrows(PermissionDeniedException.class, () -> service.fetchTaskWithId(1L, this.mockAuthentication));
    }

    @Test
    @DisplayName("fetchTaskWithId returns task when user has correct ownership and roles")
    void fetchTaskWithId_returns_task_when_user_has_correct_ownership_and_roles() {
        //Arrange
        this.mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getName()).thenReturn("user123456");
        when(mockAuthentication.getRoles()).thenReturn(List.of("USER"));
        when(adaptorService.retrieveTask(1L)).thenReturn(new TaskObjectModel(1L, "user123456", "Test Task", "GENERAL", "LOW", "This is a sample task"));

        //Act
        TaskObjectModel result = service.fetchTaskWithId(1L, this.mockAuthentication);

        //Assert
        verify(adaptorService, times(1)).retrieveTask(1L);
        Assertions.assertEquals("user123456", result.getTaskOwnerId());
        Assertions.assertEquals("Test Task", result.getTaskName());
        Assertions.assertEquals("GENERAL", result.getTaskType());
        Assertions.assertEquals("LOW", result.getTaskLevel());
        Assertions.assertEquals("This is a sample task", result.getTaskDescription());
        Assertions.assertEquals(1L, result.getId());
        verifyNoMoreInteractions(adaptorService);
    }

    //Validate Task Object Model method testing
    @Test
    @DisplayName("validateTaskObjectModel returns nothing when valid task object model is passed")
    void validateTaskObjectModel_returns_true_when_valid_task_object_model_is_passed() {
        //Arrange
        TaskObjectModel validTask = new TaskObjectModel(1L, "user123456", "Test Task", "GENERAL", "LOW", "This is a valid task description");

        //Act
        Assertions.assertDoesNotThrow(() -> taskManagementService.validateTaskObjectModel(validTask, this.mockAuthentication));

    }

    @Test
    @DisplayName("validateTaskObjectModel throws IllegalArgumentException when task fields are missing or empty")
    void validateTaskObjectModel_throws_IllegalArgumentException_when_task_fields_are_missing_or_empty() {
        //Arrange + Act + Assert
        for (TaskObjectModel invalidTask : this.allInvalid) {
            assertThrows(IllegalArgumentException.class, () -> taskManagementService.validateTaskObjectModel(invalidTask, this.mockAuthentication));
        }
    }


    //Validating task ownership and authority

    @Test
    @DisplayName("validateTaskOwnershipAndAuthority returns true when user is owner and has allowed role")
    void validateTaskOwnershipAndAuthority_returns_true_when_user_is_owner_and_has_allowed_role() {
        // Arrange
        TaskObjectModel task = new TaskObjectModel(1L, "user123", "Test", "GENERAL", "LOW", "desc");
        Authentication auth = mock(Authentication.class);

        when(auth.getName()).thenReturn("user123");
        when(auth.getRoles()).thenReturn(List.of("ADMIN"));

        // Act
        boolean result = service.validateTaskOwnershipAndAuthority(task, auth, List.of("ADMIN", "USER"));

        // Assert
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("validateTaskOwnershipAndAuthority returns true when user owns the task even without allowed role")
    void validateTaskOwnershipAndAuthority_returns_true_when_user_is_owner_even_without_allowed_role() {
        // Arrange
        TaskObjectModel task = new TaskObjectModel(1L, "user123", "Test", "GENERAL", "LOW", "desc");
        Authentication auth = mock(Authentication.class);

        when(auth.getName()).thenReturn("user123");
        when(auth.getRoles()).thenReturn(List.of("INVALID"));

        // Act
        boolean result = service.validateTaskOwnershipAndAuthority(task, auth, List.of("ADMIN", "USER"));

        // Assert
        Assertions.assertTrue(result);
    }

    @DisplayName("validateTaskOwnershipAndAuthority returns true when user has allowed role even if not owner")
    void validateTaskOwnershipAndAuthority_returns_true_when_user_has_allowed_role_even_if_not_owner() {
        // Arrange
        TaskObjectModel task = new TaskObjectModel(1L, "differentUser", "Test", "GENERAL", "LOW", "desc");
        Authentication auth = mock(Authentication.class);

        when(auth.getName()).thenReturn("user123");
        when(auth.getRoles()).thenReturn(List.of("ADMIN"));

        // Act
        boolean result = service.validateTaskOwnershipAndAuthority(task, auth, List.of("ADMIN", "USER"));

        // Assert
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("validateTaskOwnershipAndAuthority returns false when user is not owner and has no allowed role")
    void validateTaskOwnershipAndAuthority_returns_false_when_user_is_not_owner_and_has_no_allowed_role() {
        // Arrange
        TaskObjectModel task = new TaskObjectModel(1L, "differentUser", "Test", "GENERAL", "LOW", "desc");
        Authentication auth = mock(Authentication.class);

        when(auth.getName()).thenReturn("user123");
        when(auth.getRoles()).thenReturn(List.of("INVALID"));

        // Act
        boolean result = service.validateTaskOwnershipAndAuthority(task, auth, List.of("ADMIN", "USER"));

        // Assert
        Assertions.assertFalse(result);
    }
}
