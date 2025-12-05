package com.todolist.adaptors.web;

import com.todolist.Models.PlayerStatsModel;
import com.todolist.adaptors.persistence.Jpa.PlayerStatsEntity;
import com.todolist.exceptions.PermissionDeniedException;
import com.todolist.exceptions.PlayerStatsNotFound;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MicronautTest
@ExtendWith(MockitoExtension.class)
class AdaptorServicePlayerStatsTest {

    @Mock
    private PlayerStatsMapper playerStatsMapper;

    @Mock
    private EntityManager entityManager;


    @InjectMocks
    private AdaptorServicePlayerStats adaptorServicePlayerStats;

    //create player stats testing
    @Test
    @DisplayName("Throws when player stats model is null")
    void createPlayerStats_throws_whenModelNull() {
        assertThrows(IllegalArgumentException.class,
                () -> adaptorServicePlayerStats.createPlayerStats(null)
        );
    }

    @Test
    @DisplayName("Throws when username is null")
    void createPlayerStats_throws_whenUsernameNull() {
        PlayerStatsModel model = new PlayerStatsModel();
        model.setPlayerUsername(null);
        model.setPlayerLevel(1);
        model.setPlayerXp(0);
        model.setXpToNextLevel(10);

        assertThrows(IllegalArgumentException.class,
                () -> adaptorServicePlayerStats.createPlayerStats(model)
        );
    }

    @Test
    @DisplayName("Throws when username is blank")
    void createPlayerStats_throws_whenUsernameBlank() {
        PlayerStatsModel model = new PlayerStatsModel();
        model.setPlayerUsername("   ");
        model.setPlayerLevel(1);
        model.setPlayerXp(0);
        model.setXpToNextLevel(10);

        assertThrows(IllegalArgumentException.class,
                () -> adaptorServicePlayerStats.createPlayerStats(model)
        );
    }

    @Test
    @DisplayName("Throws when player level is less than 1")
    void createPlayerStats_throws_whenLevelInvalid() {
        PlayerStatsModel model = new PlayerStatsModel();
        model.setPlayerUsername("sherlock");
        model.setPlayerLevel(0);
        model.setPlayerXp(0);
        model.setXpToNextLevel(10);

        assertThrows(IllegalArgumentException.class,
                () -> adaptorServicePlayerStats.createPlayerStats(model)
        );
    }

    @Test
    @DisplayName("Throws when XP is negative")
    void createPlayerStats_throws_whenXpNegative() {
        PlayerStatsModel model = new PlayerStatsModel();
        model.setPlayerUsername("sherlock");
        model.setPlayerLevel(1);
        model.setPlayerXp(-1);
        model.setXpToNextLevel(10);

        assertThrows(IllegalArgumentException.class,
                () -> adaptorServicePlayerStats.createPlayerStats(model)
        );
    }

    @Test
    @DisplayName("Throws when XP to next level is zero or negative")
    void createPlayerStats_throws_whenXpToNextLevelInvalid() {
        PlayerStatsModel model = new PlayerStatsModel();
        model.setPlayerUsername("sherlock");
        model.setPlayerLevel(1);
        model.setPlayerXp(0);
        model.setXpToNextLevel(0);

        assertThrows(IllegalArgumentException.class,
                () -> adaptorServicePlayerStats.createPlayerStats(model)
        );
    }

    @Test
    @DisplayName("Create player stats entity when player stats model is valid")
    void create_Player_Stats_Entity_when_Model_Valid() {
        //Arrange
        PlayerStatsModel model = new PlayerStatsModel();
        model.setPlayerUsername("sherlock");
        model.setPlayerLevel(1);
        model.setPlayerXp(0);
        model.setXpToNextLevel(10);
        model.setPlayerProfileId(1L);

        PlayerStatsEntity entityToBeCreated = new PlayerStatsEntity();
        entityToBeCreated.setPlayerProfileId(1L);
        entityToBeCreated.setPlayerUsername("sherlock");
        entityToBeCreated.setPlayerLevel(1);
        entityToBeCreated.setPlayerXp(0);
        entityToBeCreated.setXpToNextLevel(10);
        when(adaptorServicePlayerStats.playerStatsMapper.toEntity(model)).thenReturn(entityToBeCreated);

        //Act
        adaptorServicePlayerStats.createPlayerStats(model);

        //Assert
        verify(entityManager).persist(entityToBeCreated);


    }

