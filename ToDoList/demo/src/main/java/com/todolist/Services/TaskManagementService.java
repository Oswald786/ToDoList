package com.todolist.Services;

import com.todolist.Models.TaskObjectModel;
import com.todolist.Models.UpdateTaskRequestPackage;
import com.todolist.adaptors.web.AdaptorService;
import com.todolist.exceptions.PermissionDeniedException;
import com.todolist.exceptions.TaskNotFoundException;
import io.micronaut.security.authentication.Authentication;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class TaskManagementService {

    private static final Logger log = LoggerFactory.getLogger(TaskManagementService.class);

    @Inject
    AdaptorService adaptorService;

    @Inject
    GameService gameService;




    public TaskManagementService() {

    }
    //add the task owner id to all arguments

    public void createTask(TaskObjectModel taskObjectModel, Authentication authentication){
            log.info("Task creation in progress");
            log.info("Auth received in createTask(): {}", authentication);
            if(authentication == null){
                throw new IllegalStateException("Authentication is null");
            }else if(taskObjectModel == null){
                throw new IllegalStateException("Task object model is null");
            }
            validateTaskObjectModel(taskObjectModel,authentication);
            taskObjectModel.setTaskOwnerId(authentication.getName());
            this.adaptorService.createTask(taskObjectModel);
            log.info("Task created");

    }

    public void updateTask(UpdateTaskRequestPackage updateTaskRequestPackage, Authentication authentication){
        log.info("Creating update task request");
        //Covers any state exceptions
        if(authentication == null){
            throw new IllegalStateException("Authentication is null");
        }else if(updateTaskRequestPackage == null){
            throw new IllegalStateException("Update task request package is null");
        }
        //Checks for any illegal argument exceptions from updateTaskRequestPackage
        if(updateTaskRequestPackage.getFieldToUpdate() == null || updateTaskRequestPackage.getReplacementValue() == null
        || updateTaskRequestPackage.getFieldToUpdate().isBlank() || updateTaskRequestPackage.getReplacementValue().isBlank()){
            throw new IllegalArgumentException("Field to update or replacement value cannot be null or blank");
        }
        //need to validate here the task owner id provided matches with the task id provided
        TaskObjectModel taskToUpdate = adaptorService.retrieveTask(updateTaskRequestPackage.getId());
        if(!validateTaskOwnershipAndAuthority(taskToUpdate,authentication,List.of("ADMIN","USER"))){
            log.warn("User does not have permission to update this task");
            throw new PermissionDeniedException("User does not have permission to update this task");
        }
        this.adaptorService.updateTask(updateTaskRequestPackage);
    }

    public void deleteTask(long id,Authentication authentication){
        if (authentication == null) {
            throw new IllegalStateException("Authentication is null");
        }
        log.info("Creating delete task request");
        //need to validate here the task owner id provided matches with the task id provided
        TaskObjectModel taskToDelete = adaptorService.retrieveTask(id);
        if(!validateTaskOwnershipAndAuthority(taskToDelete,authentication,List.of("ADMIN","USER"))){
            log.warn("User does not have permission to delete this task");
            throw new PermissionDeniedException("User does not have permission to delete this task");
        }
        adaptorService.deleteTask(id);
        log.info("Task deleted");
        gameService.addXPForTaskCompletion(taskToDelete,authentication);
    }

    public ArrayList<TaskObjectModel> fetchAllTasks(Authentication authentication){
        log.info("Fetching all tasks");
        if(authentication == null){
            throw new IllegalStateException("Authentication is null");
        }
        //need to validate here the task owner id provided matches with all the task ids provided
        return adaptorService.fetchAllTasksByOwner(authentication.getName());
    }

    public TaskObjectModel fetchTaskWithId(long id, Authentication authentication){
        log.info("Fetching task with id");
        TaskObjectModel retrievedEntity = adaptorService.retrieveTask(id);
        //need to validate here the task owner id provided matches with the task id provided
        if(!validateTaskOwnershipAndAuthority(retrievedEntity,authentication,List.of("ADMIN","USER"))){
            throw new PermissionDeniedException("User does not have permission to view this task");
        }
        return retrievedEntity;
    }

    public void validateTaskObjectModel(TaskObjectModel taskObjectModel, Authentication authentication){
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
            throw new IllegalArgumentException(String.join(", ",errors));
        }else{
            log.info("Task object model is valid");
        }
    }

    public boolean validateTaskOwnershipAndAuthority(TaskObjectModel task,
                                                     Authentication authentication,
                                                     List<String> allowedRoles) {
        System.out.println("Validating task ownership and authority");

        boolean isOwner = task.getTaskOwnerId().equals(authentication.getName());

        boolean hasAllowedRole = authentication.getRoles().stream().anyMatch(allowedRoles::contains);

        // Either the user owns the task OR has an allowed role (e.g., ADMIN)
        System.out.println("isOwner: " + isOwner + " hasAllowedRole: " + hasAllowedRole);
        return isOwner || hasAllowedRole;
    }

}
