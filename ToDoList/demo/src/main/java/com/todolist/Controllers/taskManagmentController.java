package com.todolist.Controllers;

import com.todolist.Models.taskObjectModel;
import com.todolist.Models.updateTaskRequestPackage;
import com.todolist.Services.TaskManagmentService;
import com.todolist.adaptors.web.AdaptorService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;

import java.util.ArrayList;

@Secured({"ADMIN","USER"})
@Controller("/v1taskManagmentController")
public class taskManagmentController {

    @Inject
    TaskManagmentService taskManagmentService;

    //all fot hese methods need the task owner here as well to ensure that the task is taken or altered by the owner

    @Post("/createTask")
    public void createTask(taskObjectModel taskObjectModel, Authentication authentication){
        System.out.println("Creating task for: " + authentication.getName());
        this.taskManagmentService.createTask(taskObjectModel,authentication);
    }

    @Get("/getTasks")
    public ArrayList<taskObjectModel> getTasks(Authentication authentication){
        return this.taskManagmentService.fetchAllTasks(authentication);
    }

    @Get("/getTaskWithId/{taskId}")
    public taskObjectModel getTaskWithId(long taskId,Authentication authentication){
        return this.taskManagmentService.fetchTaskWithId(taskId,authentication);
    }

    @Post("/updateTask")
    public void updateTask(updateTaskRequestPackage updateTaskRequestPackage,Authentication authentication){
        this.taskManagmentService.updateTask(updateTaskRequestPackage,authentication);
    }

    @Delete("/deleteTask")
    public void deleteTask(long id,Authentication authentication){
        this.taskManagmentService.deleteTask(id,authentication);
    }
}
