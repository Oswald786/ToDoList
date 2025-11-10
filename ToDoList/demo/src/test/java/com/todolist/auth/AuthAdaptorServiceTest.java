package com.todolist.auth;

import com.todolist.Models.userDetailsModel;
import com.todolist.adaptors.persistence.jpa.userEntity;
import com.todolist.adaptors.web.UserMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthAdaptorServiceTest {

    private AuthAdaptorService authAdaptorService;
    private EntityManager entityManager;
    private UserMapper userMapper;
    private TypedQuery<userEntity> typedQuery;

    @BeforeEach
    void setUp() {
        entityManager = mock(EntityManager.class);
        userMapper = mock(UserMapper.class);
        typedQuery = mock(TypedQuery.class);

        authAdaptorService = spy(new AuthAdaptorService());
        authAdaptorService.entityManager = entityManager;
        authAdaptorService.userMapper = userMapper;
    }

    // ===========================================================
    @Test
    @DisplayName("✅ findUser returns mapped user when found")
    void findUser_Success() {
        // Arrange
        userEntity userEntity = new userEntity();
        userEntity.setUserName("ETHAN");
        userEntity.setPassword("hashedPass");

        userDetailsModel expectedModel = new userDetailsModel();
        expectedModel.setUserName("ETHAN");
        expectedModel.setPassword("hashedPass");

        when(entityManager.createQuery(anyString(), eq(userEntity.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("username"), eq("ETHAN"))).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(userEntity);
        when(userMapper.toModel(userEntity)).thenReturn(expectedModel);

        // Act
        userDetailsModel result = authAdaptorService.findUser("ETHAN");

        // Assert
        assertNotNull(result);
        assertEquals("ETHAN", result.getUserName());
        verify(entityManager).createQuery(anyString(), eq(userEntity.class));
        verify(userMapper).toModel(userEntity);
    }

    // ===========================================================
    @Test
    @DisplayName("❌ findUser returns null when an exception occurs")
    void findUser_Exception_ReturnsNull() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(userEntity.class))).thenThrow(new RuntimeException("DB failure"));

        // Act
        userDetailsModel result = authAdaptorService.findUser("ETHAN");

        // Assert
        assertNull(result);
    }

    // ===========================================================
    @Test
    @DisplayName("✅ createUser persists new user entity")
    void createUser_Success() {
        // Arrange
        userDetailsModel model = new userDetailsModel();
        model.setUserName("ETHAN");

        userEntity entity = new userEntity();
        entity.setUserName("ETHAN");

        when(userMapper.toEntity(model)).thenReturn(entity);

        // Act
        authAdaptorService.createUser(model);

        // Assert
        verify(userMapper).toEntity(model);
        verify(entityManager).persist(entity);
    }

    // ===========================================================
    @Test
    @DisplayName("❌ createUser throws IllegalArgumentException on failure")
    void createUser_Fails_ThrowsException() {
        // Arrange
        userDetailsModel model = new userDetailsModel();
        model.setUserName("ETHAN");

        userEntity entity = new userEntity();
        entity.setUserName("ETHAN");

        when(userMapper.toEntity(model)).thenReturn(entity);
        doThrow(new RuntimeException("DB fail")).when(entityManager).persist(any());

        // Act & Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> authAdaptorService.createUser(model)
        );

        assertTrue(ex.getMessage().contains("already exists"));
    }

    // ===========================================================
    @Test
    @DisplayName("✅ updateUser merges user entity correctly")
    void updateUser_Success() {
        // Arrange
        userDetailsModel model = new userDetailsModel();
        model.setUserName("ETHAN");

        userEntity entity = new userEntity();
        entity.setUserName("ETHAN");

        when(userMapper.toEntity(model)).thenReturn(entity);

        // Act
        authAdaptorService.updateUser(model);

        // Assert
        verify(userMapper).toEntity(model);
        verify(entityManager).merge(entity);
    }

    // ===========================================================
    @Test
    @DisplayName("✅ deleteUser removes user entity successfully")
    void deleteUser_Success() {
        // Arrange
        userDetailsModel model = new userDetailsModel();
        model.setUserName("ETHAN");

        when(entityManager.find(userDetailsModel.class, "ETHAN")).thenReturn(model);

        // Act
        authAdaptorService.deleteUser("ETHAN");

        // Assert
        verify(entityManager).remove(model);
    }
}