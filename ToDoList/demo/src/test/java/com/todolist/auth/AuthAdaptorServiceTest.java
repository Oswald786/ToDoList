package com.todolist.auth;

import com.todolist.Models.UserDetailsModel;
import com.todolist.adaptors.persistence.Jpa.UserEntity;
import com.todolist.adaptors.web.UserMapper;
import com.todolist.exceptions.UserNotFoundException;
import io.micronaut.security.authentication.Authentication;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

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
    @DisplayName("findUser returns mapped user when found")
    void findUser_Success() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("ETHAN");
        userEntity.setPassword("hashedPass");

        UserDetailsModel expectedModel = new UserDetailsModel();
        expectedModel.setUsername("ETHAN");
        expectedModel.setPassword("hashedPass");

        when(entityManager.createQuery(anyString(), eq(UserEntity.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("username"), eq("ETHAN"))).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(userEntity);
        when(userMapper.toModel(userEntity)).thenReturn(expectedModel);

        // Act
        UserDetailsModel result = authAdaptorService.findUser("ETHAN");

        // Assert
        assertNotNull(result);
        assertEquals("ETHAN", result.getUsername());
        verify(entityManager).createQuery(anyString(), eq(UserEntity.class));
        verify(userMapper).toModel(userEntity);
    }

    // ===========================================================
    @Test
    @DisplayName("findUser returns null when no suer exists")
    void find_User_Exception_Returns_null_when_No_User_Exists() {

        assertThrows(NullPointerException.class,() -> authAdaptorService.findUser("ETHAN"));
    }

    // ===========================================================
    @Test
    @DisplayName("createUser persists new user entity")
    void createUser_Success() {
        // Arrange
        UserDetailsModel model = new UserDetailsModel();
        model.setUsername("ETHAN");
        model.setPassword("password");

        UserEntity entity = new UserEntity();
        entity.setUsername("ETHAN");
        entity.setPassword("password");

        when(userMapper.toEntity(model)).thenReturn(entity);

        // Act
        authAdaptorService.createUser(model);

        // Assert
        verify(userMapper).toEntity(model);
        verify(entityManager).persist(entity);
    }

    // ===========================================================
    @Test
    @DisplayName("createUser throws IllegalArgumentException on failure")
    void createUser_Fails_ThrowsException() {
        // Arrange
        UserDetailsModel model = new UserDetailsModel();
        model.setUsername("ETHAN");

        UserEntity entity = new UserEntity();
        entity.setUsername("ETHAN");

        when(userMapper.toEntity(model)).thenReturn(entity);
        doThrow(new RuntimeException("DB fail")).when(entityManager).persist(any());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authAdaptorService.createUser(model));
    }

    // ===========================================================
    @Test
    @DisplayName("updateUser merges user entity correctly")
    void updateUser_Success() {
        Authentication authentication = mock(Authentication.class);

        // Arrange
        UserDetailsModel model = new UserDetailsModel();
        model.setUsername("ETHAN");
        model.setPassword("password");
        model.setRole("ADMIN");
        model.setEmail("testemail@gmail.com");

        UserEntity entity = new UserEntity();
        entity.setUsername("ETHAN");

        when(authentication.getName()).thenReturn("ETHAN");
        when(authentication.getRoles()).thenReturn(List.of("ADMIN"));


        when(userMapper.toEntity(model)).thenReturn(entity);
        when(entityManager.find(UserEntity.class, "ETHAN")).thenReturn(entity);


        // Act
        authAdaptorService.updateUser(model,authentication);

        // Assert
        verify(userMapper).toEntity(model);
        verify(entityManager).merge(entity);
    }

    // ===========================================================
    @Test
    @DisplayName("deleteUser removes user entity successfully")
    void deleteUser_Success() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("ETHAN");
        when(authentication.getRoles()).thenReturn(List.of("ADMIN"));

        UserEntity model = new UserEntity();
        model.setUsername("ETHAN");
        model.setPassword("password");
        model.setRole("ADMIN");

        when(entityManager.find(UserEntity.class, "ETHAN")).thenReturn(model);

        // Act
        authAdaptorService.deleteUser("ETHAN", authentication);

        // Assert
        verify(entityManager).remove(model);
    }
}