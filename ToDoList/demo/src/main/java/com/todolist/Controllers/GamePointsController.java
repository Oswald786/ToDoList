package com.todolist.Controllers;

import com.todolist.Models.TaskObjectModel;
import com.todolist.Services.GameService;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import jakarta.inject.Inject;

@Controller("/gamePoints")
@Secured({"USER","ADMIN"})
public class GamePointsController {


    @Inject
    GameService gameService;

    @Post("/v1rewardTaskCompletion")
    public void rewardTaskCompletion(@Body TaskObjectModel taskObjectModel, Authentication Authentication){
        try{
            gameService.addXPForTaskCompletion(taskObjectModel, Authentication);
            System.out.println("Task completion reward added");
        }catch (Exception e){
            e.printStackTrace();
            e.getCause();
        }
    }
}
