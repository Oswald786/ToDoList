package com.todolist.Controllers;

import com.todolist.Models.TaskObjectModel;
import com.todolist.Models.UpdateTaskRequestPackage;
import com.todolist.Services.TaskManagementService;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Null;

import java.util.ArrayList;
import java.util.List;

@Secured({"ADMIN","USER"})
@Controller("/v1taskManagementController")
public class TaskManagementController {

    @Inject
    TaskManagementService taskManagementService;

    //all fot hese methods need the task owner here as well to ensure that the task is taken or altered by the owner

    @Post("/createTask")
    public void createTask(@Body TaskObjectModel taskObjectModel, @Nullable Authentication authentication){
        this.taskManagementService.createTask(taskObjectModel,authentication);
    }

    @Get("/getTasks")
    public List<TaskObjectModel> getTasks(@Nullable Authentication authentication){
        return this.taskManagementService.fetchAllTasks(authentication);
    }

    @Get("/getTaskWithId/{taskId}")
    public TaskObjectModel getTaskWithId(long taskId,@Nullable Authentication authentication){
        return this.taskManagementService.fetchTaskWithId(taskId,authentication);
    }

    @Post("/updateTask")
    public void updateTask(@Body UpdateTaskRequestPackage updateTaskRequestPackage, @Nullable Authentication authentication){
        this.taskManagementService.updateTask(updateTaskRequestPackage,authentication);
    }

    @Delete("/deleteTask")
    public void deleteTask(@Body long id, @Nullable Authentication authentication){
        this.taskManagementService.deleteTask(id,authentication);
    }
}
