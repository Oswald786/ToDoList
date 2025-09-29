package com.todolist.adaptors.web;

import com.todolist.Models.taskObjectModel;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.inject.Inject;

import java.util.ArrayList;

@Controller("/v1AdaptorController")
public class AdaptorController {

    @Inject
    AdaptorService adaptorService;

    @Get("/getTasks")
    public ArrayList<taskObjectModel> getTasks(){
        return this.adaptorService.fetchAllTaskModels();
    }

    @Get("/getTaskWithId")
    public taskObjectModel getTaskWithId(int taskId){
        return this.adaptorService.retrieveTask((long) taskId);
    }
}
