package com.todolist.Services;

import com.todolist.Models.PlayerStatsModel;
import com.todolist.Models.TaskObjectModel;
import com.todolist.Models.UserDetailsModel;
import com.todolist.adaptors.persistence.Jpa.PlayerStatsEntity;
import com.todolist.adaptors.web.AdaptorServicePlayerStats;
import com.todolist.adaptors.web.PlayerStatsMapper;
import com.todolist.exceptions.PlayerUsernameNotProvided;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@MicronautTest
@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private AdaptorServicePlayerStats adaptorServicePlayerStats;

    @Mock
    private PlayerStatsMapper playerStatsMapper;

    @InjectMocks
    private GameService gameService;

    //Create player stats profile testing

    @Test
    @DisplayName("Throws error when user details is null")
    void register_throws_When_User_Details_Null() {
        //Arrange
        UserDetailsModel userDetailsModel = null;

        //Act + Assert
        Assertions.assertThrows(IllegalArgumentException.class,() -> {
            gameService.createPlayerStatsProfile(userDetailsModel);
        });
    }

    @Test
    @DisplayName("Throws error when username is null")
    void register_throws_When_Username_Null() {
        //Arrange
        UserDetailsModel userDetailsModel = new UserDetailsModel();
        userDetailsModel.setUsername(null);
        userDetailsModel.setPassword("password");
        userDetailsModel.setRole("ROLE_USER");
        userDetailsModel.setEmail("email");

        //Act + Assert
        Assertions.assertThrows(PlayerUsernameNotProvided.class,() -> {
            gameService.createPlayerStatsProfile(userDetailsModel);
        });
    }

    @Test
    @DisplayName("Throws error when username is blank")
    void register_throws_When_Username_Blank() {
        //Arrange
        UserDetailsModel userDetailsModel = new UserDetailsModel();
        userDetailsModel.setUsername("");
        userDetailsModel.setPassword("password");
        userDetailsModel.setRole("ROLE_USER");
        userDetailsModel.setEmail("email");

        //Act + Assert
        Assertions.assertThrows(PlayerUsernameNotProvided.class,() -> {
            gameService.createPlayerStatsProfile(userDetailsModel);
        });
    }

    @Test
    @DisplayName("Creates player stats profile when input is valid")
    void register_creates_Player_Stats_Profile_When_Input_Valid() {
        //Arrange
        UserDetailsModel userDetailsModel = new UserDetailsModel();
        userDetailsModel.setUsername("TestUser");
        userDetailsModel.setPassword("password");
        userDetailsModel.setRole("ROLE_USER");
        userDetailsModel.setEmail("email");

        ArgumentCaptor<PlayerStatsModel> playerStatsModelArgumentCaptor = ArgumentCaptor.forClass(PlayerStatsModel.class);

        //Act
        gameService.createPlayerStatsProfile(userDetailsModel);

        //Assert
        Mockito.verify(adaptorServicePlayerStats).createPlayerStats(playerStatsModelArgumentCaptor.capture());
        verifyNoMoreInteractions(adaptorServicePlayerStats);
        Assertions.assertEquals("TestUser",playerStatsModelArgumentCaptor.getValue().getPlayerUsername());
        Assertions.assertEquals(1,playerStatsModelArgumentCaptor.getValue().getPlayerLevel());
        Assertions.assertEquals(0,playerStatsModelArgumentCaptor.getValue().getPlayerXp());
        Assertions.assertEquals(20,playerStatsModelArgumentCaptor.getValue().getXpToNextLevel());
    }

    //Calculate XP Reward for task completion testing
    @Test
    @DisplayName("Throws error when task is null")
    void calculateXP_throws_When_Task_Null() {
        // Arrange
        TaskObjectModel taskObjectModel = null;

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("TestUser");
        when(authentication.getRoles()).thenReturn(List.of("ROLE_USER"));

        PlayerStatsEntity fakeEntity = new PlayerStatsEntity();
        fakeEntity.setPlayerLevel(5);
        fakeEntity.setPlayerXp(100);

        when(adaptorServicePlayerStats.retrievePlayerStats(authentication))
                .thenReturn(fakeEntity);

        PlayerStatsModel fakeModel = new PlayerStatsModel();
        fakeModel.setPlayerUsername("TestUser");
        fakeModel.setPlayerLevel(5);
        fakeModel.setPlayerXp(100);
        fakeModel.setXpToNextLevel(200);

        when(playerStatsMapper.toModel(fakeEntity))
                .thenReturn(fakeModel);

        // Act + Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            gameService.calculateXP(taskObjectModel, authentication);
        });

        // Ensure adaptor not called again during XP logic
        verify(adaptorServicePlayerStats, times(1))
                .retrievePlayerStats(authentication);  // called once by validation

        verifyNoMoreInteractions(adaptorServicePlayerStats);
    }

    @Test
    @DisplayName("Throws error when task level is null")
    void calculateXP_throws_When_Task_Level_Null() {
        // Arrange
        TaskObjectModel taskObjectModel = new TaskObjectModel();
        taskObjectModel.setTaskLevel(null);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("TestUser");
        when(authentication.getRoles()).thenReturn(List.of("ROLE_USER"));

        PlayerStatsEntity fakeEntity = new PlayerStatsEntity();
        fakeEntity.setPlayerLevel(5);
        fakeEntity.setPlayerXp(100);

        when(adaptorServicePlayerStats.retrievePlayerStats(authentication))
                .thenReturn(fakeEntity);

        PlayerStatsModel fakeModel = new PlayerStatsModel();
        fakeModel.setPlayerUsername("TestUser");
        fakeModel.setPlayerLevel(5);
        fakeModel.setPlayerXp(100);
        fakeModel.setXpToNextLevel(200);

        when(playerStatsMapper.toModel(fakeEntity))
                .thenReturn(fakeModel);

        // Act + Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            gameService.calculateXP(taskObjectModel, authentication);
        });

        // Ensure adaptor not called again during XP logic
        verify(adaptorServicePlayerStats, times(1))
                .retrievePlayerStats(authentication);  // called once by validation

        verifyNoMoreInteractions(adaptorServicePlayerStats);
    }

    @Test
    @DisplayName("Throws error when task level is not a number")
    void calculateXP_throws_When_Task_Level_Not_A_Number() {
        // Arrange
        TaskObjectModel taskObjectModel = new TaskObjectModel();
        taskObjectModel.setTaskLevel("!!==ioujghoufu");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("TestUser");
        when(authentication.getRoles()).thenReturn(List.of("ROLE_USER"));

        PlayerStatsEntity fakeEntity = new PlayerStatsEntity();
        fakeEntity.setPlayerLevel(5);
        fakeEntity.setPlayerXp(100);

        when(adaptorServicePlayerStats.retrievePlayerStats(authentication))
                .thenReturn(fakeEntity);

        PlayerStatsModel fakeModel = new PlayerStatsModel();
        fakeModel.setPlayerUsername("TestUser");
        fakeModel.setPlayerLevel(5);
        fakeModel.setPlayerXp(100);
        fakeModel.setXpToNextLevel(200);

        when(playerStatsMapper.toModel(fakeEntity))
                .thenReturn(fakeModel);

        // Act + Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            gameService.calculateXP(taskObjectModel, authentication);
        });

        // Ensure adaptor not called again during XP logic
        verify(adaptorServicePlayerStats, times(1))
                .retrievePlayerStats(authentication);  // called once by validation

        verifyNoMoreInteractions(adaptorServicePlayerStats);
    }

    @Test
    @DisplayName("Returns correct XP for valid task level")
    void calculateXP_returns_Correct_XP_For_Valid_Task_Level() {
        // Arrange
        TaskObjectModel task = new TaskObjectModel();
        task.setTaskLevel("5"); // baseXp = 100

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("TestUser");
        when(authentication.getRoles()).thenReturn(List.of("ROLE_USER"));

        // Mock player stats entity
        PlayerStatsEntity fakeEntity = new PlayerStatsEntity();
        fakeEntity.setPlayerLevel(2); // scaling = 0.96

        when(adaptorServicePlayerStats.retrievePlayerStats(authentication))
                .thenReturn(fakeEntity);

        // Mock mapper returning valid model (validation pass)
        PlayerStatsModel fakeModel = new PlayerStatsModel();
        fakeModel.setPlayerUsername("TestUser");
        fakeModel.setPlayerLevel(2);
        fakeModel.setPlayerXp(0);
        fakeModel.setXpToNextLevel(20);

        when(playerStatsMapper.toModel(fakeEntity))
                .thenReturn(fakeModel);

        // Act
        int result = gameService.calculateXP(task, authentication);

        // Assert
        assertEquals(96, result); // 100 * 0.96 = 96
    }

    @Test
    @DisplayName("Uses minimum scaling of 0.4 for high player levels")
    void calculateXP_uses_Minimum_Scaling_For_High_Player_Levels() {
        // Arrange
        TaskObjectModel task = new TaskObjectModel();
        task.setTaskLevel("5"); // baseXp = 100

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("TestUser");
        when(authentication.getRoles()).thenReturn(List.of("ROLE_USER"));

        // Mock player stats entity
        PlayerStatsEntity fakeEntity = new PlayerStatsEntity();
        fakeEntity.setPlayerLevel(50); // scaling would be negative => floor at 0.4

        when(adaptorServicePlayerStats.retrievePlayerStats(authentication))
                .thenReturn(fakeEntity);

        // Mock mapper returning valid model
        PlayerStatsModel fakeModel = new PlayerStatsModel();
        fakeModel.setPlayerUsername("TestUser");
        fakeModel.setPlayerLevel(50);
        fakeModel.setPlayerXp(0);
        fakeModel.setXpToNextLevel(20);

        when(playerStatsMapper.toModel(fakeEntity))
                .thenReturn(fakeModel);

        // Act
        int result = gameService.calculateXP(task, authentication);

        // Assert
        assertEquals(40, result); // 100 * 0.4 = 40
    }

    //Add Xp for task completion testing

    @Test
    void createPlayerStatsProfile() {
    }

    @Test
    void calculateXP() {
    }

    @Test
    void addXPForTaskCompletion() {
    }

    @Test
    void calculateXPToLevelUp() {
    }

    @Test
    void getPlayerStats() {
    }

    @Test
    void validatePlayerAuthentication() {
    }
}