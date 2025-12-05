package com.todolist.adaptors.web;

import com.todolist.Models.PlayerStatsModel;
import com.todolist.adaptors.persistence.Jpa.PlayerStatsEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerStatsMapperTest {

    PlayerStatsMapper playerStatsMapper = new PlayerStatsMapperImpl();

    @Test
    @DisplayName("Tests mapper converts entity's to model objects")
    void toModel() {
        //Arrange
        PlayerStatsEntity playerStatsEntity = new PlayerStatsEntity();
        playerStatsEntity.setPlayerProfileId(1L);
        playerStatsEntity.setPlayerUsername("TestUser");
        playerStatsEntity.setPlayerLevel(1);
        playerStatsEntity.setPlayerXp(0);
        playerStatsEntity.setXpToNextLevel(150);

        //Act
        PlayerStatsModel testResult = playerStatsMapper.toModel(playerStatsEntity);

        //Assert
        Assertions.assertEquals(1L,testResult.getPlayerProfileId());
        Assertions.assertEquals("TestUser",testResult.getPlayerUsername());
        Assertions.assertEquals(1,testResult.getPlayerLevel());
        Assertions.assertEquals(0,testResult.getPlayerXp());
        Assertions.assertEquals(150,testResult.getXpToNextLevel());
    }

    @Test
    @DisplayName("Tests mapper converts model objects to entity's")
    void toEntity() {
        //Arrange
        PlayerStatsModel playerStatsModel = new PlayerStatsModel();
        playerStatsModel.setPlayerProfileId(1L);
        playerStatsModel.setPlayerUsername("TestUser");
        playerStatsModel.setPlayerLevel(1);
        playerStatsModel.setPlayerXp(0);
        playerStatsModel.setXpToNextLevel(150);

        //Act
        PlayerStatsEntity testResult = playerStatsMapper.toEntity(playerStatsModel);

        //Assert
        Assertions.assertEquals(1L,testResult.getPlayerProfileId());
        Assertions.assertEquals("TestUser",testResult.getPlayerUsername());
        Assertions.assertEquals(1,testResult.getPlayerLevel());
        Assertions.assertEquals(0,testResult.getPlayerXp());
        Assertions.assertEquals(150,testResult.getXpToNextLevel());
    }
}