package com.todolist.Services;

import com.todolist.Models.PlayerStatsModel;
import com.todolist.Models.TaskObjectModel;
import com.todolist.Models.UserDetailsModel;
import com.todolist.adaptors.persistence.Jpa.PlayerStatsEntity;
import com.todolist.adaptors.web.AdaptorServicePlayerStats;
import com.todolist.adaptors.web.PlayerStatsMapper;
import com.todolist.exceptions.MapperFailedException;
import com.todolist.exceptions.PermissionDeniedException;
import com.todolist.exceptions.PlayerStatsNotFound;
import com.todolist.exceptions.PlayerUsernameNotProvided;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.Authenticator;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@MicronautTest
@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    private final Authenticator authenticator;
    @Mock
    private AdaptorServicePlayerStats adaptorServicePlayerStats;

    @Mock
    private PlayerStatsMapper playerStatsMapper;

    @InjectMocks
    private GameService gameService;

    GameServiceTest(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

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
    @DisplayName("Throws exception when TaskObjectModel is null")
    void addXPForTaskCompletion_throws_When_TaskObjectModel_Null() {
        //Arrange
        TaskObjectModel taskObjectModel = null;
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("TestUser");
        when(authentication.getRoles()).thenReturn(List.of("ROLE_USER"));
        PlayerStatsEntity fakeEntity = new PlayerStatsEntity();
        PlayerStatsModel fakePlayerStats = new PlayerStatsModel();
        fakePlayerStats.setPlayerUsername("TestUser");
        when(adaptorServicePlayerStats.retrievePlayerStats(authentication)).thenReturn(fakeEntity);
        when(playerStatsMapper.toModel(fakeEntity)).thenReturn(fakePlayerStats);


        //Act + Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            gameService.addXPForTaskCompletion(taskObjectModel, authentication);
        });
    }

    @Test
    @DisplayName("Throws exception when TaskObjectModel level is null")
    void addXPForTaskCompletion_throws_When_TaskObjectModel_Level_Null() {
        //Arrange
        TaskObjectModel taskObjectModel = new TaskObjectModel();
        taskObjectModel.setTaskLevel(null);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("TestUser");
        when(authentication.getRoles()).thenReturn(List.of("ROLE_USER"));
        PlayerStatsEntity fakeEntity = new PlayerStatsEntity();
        PlayerStatsModel fakePlayerStats = new PlayerStatsModel();
        fakePlayerStats.setPlayerUsername("TestUser");
        when(adaptorServicePlayerStats.retrievePlayerStats(authentication)).thenReturn(fakeEntity);
        when(playerStatsMapper.toModel(fakeEntity)).thenReturn(fakePlayerStats);


        //Act + Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            gameService.addXPForTaskCompletion(taskObjectModel, authentication);
        });
    }

    @Test
    @DisplayName("Player Xp added with no level up and updated properly")
    void addXPForTaskCompletion_Player_Xp_Added_With_No_Level_Up_And_Updated_Properly() {
        //Arrange
        TaskObjectModel taskObjectModel = new TaskObjectModel();
        taskObjectModel.setTaskLevel("5"); // baseXp = 100
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("TestUser");
        when(authentication.getRoles()).thenReturn(List.of("ROLE_USER"));
        PlayerStatsEntity fakeEntity = new PlayerStatsEntity();
        fakeEntity.setXpToNextLevel(1000);
        fakeEntity.setPlayerLevel(5);
        fakeEntity.setPlayerXp(0);
        when(adaptorServicePlayerStats.retrievePlayerStats(authentication)).thenReturn(fakeEntity);
        PlayerStatsModel fakePlayerStats = new PlayerStatsModel();
        fakePlayerStats.setPlayerUsername("TestUser");
        fakePlayerStats.setPlayerLevel(5);
        fakePlayerStats.setPlayerXp(0);
        fakePlayerStats.setXpToNextLevel(1000);
        when(playerStatsMapper.toModel(fakeEntity)).thenReturn(fakePlayerStats);
        ArgumentCaptor<PlayerStatsModel> playerStatsModelArgumentCaptor = ArgumentCaptor.forClass(PlayerStatsModel.class);

        //Act
        gameService.addXPForTaskCompletion(taskObjectModel, authentication);

        //Assert
        verify(adaptorServicePlayerStats).updatePlayerStats(playerStatsModelArgumentCaptor.capture(),eq(authentication));
        verifyNoMoreInteractions(adaptorServicePlayerStats);
        Assertions.assertEquals(90,playerStatsModelArgumentCaptor.getValue().getPlayerXp());
        Assertions.assertEquals(5,playerStatsModelArgumentCaptor.getValue().getPlayerLevel());
    }

    @Test
    @DisplayName("Tests Player gains XP and levels up once")
    void addXPForTaskCompletion_Player_Gains_XP_And_Levels_Up_ONCE() {
        //Arrange
        TaskObjectModel taskObjectModel = new TaskObjectModel();
        taskObjectModel.setTaskLevel("5"); // baseXp = 100
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("TestUser");
        when(authentication.getRoles()).thenReturn(List.of("ROLE_USER"));
        PlayerStatsEntity fakeEntity = new PlayerStatsEntity();
        fakeEntity.setXpToNextLevel(80);
        fakeEntity.setPlayerLevel(5);
        fakeEntity.setPlayerXp(0);
        when(adaptorServicePlayerStats.retrievePlayerStats(authentication)).thenReturn(fakeEntity);
        PlayerStatsModel fakePlayerStats = new PlayerStatsModel();
        fakePlayerStats.setPlayerUsername("TestUser");
        fakePlayerStats.setPlayerLevel(5);
        fakePlayerStats.setPlayerXp(0);
        fakePlayerStats.setXpToNextLevel(80);
        when(playerStatsMapper.toModel(fakeEntity)).thenReturn(fakePlayerStats);
        ArgumentCaptor<PlayerStatsModel> playerStatsModelArgumentCaptor = ArgumentCaptor.forClass(PlayerStatsModel.class);

        //Act
        gameService.addXPForTaskCompletion(taskObjectModel, authentication);

        //Assert
        verify(adaptorServicePlayerStats).updatePlayerStats(playerStatsModelArgumentCaptor.capture(),eq(authentication));
        verifyNoMoreInteractions(adaptorServicePlayerStats);
        Assertions.assertEquals(10,playerStatsModelArgumentCaptor.getValue().getPlayerXp());
        Assertions.assertEquals(6,playerStatsModelArgumentCaptor.getValue().getPlayerLevel());
    }

    @Test
    @DisplayName("Tests Player levels up multiple times")
    void addXPForTaskCompletion_Player_Levels_Up_Multiple_Times() {
        //Arrange
        TaskObjectModel taskObjectModel = new TaskObjectModel();
        taskObjectModel.setTaskLevel("5"); // baseXp = 100
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("TestUser");
        when(authentication.getRoles()).thenReturn(List.of("ROLE_USER"));
        PlayerStatsEntity fakeEntity = new PlayerStatsEntity();
        fakeEntity.setXpToNextLevel(20);
        fakeEntity.setPlayerLevel(1);
        fakeEntity.setPlayerXp(0);
        when(adaptorServicePlayerStats.retrievePlayerStats(authentication)).thenReturn(fakeEntity);
        PlayerStatsModel fakePlayerStats = new PlayerStatsModel();
        fakePlayerStats.setPlayerUsername("TestUser");
        fakePlayerStats.setPlayerLevel(1);
        fakePlayerStats.setPlayerXp(0);
        fakePlayerStats.setXpToNextLevel(20);
        when(playerStatsMapper.toModel(fakeEntity)).thenReturn(fakePlayerStats);
        ArgumentCaptor<PlayerStatsModel> playerStatsModelArgumentCaptor = ArgumentCaptor.forClass(PlayerStatsModel.class);

        //Act
        gameService.addXPForTaskCompletion(taskObjectModel, authentication);

        //Assert
        verify(adaptorServicePlayerStats).updatePlayerStats(playerStatsModelArgumentCaptor.capture(),eq(authentication));
        verifyNoMoreInteractions(adaptorServicePlayerStats);
        Assertions.assertEquals(38,playerStatsModelArgumentCaptor.getValue().getPlayerXp());
        Assertions.assertEquals(3,playerStatsModelArgumentCaptor.getValue().getPlayerLevel());
    }

    @Test
    @DisplayName("Levels up correctly when xp earned matches xp required to level up")
    void addXPForTaskCompletion_Levels_Up_Correctly_When_Xp_Earned_Matches_Xp_Required_To_Level_Up() {
        //Arrange
        TaskObjectModel taskObjectModel = new TaskObjectModel();
        taskObjectModel.setTaskLevel("5"); // baseXp = 100
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("TestUser");
        when(authentication.getRoles()).thenReturn(List.of("ROLE_USER"));
        PlayerStatsEntity fakeEntity = new PlayerStatsEntity();
        fakeEntity.setXpToNextLevel(98);
        fakeEntity.setPlayerLevel(1);
        fakeEntity.setPlayerXp(0);
        when(adaptorServicePlayerStats.retrievePlayerStats(authentication)).thenReturn(fakeEntity);
        PlayerStatsModel fakePlayerStats = new PlayerStatsModel();
        fakePlayerStats.setPlayerUsername("TestUser");
        fakePlayerStats.setPlayerLevel(1);
        fakePlayerStats.setPlayerXp(0);
        fakePlayerStats.setXpToNextLevel(98);
        when(playerStatsMapper.toModel(fakeEntity)).thenReturn(fakePlayerStats);
        ArgumentCaptor<PlayerStatsModel> playerStatsModelArgumentCaptor = ArgumentCaptor.forClass(PlayerStatsModel.class);

        //Act
        gameService.addXPForTaskCompletion(taskObjectModel, authentication);

        //Assert
        verify(adaptorServicePlayerStats).updatePlayerStats(playerStatsModelArgumentCaptor.capture(),eq(authentication));
        verifyNoMoreInteractions(adaptorServicePlayerStats);
        Assertions.assertEquals(0,playerStatsModelArgumentCaptor.getValue().getPlayerXp());
        Assertions.assertEquals(2,playerStatsModelArgumentCaptor.getValue().getPlayerLevel());
    }

    //Testing for Level up method as its public method using the addXPForTaskCompletion to ensure overflow xp is correct
    @Test
    @DisplayName("Tests level up correctly when there is xp overflow")
    void levelUp_CalculatesOverflowCorrectly() {
        //Arrange
        TaskObjectModel task = new TaskObjectModel();
        task.setTaskLevel("10"); // Base XP = 200

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("TestUser");
        when(auth.getRoles()).thenReturn(List.of("ROLE_USER"));

        PlayerStatsEntity entity = new PlayerStatsEntity();
        entity.setPlayerLevel(1);
        entity.setPlayerXp(0);
        entity.setXpToNextLevel(150); // Will overflow by 50
        entity.setPlayerUsername("TestUser");
        entity.setPlayerProfileId(1L);

        when(adaptorServicePlayerStats.retrievePlayerStats(auth)).thenReturn(entity);

        PlayerStatsModel model = new PlayerStatsModel();
        model.setPlayerLevel(1);
        model.setPlayerXp(0);
        model.setXpToNextLevel(150);
        model.setPlayerUsername("TestUser");
        model.setPlayerProfileId(1L);

        when(playerStatsMapper.toModel(entity)).thenReturn(model);

        ArgumentCaptor<PlayerStatsModel> captor = ArgumentCaptor.forClass(PlayerStatsModel.class);

        //Act
        gameService.addXPForTaskCompletion(task, auth);

        //Assert
        verify(adaptorServicePlayerStats).updatePlayerStats(captor.capture(), eq(auth));

        PlayerStatsModel updated = captor.getValue();

        // XP = 200 * scaling(0.98) = 196
        // Threshold = 150 → overflow = 46
        Assertions.assertEquals(6, updated.getPlayerXp());
        Assertions.assertEquals(3, updated.getPlayerLevel());
        Assertions.assertEquals(3 * 20, updated.getXpToNextLevel());
    }


    // ValidatePlayerAuthentication tests

    @Test
    @DisplayName("Throws IllegalStateException when Authentication is null")
    void validatePlayerAuthentication_throwsIllegalStateException_whenAuthenticationIsNull() {
        // Arrange
        Authentication authentication = null;

        // Act + Assert
        Assertions.assertThrows(IllegalStateException.class, () ->
                gameService.validatePlayerAuthentication(authentication, "UnitTest: Auth null")
        );
    }

    @Test
    @DisplayName("Throws IllegalStateException when Authentication username is null")
    void validatePlayerAuthentication_throwsIllegalStateException_whenUsernameIsNull() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(null);
        // getRoles() is never reached, so we don't need to stub it

        // Act + Assert
        Assertions.assertThrows(IllegalStateException.class, () ->
                gameService.validatePlayerAuthentication(authentication, "UnitTest: Username null")
        );
    }

    @Test
    @DisplayName("Throws PermissionDeniedException when roles list is empty")
    void validatePlayerAuthentication_throwsPermissionDenied_whenRolesEmpty() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("TestUser");
        when(authentication.getRoles()).thenReturn(Collections.emptyList());

        // Act + Assert
        Assertions.assertThrows(PermissionDeniedException.class, () ->
                gameService.validatePlayerAuthentication(authentication, "UnitTest: Roles empty")
        );
    }

    @Test
    @DisplayName("Throws PlayerStatsNotFound when mapped PlayerStatsModel is null")
    void validatePlayerAuthentication_throwsPlayerStatsNotFound_whenPlayerStatsModelNull() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("TestUser");
        when(authentication.getRoles()).thenReturn(List.of("ROLE_USER"));

        PlayerStatsEntity fakeEntity = new PlayerStatsEntity();
        when(adaptorServicePlayerStats.retrievePlayerStats(authentication)).thenReturn(fakeEntity);
        when(playerStatsMapper.toModel(fakeEntity)).thenReturn(null);

        // Act + Assert
        Assertions.assertThrows(PlayerStatsNotFound.class, () ->
                gameService.validatePlayerAuthentication(authentication, "UnitTest: PlayerStatsModel null")
        );
    }

    @Test
    @DisplayName("Throws MapperFailedException when mapped PlayerStatsModel username is null or empty")
    void validatePlayerAuthentication_throwsMapperFailed_whenUsernameNullOrEmpty() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("TestUser");
        when(authentication.getRoles()).thenReturn(List.of("ROLE_USER"));

        PlayerStatsEntity fakeEntity = new PlayerStatsEntity();
        when(adaptorServicePlayerStats.retrievePlayerStats(authentication)).thenReturn(fakeEntity);

        PlayerStatsModel fakePlayerStatsModel = new PlayerStatsModel();
        fakePlayerStatsModel.setPlayerUsername(null); // or "" if you want the empty case
        when(playerStatsMapper.toModel(fakeEntity)).thenReturn(fakePlayerStatsModel);

        // Act + Assert
        Assertions.assertThrows(MapperFailedException.class, () ->
                gameService.validatePlayerAuthentication(authentication, "UnitTest: Username null/empty")
        );
    }
}