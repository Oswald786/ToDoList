package com.todolist.Models;

import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.serde.config.annotation.SerdeConfig;
import lombok.Getter;
import lombok.Setter;
@Serdeable
@Getter
@Setter
public class PlayerStatsModel {


    private Long playerProfileId;
    private String playerUsername;
    private Integer playerLevel;
    private Integer playerXp;
    private Integer xpToNextLevel;

    // --- Constructors ---

    public PlayerStatsModel() {}

    public PlayerStatsModel(Long playerProfileId, String playerUsername, Integer playerLevel, Integer playerXp, Integer xpToNextLevel) {
        this.playerProfileId = playerProfileId;
        this.playerUsername = playerUsername;
        this.playerLevel = playerLevel;
        this.playerXp = playerXp;
        this.xpToNextLevel = xpToNextLevel;
    }
}
