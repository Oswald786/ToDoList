package com.todolist.Services;

import com.todolist.Models.PlayerStatsModel;
import com.todolist.Models.TaskObjectModel;
import com.todolist.Models.UserDetailsModel;
import com.todolist.adaptors.persistence.Jpa.PlayerStatsEntity;
import com.todolist.adaptors.web.AdaptorServicePlayerStats;
import com.todolist.adaptors.web.PlayerStatsMapper;
import com.todolist.exceptions.MapperFailedException;
import com.todolist.exceptions.PermissionDeniedException;
import com.todolist.exceptions.PlayerStatsNotFoundException;
import com.todolist.exceptions.PlayerUsernameNotProvidedException;
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
    public void createPlayerStatsProfile(UserDetailsModel userDetailsModel) {
        if (userDetailsModel == null){
            throw new IllegalArgumentException("User details model cannot be null");
        }
        if (userDetailsModel.getUsername() == null || userDetailsModel.getUsername().isBlank()){
            throw new PlayerUsernameNotProvidedException("Player username cannot be null or blank");
        }
        PlayerStatsModel playerStatsModel = new PlayerStatsModel();
        playerStatsModel.setPlayerUsername(userDetailsModel.getUsername());
        playerStatsModel.setPlayerLevel(1);
        playerStatsModel.setPlayerXp(0);
        playerStatsModel.setXpToNextLevel(20);
        adaptorServicePlayerStats.createPlayerStats(playerStatsModel);
    }


    //Calculate XP Reward for task completion
    public int calculateXP(TaskObjectModel taskObjectModel, Authentication authentication){
        validatePlayerAuthentication(authentication,"CalculateXP method");
        if(taskObjectModel == null || taskObjectModel.getTaskLevel() == null){
            throw new IllegalArgumentException("Task level cannot be null");
        }
        try {
            Integer.parseInt(taskObjectModel.getTaskLevel());
        }catch (NumberFormatException e){
            throw new IllegalArgumentException("Task level must be a number");
        }
        //Authentication checker
        int baseXp = Integer.parseInt(taskObjectModel.getTaskLevel()) * 20;
        PlayerStatsEntity User = adaptorServicePlayerStats.retrievePlayerStats(authentication);
        double levelScaling = Math.max(1.00 - (User.getPlayerLevel() * 0.02), 0.4);
        int totalXp = (int) (baseXp * levelScaling);
        log.info("XP Reward for {} is {}", authentication.getName(), totalXp);
        return totalXp;
    }


    //Add XP to the user profile
    public void addXPForTaskCompletion(TaskObjectModel taskObjectModel, Authentication authentication){
        validatePlayerAuthentication(authentication,"AddXPForTaskCompletion method");
        if(taskObjectModel == null || taskObjectModel.getTaskLevel() == null){
            throw new IllegalArgumentException("Task level cannot be null");
        }
        int xp = calculateXP(taskObjectModel, authentication);
        PlayerStatsModel playerStatsModel = playerStatsMapper.toModel(adaptorServicePlayerStats.retrievePlayerStats(authentication));
        playerStatsModel.setPlayerXp(playerStatsModel.getPlayerXp() + xp);
        int levelUpCounter = 0;
        while(playerStatsModel.getPlayerXp() >= playerStatsModel.getXpToNextLevel()){
            levelUp(playerStatsModel,authentication);
            levelUpCounter++;
        }
        if(levelUpCounter > 0){
            log.info("User {} has leveled up {} times", authentication.getName(), levelUpCounter);
        }
        log.info("User {} has {} XP remaining", authentication.getName(), playerStatsModel.getXpToNextLevel() - playerStatsModel.getPlayerXp());
        adaptorServicePlayerStats.updatePlayerStats(playerStatsModel, authentication);
    }

    //Level up the user profile if needed
    private void levelUp(PlayerStatsModel playerStatsModelBeingUsed,Authentication authentication){
        validatePlayerAuthentication(authentication,"LevelUp method");
        // Retrieve the user's current stats as a model

        // --- Level-up logic ---
        int oldLevel = playerStatsModelBeingUsed.getPlayerLevel();
        int newLevel = oldLevel + 1;

        // Optional: Carry over leftover XP instead of resetting to zero
        int overflowXp = playerStatsModelBeingUsed.getPlayerXp() - playerStatsModelBeingUsed.getXpToNextLevel();
        if (overflowXp < 0) overflowXp = 0;

        playerStatsModelBeingUsed.setPlayerLevel(newLevel);
        playerStatsModelBeingUsed.setPlayerXp(overflowXp);
        playerStatsModelBeingUsed.setXpToNextLevel(calculateNewLevelUpThreshold(playerStatsModelBeingUsed));

        log.info(" User {} leveled up! Old Level: {}, New Level: {}, XP carried over: {}",
                authentication.getName(), oldLevel, newLevel, overflowXp);
    }

    private int calculateNewLevelUpThreshold(PlayerStatsModel playerStatsModel){
        if(playerStatsModel == null){
            throw new PlayerStatsNotFoundException("Player stats not found Unable to calculate new level up threshold");
        }else if(playerStatsModel.getPlayerUsername() == null || playerStatsModel.getPlayerUsername().isEmpty()){
            throw new MapperFailedException("Mapper failed to map player stats to model at calculateNewLevelUpThreshold method.");
        }
        return playerStatsModel.getPlayerLevel() * 20;
    }


    //Grant level reward (Optional)

    //Get progress player stats and return as model
    public PlayerStatsModel getPlayerStats(Authentication authentication){
        validatePlayerAuthentication(authentication,"GetPlayerStats method");
        return playerStatsMapper.toModel(adaptorServicePlayerStats.retrievePlayerStats(authentication));
    }


    //Validate player Authentication
    public void validatePlayerAuthentication(Authentication authentication,String ErrorLocation) {
       if (authentication == null || authentication.getName() == null) {
           throw new IllegalStateException("Authentication is null or username is null at " + ErrorLocation + ".");
       }
       else if(authentication.getRoles().isEmpty()) {
           throw new PermissionDeniedException("User does not have permission to access this resource at " + ErrorLocation + ".");
       }
       PlayerStatsModel playerStatsModel = playerStatsMapper.toModel(adaptorServicePlayerStats.retrievePlayerStats(authentication));
       if(playerStatsModel == null){
           throw new PlayerStatsNotFoundException("Player stats not found for user " + authentication.getName() + " at " + ErrorLocation + ".");
       }else if(playerStatsModel.getPlayerUsername() == null || playerStatsModel.getPlayerUsername().isEmpty()){
           throw new MapperFailedException("Mapper failed to map player stats to model at " + ErrorLocation + ".");
       }
    }
}