    //retrieve player stats testing

    @Test
    @DisplayName("retrievePlayerStats throws PlayerStatsNotFound when no stats are found")
    void retrivePlayerStats_throws_PlayerStatsNotFound_when_no_stats_found() {
        //Arrange
        Authentication fakeAuthentication = mock(Authentication.class);
        when(fakeAuthentication.getName()).thenReturn("TestUser");
        TypedQuery<PlayerStatsEntity> fakeQuery = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT p FROM PlayerStatsEntity p WHERE p.playerUsername = :username", PlayerStatsEntity.class)).thenReturn(fakeQuery);
        when(fakeQuery.getSingleResult()).thenThrow(NoResultException.class);

        //act + assert
        Assertions.assertThrows(PlayerStatsNotFound.class, () -> adaptorServicePlayerStats.retrievePlayerStats(fakeAuthentication));
    }

    @Test
    @DisplayName("retrievePlayerStats throws PlayerStatsNotFound when multiple stats are found")
    void retrievePlayerStats_throwsPlayerStatsNotFound_whenMultipleStatsFound() {
        //Arrange
        Authentication fakeAuthentication = mock(Authentication.class);
        when(fakeAuthentication.getName()).thenReturn("TestUser");
        TypedQuery<PlayerStatsEntity> fakeQuery = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT p FROM PlayerStatsEntity p WHERE p.playerUsername = :username", PlayerStatsEntity.class)).thenReturn(fakeQuery);
        when(fakeQuery.getSingleResult()).thenThrow(NonUniqueResultException.class);

        //Act + Assert
        Assertions.assertThrows(PlayerStatsNotFound.class, () -> adaptorServicePlayerStats.retrievePlayerStats(fakeAuthentication));

    }

    @Test
    @DisplayName("retrievePlayerStats returns entity when a matching record exists")
    void retrievePlayerStats_returnsEntity_whenRecordExists() {
        //Arrange
        Authentication fakeAuthentication = mock(Authentication.class);
        when(fakeAuthentication.getName()).thenReturn("TestUser");
        TypedQuery<PlayerStatsEntity> fakeQuery = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT p FROM PlayerStatsEntity p WHERE p.playerUsername = :username", PlayerStatsEntity.class)).thenReturn(fakeQuery);
        PlayerStatsEntity fakeEntity = new PlayerStatsEntity();
        fakeEntity.setPlayerUsername("TestUser");
        fakeEntity.setPlayerLevel(1);
        fakeEntity.setPlayerXp(0);
        fakeEntity.setXpToNextLevel(10);
        when(fakeQuery.getSingleResult()).thenReturn(fakeEntity);

        //Act + Assert
        assertEquals(fakeEntity, adaptorServicePlayerStats.retrievePlayerStats(fakeAuthentication));
        verify(fakeQuery).getSingleResult();
        verify(entityManager).createQuery("SELECT p FROM PlayerStatsEntity p WHERE p.playerUsername = :username", PlayerStatsEntity.class);
    }

    //Update Method testing
    @Test
    @DisplayName("updatePlayerStats updates entity when model and authentication are valid")
    void updatePlayerStats_updatesEntity_whenModelAndAuthAreValid() {
        //Arrange
        PlayerStatsModel model = new PlayerStatsModel();
        model.setPlayerUsername("TestUser");
        model.setPlayerLevel(1);
        model.setPlayerXp(0);
        model.setXpToNextLevel(10);
        Authentication fakeAuthentication = mock(Authentication.class);
        when(fakeAuthentication.getName()).thenReturn("TestUser");
        when(fakeAuthentication.getRoles()).thenReturn(List.of("USER", "ADMIN"));
        PlayerStatsEntity fakeEntity = new PlayerStatsEntity();
        fakeEntity.setPlayerUsername("TestUser");
        fakeEntity.setPlayerLevel(1);
        fakeEntity.setPlayerXp(0);
        fakeEntity.setXpToNextLevel(10);
        fakeEntity.setPlayerProfileId(1L);
        ArgumentCaptor<PlayerStatsEntity> entityCaptor = ArgumentCaptor.forClass(PlayerStatsEntity.class);
        TypedQuery<PlayerStatsEntity> fakeQuery = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT p FROM PlayerStatsEntity p WHERE p.playerUsername = :username", PlayerStatsEntity.class)).thenReturn(fakeQuery);
        when(fakeQuery.getSingleResult()).thenReturn(fakeEntity);
        verify(entityManager).merge(entityCaptor.capture());

        //Act
        adaptorServicePlayerStats.updatePlayerStats(model, fakeAuthentication);

        //Assert
        assertEquals(model.getPlayerUsername(), entityCaptor.getValue().getPlayerUsername());
        assertEquals(model.getPlayerLevel(), entityCaptor.getValue().getPlayerLevel());
        assertEquals(model.getPlayerXp(), entityCaptor.getValue().getPlayerXp());
        assertEquals(model.getXpToNextLevel(), entityCaptor.getValue().getXpToNextLevel());
        verify(entityManager, never()).persist(any());
    }

