package com.todolist.auth;

import com.todolist.Models.UserDetailsModel;
import com.todolist.adaptors.persistence.Jpa.UserEntity;
import com.todolist.adaptors.web.UserMapper;
import com.todolist.exceptions.PermissionDeniedException;
import com.todolist.exceptions.UserAlreadyExistsException;
import com.todolist.exceptions.UserNotFoundException;
import io.micronaut.security.authentication.Authentication;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class AuthAdaptorServiceTest {


    @Mock
    EntityManager entityManager;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    AuthAdaptorService authAdaptorService;

    // ===========================================================
    // findUser() tests
    // ===========================================================

    @Test
    @DisplayName("findUser returns mapped user when found")
    void findUser_success() {
        // Arrange
        UserEntity entity = new UserEntity();
        entity.setUsername("ETHAN");
        entity.setPassword("hashedPass");

        UserDetailsModel expected = new UserDetailsModel();
        expected.setUsername("ETHAN");
        expected.setPassword("hashedPass");

        TypedQuery<UserEntity> query = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(UserEntity.class))).thenReturn(query);
        when(query.setParameter("username", "ETHAN")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(entity);
        when(userMapper.toModel(entity)).thenReturn(expected);

        // Act
        UserDetailsModel result = authAdaptorService.findUser("ETHAN");

        // Assert
        assertNotNull(result);
        assertEquals("ETHAN", result.getUsername());
        verify(userMapper).toModel(entity);
    }

    @Test
    @DisplayName("findUser throws UserNotFoundException when user does not exist")
    void findUser_userNotFound() {
        // Arrange
        TypedQuery<UserEntity> query = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(UserEntity.class))).thenReturn(query);
        when(query.setParameter("username", "GHOST")).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> authAdaptorService.findUser("GHOST"));
    }

    // ===========================================================
    // createUser() tests
    // ===========================================================

    @Test
    @DisplayName("createUser persists a new user successfully")
    void createUser_success() {
        // Arrange
        UserDetailsModel model = new UserDetailsModel();
        model.setUsername("ETHAN");
        model.setPassword("password123");

        UserEntity entity = new UserEntity();

        when(userMapper.toEntity(model)).thenReturn(entity);

        // Act
        authAdaptorService.createUser(model);

        // Assert
        verify(entityManager).persist(entity);
    }

    @Test
    @DisplayName("createUser throws IllegalArgumentException when validation fails")
    void createUser_validationFails() {
        // Arrange
        UserDetailsModel model = new UserDetailsModel();
        model.setUsername("ETHAN");
        model.setPassword("short");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authAdaptorService.createUser(model));
    }

    @Test
    @DisplayName("createUser throws UserAlreadyExistsException on constraint violation")
    void createUser_constraintViolation() {
        // Arrange
        UserDetailsModel model = new UserDetailsModel();
        model.setUsername("ETHAN");
        model.setPassword("password123");

        UserEntity entity = new UserEntity();
        when(userMapper.toEntity(model)).thenReturn(entity);
        doThrow(new ConstraintViolationException("duplicate", null, "username"))
                .when(entityManager).persist(entity);

        // Act + Assert
        assertThrows(UserAlreadyExistsException.class, () -> authAdaptorService.createUser(model));
    }

    // ===========================================================
    // updateUser() tests
    // ===========================================================

    @Test
    @DisplayName("updateUser updates user successfully when admin or user role is present")
    void updateUser_success() {
        // Arrange
        Authentication auth = mock(Authentication.class);
        when(auth.getRoles()).thenReturn(List.of("ADMIN"));

        UserDetailsModel model = new UserDetailsModel();
        model.setUsername("ETHAN");
        model.setPassword("password123");

        UserEntity existing = new UserEntity();
        existing.setUsername("ETHAN");

        when(entityManager.find(UserEntity.class, "ETHAN")).thenReturn(existing);
        when(userMapper.toEntity(model)).thenReturn(existing);

        // Act
        authAdaptorService.updateUser(model, auth);

        // Assert
        verify(entityManager).merge(existing);
    }

    @Test
    @DisplayName("updateUser throws when authentication is null")
    void updateUser_authNull() {
        // Arrange
        UserDetailsModel model = new UserDetailsModel();
        model.setUsername("ETHAN");
        model.setPassword("password123");

        // Act + Assert
        assertThrows(IllegalStateException.class, () -> authAdaptorService.updateUser(model, null));
    }

    @Test
    @DisplayName("updateUser throws PermissionDeniedException when roles are invalid")
    void updateUser_invalidRoles() {
        // Arrange
        Authentication auth = mock(Authentication.class);
        when(auth.getRoles()).thenReturn(List.of("GHOST"));

        UserDetailsModel model = new UserDetailsModel();
        model.setUsername("ETHAN");
        model.setPassword("password123");

        // Act + Assert
        assertThrows(PermissionDeniedException.class, () -> authAdaptorService.updateUser(model, auth));
    }

    @Test
    @DisplayName("updateUser throws UserNotFoundException when entity not found")
    void updateUser_entityNotFound() {
        // Arrange
        Authentication auth = mock(Authentication.class);
        when(auth.getRoles()).thenReturn(List.of("USER"));

        UserDetailsModel model = new UserDetailsModel();
        model.setUsername("ETHAN");
        model.setPassword("password123");

        when(entityManager.find(UserEntity.class, "ETHAN")).thenReturn(null);

        // Act + Assert
        assertThrows(UserNotFoundException.class, () -> authAdaptorService.updateUser(model, auth));
    }

    @Test
    @DisplayName("updateUser throws when user fails validation")
    void updateUser_validationFails() {
        // Arrange
        Authentication auth = mock(Authentication.class);
        when(auth.getRoles()).thenReturn(List.of("USER"));

        UserDetailsModel model = new UserDetailsModel();
        model.setUsername("ETHAN");
        model.setPassword("short");

        UserEntity existing = new UserEntity();
        when(entityManager.find(UserEntity.class, "ETHAN")).thenReturn(existing);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authAdaptorService.updateUser(model, auth));
    }

    // ===========================================================
    // deleteUser() tests
    // ===========================================================

    @Test
    @DisplayName("deleteUser removes entity successfully when admin")
    void deleteUser_success() {
        // Arrange
        Authentication auth = mock(Authentication.class);
        when(auth.getRoles()).thenReturn(List.of("ADMIN"));

        UserEntity entity = new UserEntity();
        entity.setUsername("ETHAN");

        when(entityManager.find(UserEntity.class, "ETHAN")).thenReturn(entity);

        // Act
        authAdaptorService.deleteUser("ETHAN", auth);

        // Assert
        verify(entityManager, times(2)).find(UserEntity.class, "ETHAN");
        verify(entityManager).remove(entity);
    }

    @Test
    @DisplayName("deleteUser throws when authentication is null")
    void deleteUser_authNull() {
        // Act + Assert
        assertThrows(IllegalStateException.class, () -> authAdaptorService.deleteUser("ETHAN", null));
    }

    @Test
    @DisplayName("deleteUser throws PermissionDeniedException when user is not admin")
    void deleteUser_notAdmin() {
        // Arrange
        Authentication auth = mock(Authentication.class);
        when(auth.getRoles()).thenReturn(List.of("USER"));

        // Act + Assert
        assertThrows(PermissionDeniedException.class, () -> authAdaptorService.deleteUser("ETHAN", auth));
    }

    @Test
    @DisplayName("deleteUser throws UserNotFoundException when user does not exist")
    void deleteUser_userNotFound() {
        // Arrange
        Authentication auth = mock(Authentication.class);
        when(auth.getRoles()).thenReturn(List.of("ADMIN"));

        when(entityManager.find(UserEntity.class, "ETHAN")).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> authAdaptorService.deleteUser("ETHAN", auth));
    }

