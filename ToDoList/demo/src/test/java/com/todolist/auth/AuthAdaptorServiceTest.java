package com.todolist.auth;

import com.todolist.Models.UserDetailsModel;
import com.todolist.adaptors.persistence.Jpa.UserEntity;
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
    private TypedQuery<UserEntity> typedQuery;

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
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName("ETHAN");
        userEntity.setPassword("hashedPass");

        UserDetailsModel expectedModel = new UserDetailsModel();
        expectedModel.setUserName("ETHAN");
        expectedModel.setPassword("hashedPass");

        when(entityManager.createQuery(anyString(), eq(UserEntity.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("username"), eq("ETHAN"))).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(userEntity);
        when(userMapper.toModel(userEntity)).thenReturn(expectedModel);

        // Act
        UserDetailsModel result = authAdaptorService.findUser("ETHAN");

        // Assert
        assertNotNull(result);
        assertEquals("ETHAN", result.getUserName());
        verify(entityManager).createQuery(anyString(), eq(UserEntity.class));
        verify(userMapper).toModel(userEntity);
    }

    // ===========================================================
    @Test
    @DisplayName("❌ findUser returns null when an exception occurs")
    void findUser_Exception_ReturnsNull() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(UserEntity.class))).thenThrow(new RuntimeException("DB failure"));

        // Act
        UserDetailsModel result = authAdaptorService.findUser("ETHAN");

        // Assert
        assertNull(result);
    }

    // ===========================================================
    @Test
    @DisplayName("✅ createUser persists new user entity")
    void createUser_Success() {
        // Arrange
        UserDetailsModel model = new UserDetailsModel();
        model.setUserName("ETHAN");

        UserEntity entity = new UserEntity();
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
        UserDetailsModel model = new UserDetailsModel();
        model.setUserName("ETHAN");

        UserEntity entity = new UserEntity();
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
        UserDetailsModel model = new UserDetailsModel();
        model.setUserName("ETHAN");

        UserEntity entity = new UserEntity();
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
        UserDetailsModel model = new UserDetailsModel();
        model.setUserName("ETHAN");

        when(entityManager.find(UserDetailsModel.class, "ETHAN")).thenReturn(model);

        // Act
        authAdaptorService.deleteUser("ETHAN");

        // Assert
        verify(entityManager).remove(model);
    }
}