package com.todolist.Controllers;

import com.todolist.Models.TaskObjectModel;
import com.todolist.Models.UpdateTaskRequestPackage;
import com.todolist.Services.TaskManagementService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import jakarta.inject.Inject;

import java.util.ArrayList;

@Secured({"ADMIN","USER"})
@Controller("/v1taskManagementController")
public class TaskManagementController {

    @Inject
    TaskManagementService taskManagementService;

    //all fot hese methods need the task owner here as well to ensure that the task is taken or altered by the owner

    @Post("/createTask")
    public void createTask(TaskObjectModel taskObjectModel, Authentication authentication){
        System.out.println("Creating task for: " + authentication.getName());
        this.taskManagementService.createTask(taskObjectModel,authentication);
    }

    @Get("/getTasks")
    public ArrayList<TaskObjectModel> getTasks(Authentication authentication){
        return this.taskManagementService.fetchAllTasks(authentication);
    }

    @Get("/getTaskWithId/{taskId}")
    public TaskObjectModel getTaskWithId(long taskId, Authentication authentication){
        return this.taskManagementService.fetchTaskWithId(taskId,authentication);
    }

    @Post("/updateTask")
    public void updateTask(UpdateTaskRequestPackage updateTaskRequestPackage, Authentication authentication){
        this.taskManagementService.updateTask(updateTaskRequestPackage,authentication);
    }

    @Delete("/deleteTask")
    public void deleteTask(long id,Authentication authentication){
        this.taskManagementService.deleteTask(id,authentication);
    }
}
