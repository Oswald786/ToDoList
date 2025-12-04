package com.todolist.adaptors.web;

import com.todolist.Models.PlayerStatsModel;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

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
                () ->adaptorServicePlayerStats.createPlayerStats(null)
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

    //retrieve player stats testing

}