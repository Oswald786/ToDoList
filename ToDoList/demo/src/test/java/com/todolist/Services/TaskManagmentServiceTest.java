//package com.todolist.Services;
//
//import com.todolist.Models.taskObjectModel;
//import com.todolist.Models.updateTaskRequestPackage;
//import com.todolist.adaptors.persistence.jpa.TaskEntity;
//import com.todolist.adaptors.web.AdaptorService;
//import com.todolist.adaptors.web.TaskMapper;
//import com.todolist.adaptors.web.TaskMapperImpl;
//import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
//import jakarta.inject.Inject;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.TypedQuery;
//import org.junit.jupiter.api.*;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.Spy;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//class TaskManagmentServiceTest {
//
//    taskObjectModel valid;
//
//    taskObjectModel invalidNameNull;
//    taskObjectModel invalidNameEmpty;
//
//    taskObjectModel invalidTypeNull;
//    taskObjectModel invalidTypeEmpty;
//
//    taskObjectModel invalidLevelNull;
//    taskObjectModel invalidLevelEmpty;
//
//    taskObjectModel invalidDescNull;
//    taskObjectModel invalidDescEmpty;
//
//    java.util.List<taskObjectModel> allInvalid;
//
//
//    @BeforeEach
//    void setUp() {
//        // Valid baseline
//        valid = new taskObjectModel(1L,"ETHAN", "Test Task", "Test Type", "Test Level", "Test Description");
//
//        // Invalid: task name
//        invalidNameNull  = new taskObjectModel(2L,"ETHAN", null,        "Test Type", "Test Level", "Test Description");
//        invalidNameEmpty = new taskObjectModel(3L,"ETHAN", "",          "Test Type", "Test Level", "Test Description");
//
//        // Invalid: task type
//        invalidTypeNull  = new taskObjectModel(4L,"ETHAN", "Test Task", null,        "Test Level", "Test Description");
//        invalidTypeEmpty = new taskObjectModel(5L,"ETHAN", "Test Task", "",          "Test Level", "Test Description");
//
//        // Invalid: task level
//        invalidLevelNull  = new taskObjectModel(6L,"ETHAN", "Test Task", "Test Type", null,        "Test Description");
//        invalidLevelEmpty = new taskObjectModel(7L,"ETHAN", "Test Task", "Test Type", "",          "Test Description");
//
//        // Invalid: task description
//        invalidDescNull  = new taskObjectModel(8L,"ETHAN", "Test Task", "Test Type", "Test Level", null);
//        invalidDescEmpty = new taskObjectModel(9L,"ETHAN", "Test Task", "Test Type", "Test Level", "");
//
//        // Convenience collection
//        allInvalid = java.util.List.of(
//                invalidNameNull, invalidNameEmpty,
//                invalidTypeNull, invalidTypeEmpty,
//                invalidLevelNull, invalidLevelEmpty,
//                invalidDescNull, invalidDescEmpty
//        );
//    }
//
//    @Test
//    @DisplayName("Test createTask interacts with entity manager as expected")
//    void createTask() {
//        //Arrange
//        TaskManagmentService service = mock(TaskManagmentService.class);
//        taskObjectModel taskObjectModel = new taskObjectModel();
//        taskObjectModel.setTaskName("Test Task");
//        taskObjectModel.setTaskType("Test Type");
//        taskObjectModel.setTaskDescription("Test Description");
//        taskObjectModel.setTaskLevel("Test Level");
//        //Act
//        service.createTask(taskObjectModel);
//        //Assert
//        Mockito.verify(service).createTask(taskObjectModel);
//        Mockito.verifyNoMoreInteractions(service);
//    }
//
//    @Test
//    @DisplayName("Test createTask validates task correctly")
//    void createTask_ValidatesTaskCorrectly() {
//        //Arrange
//        TaskManagmentService service = spy(new TaskManagmentService());
//        taskObjectModel CorrecttaskObjectModel = new taskObjectModel();
//        CorrecttaskObjectModel.setTaskName("Test Task");
//        CorrecttaskObjectModel.setTaskType("Test Type");
//        CorrecttaskObjectModel.setTaskDescription("Test Description");
//        CorrecttaskObjectModel.setTaskLevel("Test Level");
//
//
//
//        //Act
//        boolean result = service.validateTaskObjectModel(CorrecttaskObjectModel);
//        boolean result2 = service.validateTaskObjectModel(invalidNameNull);
//        boolean result3 = service.validateTaskObjectModel(invalidNameEmpty);
//        boolean result4 = service.validateTaskObjectModel(invalidTypeNull);
//        boolean result5 = service.validateTaskObjectModel(invalidTypeEmpty);
//        boolean result6 = service.validateTaskObjectModel(invalidLevelNull);
//        boolean result7 = service.validateTaskObjectModel(invalidLevelEmpty);
//        boolean result8 = service.validateTaskObjectModel(invalidDescNull);
//        boolean result9 = service.validateTaskObjectModel(invalidDescEmpty);
//        //Assert
//        assertTrue(result);
//        assertFalse(result2);
//        assertFalse(result3);
//        assertFalse(result4);
//        assertFalse(result5);
//        assertFalse(result6);
//        assertFalse(result7);
//        assertFalse(result8);
//        assertFalse(result9);
//
//        Mockito.verify(service,Mockito.atMost(9)).validateTaskObjectModel(CorrecttaskObjectModel);
//
//    }
//
//    @Test
//    @DisplayName("Test throws Error when entity not found")
//    void entityNotFoundWhenTryingToUpdateTask() {
//        //Arrange
//        TaskManagmentService service = new TaskManagmentService();
//        service.adaptorService = new AdaptorService();
//        EntityManager entityManager = mock(EntityManager.class);
//        service.adaptorService.setEntityManager(entityManager);
//        updateTaskRequestPackage updateTaskRequestPackage = mock(updateTaskRequestPackage.class);
//
//
//        when(entityManager.find(eq(TaskEntity.class), anyLong())).thenReturn(null);
//
//
//        Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            service.updateTask(updateTaskRequestPackage);
//        });
//
//    }
//
//    @Test
//    @DisplayName("Test throws Error when field not found")
//    void testThrowsErrorWhenFieldNotFound() {
//        //Arrange
//        TaskManagmentService service = new TaskManagmentService();
//        service.adaptorService = new AdaptorService();
//        EntityManager entityManager = mock(EntityManager.class);
//        service.adaptorService.setEntityManager(entityManager);
//        updateTaskRequestPackage req = new updateTaskRequestPackage(1L, "taskName", null);
//        TaskEntity taskEntity = mock(TaskEntity.class);
//
//
//        when(entityManager.find(eq(TaskEntity.class), anyLong())).thenReturn(taskEntity);
//
//
//        Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            service.updateTask(req);
//        });
//    }
//
//
//    @Test
//    @DisplayName("Test throws Error when replacement not found")
//    void testThrowsErrorWhenReplacementNotFound() {
//        //Arrange
//        TaskManagmentService service = new TaskManagmentService();
//        service.adaptorService = new AdaptorService();
//        EntityManager entityManager = mock(EntityManager.class);
//        service.adaptorService.setEntityManager(entityManager);
//        updateTaskRequestPackage req = new updateTaskRequestPackage(1L, null, "Task Type");
//        TaskEntity taskEntity = mock(TaskEntity.class);
//
//
//        when(entityManager.find(eq(TaskEntity.class), anyLong())).thenReturn(taskEntity);
//
//
//        Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            service.updateTask(req);
//        });
//
//    }
//
//
//    @Test
//    @DisplayName("Test updateTask updates task name correctly")
//    void testUpdateTaskUpdatesTaskNameCorrectly() {
//        //Arrange
//        TaskManagmentService service = new TaskManagmentService();
//        service.adaptorService = new AdaptorService();
//        EntityManager entityManager = mock(EntityManager.class);
//        service.adaptorService.setEntityManager(entityManager);
//        updateTaskRequestPackage req = new updateTaskRequestPackage(1L, "Tidy Room", "taskName");
//        TaskEntity taskEntity = spy(new TaskEntity());
//        taskEntity.setId(1L);
//        taskEntity.setTaskName("Fake Name");
//        taskEntity.setTaskType("Fake Type");
//        taskEntity.setTaskLevel("Fake Level");
//        taskEntity.setTaskDescription("Fake Description");
//
//
//
//
//        when(entityManager.find(eq(TaskEntity.class), anyLong())).thenReturn(taskEntity);
//
//        //Act
//        service.updateTask(req);
//        //Assert
//        Mockito.verify(taskEntity).setTaskName("Tidy Room");
//        assertEquals("Tidy Room", taskEntity.getTaskName());
//
//    }
//
//    @Test
//    @DisplayName("Test updateTask updates task type correctly")
//    void testUpdateTaskUpdatesTaskTypeCorrectly() {
//        //Arrange
//        TaskManagmentService service = new TaskManagmentService();
//        service.adaptorService = new AdaptorService();
//        EntityManager entityManager = mock(EntityManager.class);
//        service.adaptorService.setEntityManager(entityManager);
//        updateTaskRequestPackage req = new updateTaskRequestPackage(1L, "Cleaning", "taskType");
//        TaskEntity taskEntity = spy(new TaskEntity());
//        taskEntity.setId(1L);
//        taskEntity.setTaskName("Fake Name");
//        taskEntity.setTaskType("Fake Type");
//        taskEntity.setTaskLevel("Fake Level");
//        taskEntity.setTaskDescription("Fake Description");
//
//        when(entityManager.find(eq(TaskEntity.class), anyLong())).thenReturn(taskEntity);
//
//        //Act
//        service.updateTask(req);
//        //Assert
//        Mockito.verify(taskEntity).setTaskType("Cleaning");
//        assertEquals("Cleaning", taskEntity.getTaskType());
//    }
//
//    @Test
//    @DisplayName("Test updateTask updates task level correctly")
//    void testUpdateTaskUpdatesTaskLevelCorrectly() {
//        //Arrange
//        TaskManagmentService service = new TaskManagmentService();
//        service.adaptorService = new AdaptorService();
//        EntityManager entityManager = mock(EntityManager.class);
//        service.adaptorService.setEntityManager(entityManager);
//        updateTaskRequestPackage req = new updateTaskRequestPackage(1L, "8", "taskLevel");
//        TaskEntity taskEntity = spy(new TaskEntity());
//        taskEntity.setId(1L);
//        taskEntity.setTaskName("Fake Name");
//        taskEntity.setTaskType("Fake Type");
//        taskEntity.setTaskLevel("Fake Level");
//        taskEntity.setTaskDescription("Fake Description");
//
//        when(entityManager.find(eq(TaskEntity.class), anyLong())).thenReturn(taskEntity);
//
//        //Act
//        service.updateTask(req);
//        //Assert
//        Mockito.verify(taskEntity).setTaskLevel("8");
//        assertEquals("8", taskEntity.getTaskLevel());
//    }
//
//    @Test
//    @DisplayName("Test updateTask updates task description correctly")
//    void testUpdateTaskUpdatesTaskDescriptionCorrectly() {
//        //Arrange
//        TaskManagmentService service = new TaskManagmentService();
//        service.adaptorService = new AdaptorService();
//        EntityManager entityManager = mock(EntityManager.class);
//        service.adaptorService.setEntityManager(entityManager);
//        updateTaskRequestPackage req = new updateTaskRequestPackage(1L, "get the bins out", "taskDescription");
//        TaskEntity taskEntity = spy(new TaskEntity());
//        taskEntity.setId(1L);
//        taskEntity.setTaskName("Fake Name");
//        taskEntity.setTaskType("Fake Type");
//        taskEntity.setTaskLevel("Fake Level");
//        taskEntity.setTaskDescription("Fake Description");
//
//        when(entityManager.find(eq(TaskEntity.class), anyLong())).thenReturn(taskEntity);
//
//        //Act
//        service.updateTask(req);
//        //Assert
//        Mockito.verify(taskEntity).setTaskDescription("get the bins out");
//        assertEquals("get the bins out", taskEntity.getTaskDescription());
//    }
//
//    @Test
//    @DisplayName("Test updateTask throws error when entity not found")
//    void deleteTask() {
//
//        //Arrange
//        TaskManagmentService service = new TaskManagmentService();
//        service.adaptorService = new AdaptorService();
//        EntityManager entityManager = spy(EntityManager.class);
//        service.adaptorService.setEntityManager(entityManager);
//        updateTaskRequestPackage req = new updateTaskRequestPackage(1L, "get the bins out", "taskDescription");
//        TaskEntity taskEntity = spy(new TaskEntity());
//        taskEntity.setId(1L);
//        taskEntity.setTaskName("Fake Name");
//        taskEntity.setTaskType("Fake Type");
//        taskEntity.setTaskLevel("Fake Level");
//        taskEntity.setTaskDescription("Fake Description");
//
//        when(entityManager.find(eq(TaskEntity.class), anyLong())).thenReturn(taskEntity);
//
//        //Act
//        service.deleteTask(1L);
//        //Assert
//        Mockito.verify(entityManager).remove(taskEntity);
//    }
//
//    @Test
//    @DisplayName("Tests all tasks are fetched correctly")
//    void fetchAllTasks() {
//        List<TaskEntity> resultList = getTaskEntities();
//        TaskManagmentService service = new TaskManagmentService();
//        service.adaptorService = new AdaptorService();
//        EntityManager entityManager = spy(EntityManager.class);
//        service.adaptorService.setEntityManager(entityManager);
//        TaskMapper taskMapper = new TaskMapperImpl();
//        service.adaptorService.setTaskMapper(taskMapper);
//
//        TypedQuery<TaskEntity> query = mock(TypedQuery.class);
//        when(entityManager.createQuery("SELECT t FROM TaskEntity t", TaskEntity.class)).thenReturn(query);
//        when(query.getResultList()).thenReturn(resultList);
//
//
//        //act
//        ArrayList<taskObjectModel> returnedList = service.fetchAllTasks();
//
//        //assert
//        assertEquals(3, returnedList.size());
//        assertEquals(resultList.get(0).getTaskName(), returnedList.get(0).getTaskName());
//        assertEquals(resultList.get(1).getTaskName(), returnedList.get(1).getTaskName());
//        assertEquals(resultList.get(2).getTaskName(), returnedList.get(2).getTaskName());
//        assertEquals(resultList.get(0).getTaskType(), returnedList.get(0).getTaskType());
//        assertEquals(resultList.get(1).getTaskType(), returnedList.get(1).getTaskType());
//        assertEquals(resultList.get(2).getTaskType(), returnedList.get(2).getTaskType());
//        assertEquals(resultList.get(0).getTaskLevel(), returnedList.get(0).getTaskLevel());
//        assertEquals(resultList.get(1).getTaskLevel(), returnedList.get(1).getTaskLevel());
//        assertEquals(resultList.get(2).getTaskLevel(), returnedList.get(2).getTaskLevel());
//        assertEquals(resultList.get(0).getTaskDescription(), returnedList.get(0).getTaskDescription());
//        assertEquals(resultList.get(1).getTaskDescription(), returnedList.get(1).getTaskDescription());
//        assertEquals(resultList.get(2).getTaskDescription(), returnedList.get(2).getTaskDescription());
//        assertEquals(resultList.get(0).getId(), returnedList.get(0).getId());
//        assertEquals(resultList.get(1).getId(), returnedList.get(1).getId());
//        assertEquals(resultList.get(2).getId(), returnedList.get(2).getId());
//        Mockito.verify(entityManager).createQuery("SELECT t FROM TaskEntity t", TaskEntity.class);
//    }
//
//    private static List<TaskEntity> getTaskEntities() {
//        List<TaskEntity> resultList = new ArrayList<>();
//
//        TaskEntity t1 = new TaskEntity();
//        t1.setId(1L);
//        t1.setTaskOwnerId("ETHAN");
//        t1.setTaskName("Tidy Room");
//        t1.setTaskType("Chore");
//        t1.setTaskLevel("Easy");
//        t1.setTaskDescription("Pick up clothes and make the bed");
//        resultList.add(t1);
//
//        TaskEntity t2 = new TaskEntity();
//        t2.setId(2L);
//        t2.setTaskOwnerId("ETHAN");
//        t2.setTaskName("Grocery Shopping");
//        t2.setTaskType("Errand");
//        t2.setTaskLevel("Medium");
//        t2.setTaskDescription("Buy milk, eggs, bread");
//        resultList.add(t2);
//
//        TaskEntity t3 = new TaskEntity();
//        t3.setId(3L);
//        t3.setTaskOwnerId("ETHAN");
//        t3.setTaskName("Write Report");
//        t3.setTaskType("Work");
//        t3.setTaskLevel("Hard");
//        t3.setTaskDescription("Summarize Q3 project outcomes");
//        resultList.add(t3);
//        return resultList;
//    }
//
//    @Test
//    @DisplayName("Tests task is fetched correctly Based on id")
//    void fetchTaskWithId() {
//        List<TaskEntity> resultList = getTaskEntities();
//        TaskManagmentService service = new TaskManagmentService();
//        service.adaptorService = new AdaptorService();
//        EntityManager entityManager = spy(EntityManager.class);
//        service.adaptorService.setEntityManager(entityManager);
//        TaskMapper taskMapper = new TaskMapperImpl();
//        service.adaptorService.setTaskMapper(taskMapper);
//
//        //Act
//        when(entityManager.find(TaskEntity.class, 2L)).thenReturn(resultList.get(1));
//        taskObjectModel returnedModel = service.fetchTaskWithId(2L);
//
//
//        //Assert
//        Mockito.verify(entityManager).find(TaskEntity.class, 2L);
//        Assertions.assertEquals(resultList.get(1).getTaskName(), returnedModel.getTaskName());
//        Assertions.assertEquals(resultList.get(1).getTaskType(), returnedModel.getTaskType());
//        Assertions.assertEquals(resultList.get(1).getTaskLevel(), returnedModel.getTaskLevel());
//        Assertions.assertEquals(resultList.get(1).getTaskDescription(), returnedModel.getTaskDescription());
//        Assertions.assertEquals(resultList.get(1).getId(), returnedModel.getId());
//    }
//
//    @Test
//    @DisplayName("Task throws and logs error when nothing is returned")
//    void taskThrowsErrorWhenEntityManagerFailsToReturnAnything(){
//        List<TaskEntity> resultList = getTaskEntities();
//        TaskManagmentService service = new TaskManagmentService();
//        service.adaptorService = new AdaptorService();
//        EntityManager entityManager = spy(EntityManager.class);
//        service.adaptorService.setEntityManager(entityManager);
//        TaskMapper taskMapper = new TaskMapperImpl();
//        service.adaptorService.setTaskMapper(taskMapper);
//
//        //Act
//        when(entityManager.find(TaskEntity.class, 2L)).thenReturn(null);
//        taskObjectModel returnedModel = service.fetchTaskWithId(2L);
//        Assertions.assertNull(returnedModel);
//    }
//}