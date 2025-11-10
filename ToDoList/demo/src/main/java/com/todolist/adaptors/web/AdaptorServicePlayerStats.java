package com.todolist.adaptors.web;

import com.todolist.Models.PlayerStatsModel;
import com.todolist.adaptors.persistence.Jpa.PlayerStatsEntity;
import io.micronaut.security.authentication.Authentication;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
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
        try {
            PlayerStatsEntity entity = playerStatsMapper.toEntity(playerStatsModel);
            // link to authenticated user
            entityManager.persist(entity);
            System.out.println("Player stats created");
            System.out.println("Player stats created");
            log.info("Player stats created for {}",entity.getPlayerUsername());
        } catch (Exception e) {
            log.error("Error creating player stats: {}", e.getMessage());
            throw new IllegalArgumentException("Error creating player stats", e);
        }
    }

    // --- READ ---
    @Transactional
    public PlayerStatsEntity retrievePlayerStats(Authentication authentication) {
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
            return null;
        } catch (Exception e) {
            log.error("Error retrieving player stats: {}", e.getMessage());
            throw new RuntimeException("Error retrieving player stats", e);
        }
    }

    // --- UPDATE ---
    @Transactional
    public void updatePlayerStats(PlayerStatsModel playerStatsModel, Authentication authentication) {
        try {
            String username = authentication.getName();
            TypedQuery<PlayerStatsEntity> query = entityManager.createQuery(
                    "SELECT p FROM PlayerStatsEntity p WHERE p.playerUsername = :username",
                    PlayerStatsEntity.class
            );
            query.setParameter("username", username);

            PlayerStatsEntity entity = query.getSingleResult();
            entity.setPlayerXp(playerStatsModel.getPlayerXp());
            entity.setXpToNextLevel(playerStatsModel.getXpToNextLevel());
            entity.setPlayerLevel(playerStatsModel.getPlayerLevel());

            entityManager.merge(entity);
            log.info("Player stats updated for {}", username);
        } catch (Exception e) {
            log.error("Error updating player stats: {}", e.getMessage());
            throw new IllegalArgumentException("Error updating player stats", e);
        }
    }

    // --- DELETE ---
    @Transactional
    public void deletePlayerStats(Authentication authentication) {
        try {
            String username = authentication.getName();
            TypedQuery<PlayerStatsEntity> query = entityManager.createQuery(
                    "SELECT p FROM PlayerStatsEntity p WHERE p.playerUsername = :username",
                    PlayerStatsEntity.class
            );
            query.setParameter("username", username);

            PlayerStatsEntity entity = query.getSingleResult();
            if (entity != null) {
                entityManager.remove(entity);
                log.info(" Player stats deleted for {}", username);
            } else {
                throw new IllegalArgumentException("Player stats for " + username + " do not exist");
            }

        } catch (NoResultException e) {
            log.warn(" Attempted to delete stats but none found for {}", authentication.getName());
        } catch (Exception e) {
            log.error("Error deleting player stats: {}", e.getMessage());
            throw new IllegalArgumentException("Error deleting player stats", e);
        }
    }


}
