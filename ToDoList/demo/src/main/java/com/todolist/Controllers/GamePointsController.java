package com.todolist.Controllers;

import com.todolist.Models.PlayerStatsModel;
import com.todolist.Models.TaskObjectModel;
import com.todolist.Services.GameService;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import jakarta.inject.Inject;

@Controller("/v1GamePoints")
@Secured({"USER","ADMIN"})
public class GamePointsController {


    @Inject
    GameService gameService;

    @Get("/RetrievePlayerStats")
    public PlayerStatsModel getPlayerLevel(Authentication authentication){
        PlayerStatsModel returnedPlayerStats = gameService.getPlayerStats(authentication);
        return returnedPlayerStats;
    }

    @Post("/AddXP")
    public void addXP(@Body TaskObjectModel taskObjectModel, Authentication authentication){
        gameService.addXPForTaskCompletion(taskObjectModel, authentication);
    }
}