//    //needs more testing to achieve line coverage greater than 80%
//
//    private AuthAdaptorService authAdaptorService;
//    private EntityManager entityManager;
//    private UserMapper userMapper;
//    private TypedQuery<UserEntity> typedQuery;
//
//    @BeforeEach
//    void setUp() {
//        entityManager = mock(EntityManager.class);
//        userMapper = mock(UserMapper.class);
//        typedQuery = mock(TypedQuery.class);
//
//        authAdaptorService = spy(new AuthAdaptorService());
//        authAdaptorService.entityManager = entityManager;
//        authAdaptorService.userMapper = userMapper;
//    }
//
//    // ===========================================================
//    @Test
//    @DisplayName("findUser returns mapped user when found")
//    void findUser_Success() {
//        // Arrange
//        UserEntity userEntity = new UserEntity();
//        userEntity.setUsername("ETHAN");
//        userEntity.setPassword("hashedPass");
//
//        UserDetailsModel expectedModel = new UserDetailsModel();
//        expectedModel.setUsername("ETHAN");
//        expectedModel.setPassword("hashedPass");
//
//        when(entityManager.createQuery(anyString(), eq(UserEntity.class))).thenReturn(typedQuery);
//        when(typedQuery.setParameter(eq("username"), eq("ETHAN"))).thenReturn(typedQuery);
//        when(typedQuery.getSingleResult()).thenReturn(userEntity);
//        when(userMapper.toModel(userEntity)).thenReturn(expectedModel);
//
//        // Act
//        UserDetailsModel result = authAdaptorService.findUser("ETHAN");
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("ETHAN", result.getUsername());
//        verify(entityManager).createQuery(anyString(), eq(UserEntity.class));
//        verify(userMapper).toModel(userEntity);
//    }
//
//    // ===========================================================
//    @Test
//    @DisplayName("findUser returns null when no suer exists")
//    void find_User_Exception_Returns_null_when_No_User_Exists() {
//
//        assertThrows(NullPointerException.class,() -> authAdaptorService.findUser("ETHAN"));
//    }
//
//    // ===========================================================
//    @Test
//    @DisplayName("createUser persists new user entity")
//    void createUser_Success() {
//        // Arrange
//        UserDetailsModel model = new UserDetailsModel();
//        model.setUsername("ETHAN");
//        model.setPassword("password");
//
//        UserEntity entity = new UserEntity();
//        entity.setUsername("ETHAN");
//        entity.setPassword("password");
//
//        when(userMapper.toEntity(model)).thenReturn(entity);
//
//        // Act
//        authAdaptorService.createUser(model);
//
//        // Assert
//        verify(userMapper).toEntity(model);
//        verify(entityManager).persist(entity);
//    }
//
//    // ===========================================================
//    @Test
//    @DisplayName("createUser throws IllegalArgumentException on failure")
//    void createUser_Fails_ThrowsException() {
//        // Arrange
//        UserDetailsModel model = new UserDetailsModel();
//        model.setUsername("ETHAN");
//
//        UserEntity entity = new UserEntity();
//        entity.setUsername("ETHAN");
//
//        when(userMapper.toEntity(model)).thenReturn(entity);
//        doThrow(new RuntimeException("DB fail")).when(entityManager).persist(any());
//
//        // Act & Assert
//        assertThrows(IllegalArgumentException.class, () -> authAdaptorService.createUser(model));
//    }
//
//    // ===========================================================
//    @Test
//    @DisplayName("updateUser merges user entity correctly")
//    void updateUser_Success() {
//        Authentication authentication = mock(Authentication.class);
//
//        // Arrange
//        UserDetailsModel model = new UserDetailsModel();
//        model.setUsername("ETHAN");
//        model.setPassword("password");
//        model.setRole("ADMIN");
//        model.setEmail("testemail@gmail.com");
//
//        UserEntity entity = new UserEntity();
//        entity.setUsername("ETHAN");
//
//        when(authentication.getName()).thenReturn("ETHAN");
//        when(authentication.getRoles()).thenReturn(List.of("ADMIN"));
//
//
//        when(userMapper.toEntity(model)).thenReturn(entity);
//        when(entityManager.find(UserEntity.class, "ETHAN")).thenReturn(entity);
//
//
//        // Act
//        authAdaptorService.updateUser(model,authentication);
//
//        // Assert
//        verify(userMapper).toEntity(model);
//        verify(entityManager).merge(entity);
//    }
//
//    // ===========================================================
//    @Test
//    @DisplayName("deleteUser removes user entity successfully")
//    void deleteUser_Success() {
//        // Arrange
//        Authentication authentication = mock(Authentication.class);
//        when(authentication.getName()).thenReturn("ETHAN");
//        when(authentication.getRoles()).thenReturn(List.of("ADMIN"));
//
//        UserEntity model = new UserEntity();
//        model.setUsername("ETHAN");
//        model.setPassword("password");
//        model.setRole("ADMIN");
//
//        when(entityManager.find(UserEntity.class, "ETHAN")).thenReturn(model);
//
//        // Act
//        authAdaptorService.deleteUser("ETHAN", authentication);
//
//        // Assert
//        verify(entityManager).remove(model);
//    }
}