    //Delete Method testing
    @Test
    @DisplayName("deletePlayerStats does NOT remove entity when authentication validation fails")
    void deletePlayerStats_doesNotRemove_whenAuthenticationValidationFails() {
        //Arrange
        PlayerStatsModel model = new PlayerStatsModel();
        model.setPlayerUsername("TestUser");
        model.setPlayerLevel(1);
        model.setPlayerXp(0);
        model.setXpToNextLevel(10);
        Authentication fakeAuthentication = mock(Authentication.class);
        when(fakeAuthentication.getName()).thenReturn(null);
        PlayerStatsEntity fakeEntity = new PlayerStatsEntity();

        //Act
        Assertions.assertThrows(PermissionDeniedException.class, () -> adaptorServicePlayerStats.deletePlayerStats(fakeAuthentication));

        verify(entityManager, never()).remove(any());

    }

    @Test
    @DisplayName("deletePlayerStats does NOT remove entity when player stats cannot be retrieved")
    void deletePlayerStats_doesNotRemove_whenPlayerStatsNotFound() {
        //Arrange
        PlayerStatsModel model = new PlayerStatsModel();
        model.setPlayerUsername("TestUser");
        model.setPlayerLevel(1);
        model.setPlayerXp(0);
        model.setXpToNextLevel(10);
        Authentication fakeAuthentication = mock(Authentication.class);
        when(fakeAuthentication.getName()).thenReturn("TestUser");
        TypedQuery<PlayerStatsEntity> fakeQuery = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT p FROM PlayerStatsEntity p WHERE p.playerUsername = :username", PlayerStatsEntity.class)).thenReturn(fakeQuery);
        when(fakeQuery.getSingleResult()).thenThrow(NoResultException.class);

        //Act
        Assertions.assertThrows(PlayerStatsNotFound.class, () -> adaptorServicePlayerStats.deletePlayerStats(fakeAuthentication));

        verify(entityManager, never()).remove(any());

    }

    @Test
    @DisplayName("deletePlayerStats does NOT remove entity when model validation fails")
    void deletePlayerStats_doesNotRemove_whenModelValidationFails(){
        //Arrange
        PlayerStatsModel model = new PlayerStatsModel();
        model.setPlayerUsername("TestUser");
        model.setPlayerLevel(1);
        model.setPlayerXp(0);
        model.setXpToNextLevel(10);
        Authentication fakeAuthentication = mock(Authentication.class);
        when(fakeAuthentication.getName()).thenReturn("TestUser");
        TypedQuery<PlayerStatsEntity> fakeQuery = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT p FROM PlayerStatsEntity p WHERE p.playerUsername = :username", PlayerStatsEntity.class)).thenReturn(fakeQuery);
        when(fakeQuery.getSingleResult()).thenThrow(NoResultException.class);

        //Act
        Assertions.assertThrows(PlayerStatsNotFound.class, () -> adaptorServicePlayerStats.deletePlayerStats(fakeAuthentication));

        verify(entityManager, never()).remove(any());

    }

