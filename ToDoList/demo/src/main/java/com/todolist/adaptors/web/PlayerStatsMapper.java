package com.todolist.adaptors.web;

import com.todolist.Models.PlayerStatsModel;
import com.todolist.adaptors.persistence.jpa.PlayerStatsEntity;
import jakarta.inject.Singleton;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jsr330")
public interface PlayerStatsMapper {
    
    PlayerStatsModel toModel(PlayerStatsEntity playerStatsEntity);
    PlayerStatsEntity toEntity(PlayerStatsModel playerStatsModel);

    
}
