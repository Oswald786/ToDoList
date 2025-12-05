package com.todolist.adaptors.web;

import com.todolist.Models.TaskObjectModel;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.inject.Inject;

import java.util.ArrayList;

@Controller("/v1AdaptorController")
public class AdaptorController {

    @Inject
    AdaptorService adaptorService;

    @Get("/getTasks")
    public ArrayList<TaskObjectModel> getTasks(){
        return this.adaptorService.fetchAllTaskModels();
    }

    @Get("/getTaskWithId")
    public TaskObjectModel getTaskWithId(int taskId){
        return this.adaptorService.retrieveTask((long) taskId);
    }
}
