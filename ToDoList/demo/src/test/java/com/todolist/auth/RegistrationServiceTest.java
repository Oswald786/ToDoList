package com.todolist.auth;

import com.todolist.Models.UserDetailsModel;
import com.todolist.adaptors.persistence.Jpa.UserEntity;
import com.todolist.adaptors.web.UserMapper;
import com.todolist.adaptors.web.UserMapperImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationServiceTest {

    private RegistrationService registrationService;
    private PasswordHasher passwordHasher;
    private AuthAdaptorService fakeauthAdaptorService;

    private final UserMapper userMapper = new UserMapperImpl();

    @BeforeEach
    void setUp() {
        passwordHasher = mock(PasswordHasher.class);
        fakeauthAdaptorService = mock(AuthAdaptorService.class);
        registrationService = spy(new RegistrationService());
        registrationService.setPasswordHasher(passwordHasher);
        registrationService.setAuthAdaptorService(fakeauthAdaptorService);
        registrationService.setEntityManager(mock(EntityManager.class));
    }

    @Test
    @DisplayName("User unable to register because there password has not been hashed")
    void registerFailsWithUnhashedPassword() {
        // Arrange
        UserDetailsModel userDetailsModel = new UserDetailsModel();
        userDetailsModel.setUserName("ETHAN");
        userDetailsModel.setPassword("unhashed");
        when(passwordHasher.hashPassword(eq("unhashed"))).thenReturn("unhashed");

        //act
        assertThrows(IllegalArgumentException.class,() -> {
            registrationService.register(userDetailsModel);
        });

        //assert
    }

    @Test
    @DisplayName("User unable to register because the user already exists")
    void registerFailsWithExistingUser() {
        // Arrange
        UserDetailsModel userDetailsModel = new UserDetailsModel();
        userDetailsModel.setUserName("ETHAN");
        userDetailsModel.setPassword("hashed");
        userDetailsModel.setRole("ROLE_USER");
        UserEntity userEntity = userMapper.toEntity(userDetailsModel);
        when(passwordHasher.hashPassword(eq("unhashed"))).thenCallRealMethod();
        when(registrationService.entityManager.find(eq(UserEntity.class), eq(userDetailsModel.getUserName()))).thenReturn(userEntity);

        //act
        assertThrows(IllegalArgumentException.class,() -> {
            registrationService.register(userDetailsModel);
        });

        //assert
    }

    @Test
    @DisplayName("User unable to register because of unknown error")
    void registerFailsWithUnknownError() {
        // Arrange
        UserDetailsModel userDetailsModel = new UserDetailsModel();
        userDetailsModel.setUserName("ETHAN");
        userDetailsModel.setPassword("hashed");
        userDetailsModel.setRole("ROLE_USER");
        UserEntity userEntity = userMapper.toEntity(userDetailsModel);
        when(passwordHasher.hashPassword(eq("unhashed"))).thenCallRealMethod();
        when(registrationService.entityManager.find(eq(UserEntity.class), eq(userDetailsModel.getUserName()))).thenReturn(null);

        //act
        assertThrows(IllegalArgumentException.class,() -> {
            registrationService.register(userDetailsModel);
        });
    }

    @Test
    @DisplayName("User able to register")
    void registerSucceeds() {
        // Arrange
        UserDetailsModel userDetailsModel = new UserDetailsModel();
        userDetailsModel.setUserName("ETHAN");
        userDetailsModel.setPassword("unHashedPassword");
        userDetailsModel.setRole("ROLE_USER");
        UserEntity userEntity = userMapper.toEntity(userDetailsModel);
        when(passwordHasher.hashPassword(anyString())).thenReturn("hashedPassword");
        when(registrationService.entityManager.find(eq(UserEntity.class), eq(userDetailsModel.getUserName()))).thenReturn(null);

        //act
        registrationService.register(userDetailsModel);

        // Assert
        verify(passwordHasher).hashPassword("unHashedPassword");
        verify(registrationService.getAuthAdaptorService()).createUser(any(UserDetailsModel.class));


    }
}