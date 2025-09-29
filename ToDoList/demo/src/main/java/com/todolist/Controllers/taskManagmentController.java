package com.todolist.Controllers;

import com.todolist.Models.taskObjectModel;
import com.todolist.Models.updateTaskRequestPackage;
import com.todolist.adaptors.web.AdaptorService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;

import java.util.ArrayList;
@Controller("/v1taskManagmentController")
public class taskManagmentController {

    @Inject
    AdaptorService adaptorService;

    @Post("/createTask")
    public void createTask(taskObjectModel taskObjectModel){
        this.adaptorService.createTask(taskObjectModel);
    }

    @Get("/getTasks")
    public ArrayList<taskObjectModel> getTasks(){
        return this.adaptorService.fetchAllTaskModels();
    }

    @Get("/getTaskWithId/{taskId}")
    public taskObjectModel getTaskWithId(long taskId){
        return this.adaptorService.retrieveTask(taskId);
    }

    @Post("/updateTask")
    public void updateTask(updateTaskRequestPackage updateTaskRequestPackage){
        this.adaptorService.updateTask(updateTaskRequestPackage);
    }

    @Delete("/deleteTask")
    public void deleteTask(long id){
        this.adaptorService.deleteTask(id);
    }
}
