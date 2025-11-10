package com.todolist.Controllers;

import com.todolist.Models.TaskObjectModel;
import com.todolist.Models.UpdateTaskRequestPackage;
import com.todolist.Services.TaskManagmentService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import jakarta.inject.Inject;

import java.util.ArrayList;

@Secured({"ADMIN","USER"})
@Controller("/v1taskManagmentController")
public class TaskManagmentController {

    @Inject
    TaskManagmentService taskManagmentService;

    //all fot hese methods need the task owner here as well to ensure that the task is taken or altered by the owner

    @Post("/createTask")
    public void createTask(TaskObjectModel taskObjectModel, Authentication authentication){
        System.out.println("Creating task for: " + authentication.getName());
        this.taskManagmentService.createTask(taskObjectModel,authentication);
    }

    @Get("/getTasks")
    public ArrayList<TaskObjectModel> getTasks(Authentication authentication){
        return this.taskManagmentService.fetchAllTasks(authentication);
    }

    @Get("/getTaskWithId/{taskId}")
    public TaskObjectModel getTaskWithId(long taskId, Authentication authentication){
        return this.taskManagmentService.fetchTaskWithId(taskId,authentication);
    }

    @Post("/updateTask")
    public void updateTask(UpdateTaskRequestPackage updateTaskRequestPackage, Authentication authentication){
        this.taskManagmentService.updateTask(updateTaskRequestPackage,authentication);
    }

    @Delete("/deleteTask")
    public void deleteTask(long id,Authentication authentication){
        this.taskManagmentService.deleteTask(id,authentication);
    }
}