    @Test
    @DisplayName("deletePlayerStats removes entity when model and authentication are valid")
    void deletePlayerStats_removesEntity_whenModelAndAuthAreValid() {
        //Arrange
        Authentication fakeAuthentication = mock(Authentication.class);
        when(fakeAuthentication.getName()).thenReturn("TestUser");

        PlayerStatsEntity fakeEntity = new PlayerStatsEntity();
        fakeEntity.setPlayerUsername("TestUser");
        fakeEntity.setPlayerLevel(1);
        fakeEntity.setPlayerXp(0);
        fakeEntity.setXpToNextLevel(10);

        TypedQuery<PlayerStatsEntity> fakeQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT p FROM PlayerStatsEntity p WHERE p.playerUsername = :username",
                PlayerStatsEntity.class
        )).thenReturn(fakeQuery);
        when(fakeQuery.setParameter(anyString(), any())).thenReturn(fakeQuery);
        when(fakeQuery.getSingleResult()).thenReturn(fakeEntity);

        //Act
        adaptorServicePlayerStats.deletePlayerStats(fakeAuthentication);

        //Assert
        verify(entityManager).remove(fakeEntity);
    }

    // ValidatePlayerStatsModel
    @Test
    @DisplayName("validatePlayerStatsModel throws when model is null")
    void validatePlayerStatsModel_throws_whenModelIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> adaptorServicePlayerStats.validatePlayerStatsModel(null));
    }

    @Test
    @DisplayName("validatePlayerStatsModel throws when username is null or blank")
    void validatePlayerStatsModel_throws_whenUsernameInvalid() {
        PlayerStatsModel model = new PlayerStatsModel();
        model.setPlayerUsername(""); // invalid
        model.setPlayerLevel(1);
        model.setPlayerXp(0);
        model.setXpToNextLevel(10);

        assertThrows(IllegalArgumentException.class,
                () -> adaptorServicePlayerStats.validatePlayerStatsModel(model));
    }

    @Test
    @DisplayName("validatePlayerStatsModel throws when player level is less than 1")
    void validatePlayerStatsModel_throws_whenLevelInvalid() {
        PlayerStatsModel model = new PlayerStatsModel();
        model.setPlayerUsername("TestUser");
        model.setPlayerLevel(0); // invalid
        model.setPlayerXp(0);
        model.setXpToNextLevel(10);

        assertThrows(IllegalArgumentException.class,
                () -> adaptorServicePlayerStats.validatePlayerStatsModel(model));
    }

    @Test
    @DisplayName("validatePlayerStatsModel throws when player XP is negative")
    void validatePlayerStatsModel_throws_whenXpInvalid() {
        PlayerStatsModel model = new PlayerStatsModel();
        model.setPlayerUsername("TestUser");
        model.setPlayerLevel(1);
        model.setPlayerXp(-5); // invalid
        model.setXpToNextLevel(10);

        assertThrows(IllegalArgumentException.class,
                () -> adaptorServicePlayerStats.validatePlayerStatsModel(model));
    }

    @Test
    @DisplayName("validatePlayerStatsModel throws when xpToNextLevel is zero or less")
    void validatePlayerStatsModel_throws_whenXpToNextLevelInvalid() {
        PlayerStatsModel model = new PlayerStatsModel();
        model.setPlayerUsername("TestUser");
        model.setPlayerLevel(1);
        model.setPlayerXp(0);
        model.setXpToNextLevel(0); // invalid

        assertThrows(IllegalArgumentException.class,
                () -> adaptorServicePlayerStats.validatePlayerStatsModel(model));
    }

    @Test
    @DisplayName("validatePlayerStatsModel passes when model is valid")
    void validatePlayerStatsModel_passes_whenModelIsValid() {
        PlayerStatsModel model = new PlayerStatsModel();
        model.setPlayerUsername("TestUser");
        model.setPlayerLevel(1);
        model.setPlayerXp(0);
        model.setXpToNextLevel(10);

        adaptorServicePlayerStats.validatePlayerStatsModel(model);
    }

    // ValidatePlayerAuthentication
    @Test
    @DisplayName("validatePlayerAuthentication throws when authentication is null")
    void validatePlayerAuthentication_throws_whenAuthIsNull() {
        assertThrows(PermissionDeniedException.class,
                () -> adaptorServicePlayerStats.validatePlayerAuthentication(null));
    }

    @Test
    @DisplayName("validatePlayerAuthentication throws when name is null or blank")
    void validatePlayerAuthentication_throws_whenNameInvalid() {
        Authentication fakeAuth = mock(Authentication.class);
        when(fakeAuth.getName()).thenReturn("");

        assertThrows(PermissionDeniedException.class,
                () -> adaptorServicePlayerStats.validatePlayerAuthentication(fakeAuth));
    }

    @Test
    @DisplayName("validatePlayerAuthentication passes when authentication is valid")
    void validatePlayerAuthentication_passes_whenValid() {
        Authentication fakeAuth = mock(Authentication.class);
        when(fakeAuth.getName()).thenReturn("ValidUser");

        adaptorServicePlayerStats.validatePlayerAuthentication(fakeAuth);
    }
}