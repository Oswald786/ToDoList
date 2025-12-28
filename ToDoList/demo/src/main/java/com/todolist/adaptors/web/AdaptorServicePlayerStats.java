package com.todolist.adaptors.web;

import com.todolist.Models.PlayerStatsModel;
import com.todolist.adaptors.persistence.Jpa.PlayerStatsEntity;
import com.todolist.exceptions.PermissionDeniedException;
import com.todolist.exceptions.PlayerStatsNotFoundException;
import io.micronaut.security.authentication.Authentication;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AdaptorServicePlayerStats {
    @PersistenceContext
    EntityManager entityManager;

    private static final Logger log = LoggerFactory.getLogger(AdaptorServicePlayerStats.class);

    @Inject
    PlayerStatsMapper playerStatsMapper;




    //Basic PLayer Stats Crud Operations
    // --- CREATE ---
    @Transactional
    public void createPlayerStats(PlayerStatsModel playerStatsModel) {
        validatePlayerStatsModel(playerStatsModel);
        PlayerStatsEntity entity = playerStatsMapper.toEntity(playerStatsModel);
        // link to authenticated user
        entityManager.persist(entity);
        log.info("Player stats created for {}",entity.getPlayerUsername());
    }

    // --- READ ---
    @Transactional
    public PlayerStatsEntity retrievePlayerStats(Authentication authentication) {
        validatePlayerAuthentication(authentication);
        try {
            String username = authentication.getName();
            TypedQuery<PlayerStatsEntity> query = entityManager.createQuery(
                    "SELECT p FROM PlayerStatsEntity p WHERE p.playerUsername = :username",
                    PlayerStatsEntity.class
            );
            query.setParameter("username", username);
            PlayerStatsEntity entity = query.getSingleResult();
            return entity;
        } catch (NoResultException e) {
            log.warn("No player stats found for user {}", authentication.getName());
            throw new PlayerStatsNotFoundException("Player stats not found for user " + authentication.getName());
        }catch (NonUniqueResultException nonUniqueResultException){
            log.error("Multiple player stats found for user {}", authentication.getName());
            throw new PlayerStatsNotFoundException("Multiple player stats found for user " + authentication.getName());
        }
    }

    // --- UPDATE ---
    @Transactional
    public void updatePlayerStats(PlayerStatsModel playerStatsModel, Authentication authentication) {
        validatePlayerStatsModel(playerStatsModel);
        validatePlayerAuthentication(authentication);
        String username = authentication.getName();
        PlayerStatsEntity entity = retrievePlayerStats(authentication);
        entity.setPlayerXp(playerStatsModel.getPlayerXp());
        entity.setXpToNextLevel(playerStatsModel.getXpToNextLevel());
        entity.setPlayerLevel(playerStatsModel.getPlayerLevel());

        entityManager.merge(entity);
        log.info("Player stats updated for {}", username);
    }

    // --- DELETE ---
    @Transactional
    public void deletePlayerStats(Authentication authentication) {
            validatePlayerAuthentication(authentication);
            PlayerStatsEntity entity = retrievePlayerStats(authentication);
            if (entity != null) {
                entityManager.remove(entity);
                log.info(" Player stats deleted for {}", entity.getPlayerUsername());
            }
    }

    public void validatePlayerStatsModel(PlayerStatsModel playerStatsModel){
        if(playerStatsModel == null){
            throw new IllegalArgumentException("Player stats model cannot be null");
        }else if(playerStatsModel.getPlayerUsername() == null || playerStatsModel.getPlayerUsername().isBlank()) {
            throw new IllegalArgumentException("Player username cannot be null or blank");
        }else if (playerStatsModel.getPlayerLevel() < 1) {
            throw new IllegalArgumentException("Player level must be at least 1. Provided: " + playerStatsModel.getPlayerLevel());
        }else if (playerStatsModel.getPlayerXp() < 0) {
            throw new IllegalArgumentException("Player XP cannot be negative. Provided: " + playerStatsModel.getPlayerXp());
        }else if (playerStatsModel.getXpToNextLevel() <= 0) {
            throw new IllegalArgumentException("XP to next level must be greater than 0. Provided: " + playerStatsModel.getXpToNextLevel());
        }
    }

    public void validatePlayerAuthentication(Authentication authentication){
        if(authentication == null){
            throw new PermissionDeniedException("Authentication cannot be null");
        }else if(authentication.getName() == null || authentication.getName().isBlank()){
            throw new PermissionDeniedException("Authentication name cannot be null or blank");
        }
    }




}
