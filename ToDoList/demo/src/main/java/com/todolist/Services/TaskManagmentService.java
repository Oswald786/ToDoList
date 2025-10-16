package com.todolist.Services;

import com.todolist.Models.taskObjectModel;
import com.todolist.Models.updateTaskRequestPackage;
import com.todolist.adaptors.web.AdaptorService;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class TaskManagmentService {

    private static final Logger log = LoggerFactory.getLogger(TaskManagmentService.class);

    @Inject
    AdaptorService adaptorService;




    public TaskManagmentService() {

    }
    //add the task owner id to all arguments

    public void createTask(taskObjectModel taskObjectModel){
        log.info("Task creation in progress");
        try {
        if(!validateTaskObjectModel(taskObjectModel)){
            //add the owner id with the task object model so the takes will be added to the owner's task list'
            this.adaptorService.createTask(taskObjectModel);
            log.info("Task created");
        }else if (validateTaskObjectModel(taskObjectModel)){
            log.warn("Task object model is invalid");
        }
        }catch (Exception e){
            e.printStackTrace();
            log.error(String.valueOf(e.getCause()));
        }
    }

    public void updateTask(updateTaskRequestPackage updateTaskRequestPackage){
        log.info("Creating update task request");
        try {
            //need to validate here the task owner id proivided matches with the task id provided
            this.adaptorService.updateTask(updateTaskRequestPackage);
        }catch (Exception e){
            e.printStackTrace();
            log.error(String.valueOf(e.getCause()));
            throw new IllegalArgumentException("Task with id " + updateTaskRequestPackage.getId() + " does not exist");
        }
    }

    public void deleteTask(long id){
        log.info("Creating delete task request");
        try {
            //need to validate here the task owner id proivided matches with the task id provided
            adaptorService.deleteTask(id);
            log.info("Task deleted");
        }catch (Exception e){
            e.printStackTrace();
            log.error(String.valueOf(e.getCause()));
        }

    }
    public ArrayList<taskObjectModel> fetchAllTasks(){
        log.info("Fetching all tasks");
        try {
            //need to validate here the task owner id provided matches with all the task id's provided
            return adaptorService.fetchAllTaskModels();
        }catch (Exception e){
            e.printStackTrace();
            log.error(String.valueOf(e.getCause()));
            return null;
        }

    }
    public taskObjectModel fetchTaskWithId(long id){
        log.info("Fetching task with id");
        try {

            //need to validate here the task owner id provided matches with the task id provided
            taskObjectModel retrievedEntity = adaptorService.retrieveTask(id);
            return retrievedEntity;
        }catch (Exception e){
            e.printStackTrace();
            log.error(String.valueOf(e.getCause()));
            return null;
        }
    }

    public boolean validateTaskObjectModel(taskObjectModel taskObjectModel){
        ArrayList<String> errors = new ArrayList<>();
        if(taskObjectModel.getTaskName() == null || taskObjectModel.getTaskName().isEmpty()){
            errors.add("Task name cannot be empty");
        }
        if(taskObjectModel.getTaskType() == null || taskObjectModel.getTaskType().isEmpty()){
            errors.add("Task type cannot be empty");
        }
        if(taskObjectModel.getTaskLevel() == null || taskObjectModel.getTaskLevel().isEmpty()){
            errors.add("Task level cannot be empty");
        }
        if(taskObjectModel.getTaskDescription() == null || taskObjectModel.getTaskDescription().isEmpty()){
            errors.add("Task description cannot be empty");
        }
        if(!errors.isEmpty()){
            return false;
        }else{
            log.info("Task object model is valid");
            return true;
        }
    }


    //create a validator to use elsewhere to check the takes owner matches the task owner id provided
    //ensure it is tested thoroughly


}
