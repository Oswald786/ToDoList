package com.todolist.Services;

import com.todolist.Models.taskObjectModel;
import com.todolist.Models.updateTaskRequestPackage;
import com.todolist.adaptors.web.AdaptorService;
import io.micronaut.security.authentication.Authentication;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class TaskManagmentService {

    private static final Logger log = LoggerFactory.getLogger(TaskManagmentService.class);

    @Inject
    AdaptorService adaptorService;




    public TaskManagmentService() {

    }
    //add the task owner id to all arguments

    public void createTask(taskObjectModel taskObjectModel, Authentication authentication){
        try{
            log.info("Task creation in progress");
            taskObjectModel.setTaskOwnerId(authentication.getName());
            this.adaptorService.createTask(taskObjectModel);
            log.info("Task created");
        }catch (Exception e){
            e.printStackTrace();
            log.error(String.valueOf(e.getCause()));
        }
    }

    public void updateTask(updateTaskRequestPackage updateTaskRequestPackage,Authentication authentication){
        log.info("Creating update task request");
        try {
            //need to validate here the task owner id provided matches with the task id provided
            taskObjectModel tasktoUpdate = adaptorService.retrieveTask(updateTaskRequestPackage.getId());
            if(!validateTaskOwnershipAndAuthority(tasktoUpdate,authentication,List.of("ADMIN","USER"))){
                log.warn("User does not have permission to update this task");
                throw new IllegalArgumentException("User does not have permission to update this task");
            }else {
                this.adaptorService.updateTask(updateTaskRequestPackage);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error(String.valueOf(e.getCause()));
            throw new IllegalArgumentException("Task with id " + updateTaskRequestPackage.getId() + " does not exist");
        }
    }

    public void deleteTask(long id,Authentication authentication){
        log.info("Creating delete task request");
        try {
            //need to validate here the task owner id provided matches with the task id provided
            taskObjectModel taskToDelete = adaptorService.retrieveTask(id);
            if(!validateTaskOwnershipAndAuthority(taskToDelete,authentication,List.of("ADMIN","USER"))){
                log.warn("User does not have permission to delete this task");
                throw new IllegalArgumentException("User does not have permission to delete this task");
            }else {
                adaptorService.deleteTask(id);
                log.info("Task deleted");
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error(String.valueOf(e.getCause()));
        }

    }
    public ArrayList<taskObjectModel> fetchAllTasks(Authentication authentication){
        log.info("Fetching all tasks");
        try {
            //need to validate here the task owner id provided matches with all the task id's provided
            return adaptorService.fetchAllTasksByOwner(authentication.getName());
        }catch (Exception e){
            e.printStackTrace();
            log.error(String.valueOf(e.getCause()));
            return null;
        }

    }
    public taskObjectModel fetchTaskWithId(long id,Authentication authentication){
        log.info("Fetching task with id");
        taskObjectModel retrievedEntity = adaptorService.retrieveTask(id);
        try {
            //need to validate here the task owner id provided matches with the task id provided
            if(!validateTaskOwnershipAndAuthority(retrievedEntity,authentication,List.of("ADMIN","USER"))){
                log.warn("User does not have permission to view this task");
                throw new IllegalArgumentException("User does not have permission to view this task");
            }
            else {
                return retrievedEntity;
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error(String.valueOf(e.getCause()));
            return null;
        }
    }

    public boolean validateTaskObjectModel(taskObjectModel taskObjectModel,Authentication authentication){
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

    public boolean validateTaskOwnershipAndAuthority(taskObjectModel task,
                                                     Authentication authentication,
                                                     List<String> allowedRoles) {
        System.out.println("Validating task ownership and authority");

        boolean isOwner = task.getTaskOwnerId().equals(authentication.getName());

        boolean hasAllowedRole = authentication.getRoles().stream().anyMatch(allowedRoles::contains);

        // Either the user owns the task OR has an allowed role (e.g. ADMIN)
        System.out.println("isOwner: " + isOwner + " hasAllowedRole: " + hasAllowedRole);
        return isOwner || hasAllowedRole;
    }

}
