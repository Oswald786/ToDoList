package com.todolist.Services;

import com.todolist.Models.PlayerStatsModel;
import com.todolist.Models.taskObjectModel;
import com.todolist.Models.userDetailsModel;
import com.todolist.adaptors.persistence.jpa.PlayerStatsEntity;
import com.todolist.adaptors.web.AdaptorServicePlayerStats;
import com.todolist.adaptors.web.PlayerStatsMapper;
import io.micronaut.security.authentication.Authentication;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Singleton
public class GameService {

    @Inject
    AdaptorServicePlayerStats adaptorServicePlayerStats;

    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    @Inject
    PlayerStatsMapper playerStatsMapper;

    public GameService() {
    }

    //add a new player stats profile if not exists
    public void createPlayerStatsProfile(userDetailsModel userDetailsModel) {
        try {
            PlayerStatsModel playerStatsModel = new PlayerStatsModel();
            playerStatsModel.setPlayerUsername(userDetailsModel.getUserName());
            playerStatsModel.setPlayerLevel(1);
            playerStatsModel.setPlayerXp(0);
            playerStatsModel.setXpToNextLevel(20);
            adaptorServicePlayerStats.createPlayerStats(playerStatsModel);
        }catch (Exception e){
            log.error(String.valueOf(e.getCause()));
            throw new IllegalArgumentException("Error creating player stats profile");
        }
    }


    //Calculate XP Reward for task completion
    public int calculateXP(taskObjectModel taskObjectModel, Authentication authentication){
        int baseXp = Integer.parseInt(taskObjectModel.getTaskLevel()) * 20;

        PlayerStatsEntity User = adaptorServicePlayerStats.retrievePlayerStats(authentication);

        double levelScaling = Math.max(1.00 - (User.getPlayerLevel() * 0.02), 0.4);

        int totalXp = (int) (baseXp * levelScaling);

        log.info("XP Reward for {} is {}", authentication.getName(), totalXp);
        return totalXp;
    }


    //Add XP to the user profile
    public void addXPForTaskCompletion(taskObjectModel taskObjectModel, Authentication authentication){
        int xp = calculateXP(taskObjectModel, authentication);
        PlayerStatsModel playerStatsModel = playerStatsMapper.toModel(adaptorServicePlayerStats.retrievePlayerStats(authentication));
        playerStatsModel.setPlayerXp(playerStatsModel.getPlayerXp() + xp);
        if(playerStatsModel.getPlayerXp() >= playerStatsModel.getXpToNextLevel()){
            levelUp(authentication);
            log.info("User {} has leveled up", authentication.getName());
        } else {
            log.info("User {} has not leveled up", authentication.getName());
            log.info("User {} has {} XP remaining", authentication.getName(), playerStatsModel.getXpToNextLevel() - playerStatsModel.getPlayerXp());
            adaptorServicePlayerStats.updatePlayerStats(playerStatsModel, authentication);
        }
    }

    //Level up the user profile if needed
    private void levelUp(Authentication authentication){
        // Retrieve the user's current stats as a model
        PlayerStatsModel playerStatsModel = playerStatsMapper.toModel(
                adaptorServicePlayerStats.retrievePlayerStats(authentication)
        );

        // --- Level-up logic ---
        int oldLevel = playerStatsModel.getPlayerLevel();
        int newLevel = oldLevel + 1;

        // Optional: Carry over leftover XP instead of resetting to zero
        int overflowXp = playerStatsModel.getPlayerXp() - playerStatsModel.getXpToNextLevel();
        if (overflowXp < 0) overflowXp = 0;

        playerStatsModel.setPlayerLevel(newLevel);
        playerStatsModel.setPlayerXp(overflowXp);
        playerStatsModel.setXpToNextLevel(calculateNewLevelUpThreshold(playerStatsModel));

        log.info("🎉 User {} leveled up! Old Level: {}, New Level: {}, XP carried over: {}",
                authentication.getName(), oldLevel, newLevel, overflowXp);

        // --- Persist the updated data ---
        adaptorServicePlayerStats.updatePlayerStats(playerStatsModel, authentication);
    }

    private int calculateNewLevelUpThreshold(PlayerStatsModel playerStatsModel){
        return playerStatsModel.getPlayerLevel() * 20;
    }

    //Calculate XP needed to level up
    public int calculateXPToLevelUp(Authentication authentication){
        PlayerStatsEntity entityReturned = adaptorServicePlayerStats.retrievePlayerStats(authentication);
        return entityReturned.getXpToNextLevel() - entityReturned.getPlayerXp();
    }


    //Grant level reward (Optional)

    //Get progress (Optional)


}
