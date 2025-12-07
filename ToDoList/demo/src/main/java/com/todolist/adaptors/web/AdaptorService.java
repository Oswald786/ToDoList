package com.todolist.adaptors.web;


import com.todolist.Models.TaskObjectModel;
import com.todolist.Models.UpdateTaskRequestPackage;
import com.todolist.adaptors.persistence.Jpa.TaskEntity;
import com.todolist.exceptions.MapperFailedException;
import com.todolist.exceptions.TaskNotFoundException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Singleton
public class AdaptorService {
    @PersistenceContext
    EntityManager entityManager;

    @Inject
    TaskMapper taskMapper;

    private static final Logger log = LoggerFactory.getLogger(AdaptorService.class);





    public AdaptorService() {

    }
//-----------------------------------------CRUD BASED OPERATIONS BELOW FOR DATABASE INTERACTION----------------------------------------------------

    //Create
    @Transactional
    public void createTask(TaskObjectModel taskObjectModel){
        // Validate the incoming model
        validateTaskObjectModel(taskObjectModel);
        TaskEntity entity = taskMapper.toEntity(taskObjectModel);
        if (entity == null) {
            throw new MapperFailedException("TaskMapper failed to map TaskObjectModel to TaskEntity");
        }
        entityManager.persist(entity);
        log.info("Task created for owner '{}' with name '{}'", taskObjectModel.getTaskOwnerId(), taskObjectModel.getTaskName());
    }


    //Read
    @Transactional
    public ArrayList<TaskObjectModel> fetchAllTaskModels(){
            TypedQuery<TaskEntity> query = entityManager.createQuery("SELECT t FROM TaskEntity t", TaskEntity.class);

            List<TaskEntity> taskEntities = query.getResultList();
            ArrayList<TaskObjectModel> taskModels = new ArrayList<>();

            for (TaskEntity entity : taskEntities) {
                TaskObjectModel model = taskMapper.toModel(entity);
                if (model == null) {
                    log.error("TaskMapper returned null for TaskEntity with id {}", entity.getId());
                    throw new MapperFailedException("Task mapping failed — model was null for entity id " + entity.getId());
                }
                taskModels.add(model);
            }
            log.info("Fetched {} tasks from database (admin view)", taskModels.size());
            return taskModels;
    }

    @Transactional
    public ArrayList<TaskObjectModel> fetchAllTasksByOwner(String taskOwnerId){

        if(taskOwnerId == null || taskOwnerId.isBlank()){
            throw new IllegalArgumentException("Task owner ID cannot be null or blank");
        }
        ArrayList<TaskObjectModel> taskObjectModelsOwned = new ArrayList<>();

        TypedQuery<TaskEntity> query = entityManager.createQuery("SELECT t FROM TaskEntity t WHERE t.taskOwnerId = :owner", TaskEntity.class);
        query.setParameter("owner", taskOwnerId);

        List<TaskEntity> resultList = query.getResultList();

        for (TaskEntity taskEntity : resultList) {
            TaskObjectModel taskObjectModel = taskMapper.toModel(taskEntity);
            if (taskObjectModel == null) {
                log.error("Task mapper returned null for TaskEntity with id {}", taskEntity.getId());
                throw new MapperFailedException("Task mapping failed — model was null for entity id " + taskEntity.getId());
            }
            taskObjectModelsOwned.add(taskObjectModel);
        }
        return taskObjectModelsOwned;
    }

    @Transactional
    public TaskObjectModel retrieveTask(Long id){
        //need to add a parameter for the task owner id here to ensure that only tasks owned by the owner are returned
        //also add some error handling and some catches for if this is not the case
        if(id == null){
            throw new IllegalArgumentException("Task id cannot be null");
        }
        TaskEntity entity = entityManager.find(TaskEntity.class, id);
        if(entity == null){
            throw new TaskNotFoundException("Task with id " + id + " does not exist");
        }
        TaskObjectModel taskObjectModel = taskMapper.toModel(entity);
        validateTaskObjectModel(taskObjectModel);
        log.info("Task with id {} retrieved successfully", id);
        return taskObjectModel;
    }


    //Update
    @Transactional
    public void updateTask(UpdateTaskRequestPackage updateTaskRequestPackage) {
        if (updateTaskRequestPackage.getFieldToUpdate() == null || updateTaskRequestPackage.getFieldToUpdate().isBlank()) {
            throw new IllegalArgumentException("Field to update cannot be null or blank");
        }
        if (updateTaskRequestPackage.getReplacementValue() == null || updateTaskRequestPackage.getReplacementValue().isBlank()) {
            throw new IllegalArgumentException("Replacement value cannot be null or blank");
        }
            //Confirm the Task is valid and exists
            TaskObjectModel existingModel = retrieveTask(updateTaskRequestPackage.getId());

            //Used so changes are tracked by entity manager and saved
            TaskEntity entity = entityManager.find(TaskEntity.class, updateTaskRequestPackage.getId());

            String field = updateTaskRequestPackage.getFieldToUpdate();
            String value = updateTaskRequestPackage.getReplacementValue();

            // Extra field-specific validation
            if (field.equals("taskLevel")) {
                try {
                    Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("taskLevel must be numeric");
                }
            }

            // Update only the allowed fields
            switch (field) {
                case "taskName":
                    entity.setTaskName(value);
                    break;
                case "taskType":
                    entity.setTaskType(value);
                    break;
                case "taskLevel":
                    entity.setTaskLevel(value);
                    break;
                case "taskDescription":
                    entity.setTaskDescription(value);
                    break;
                default:
                    throw new TaskNotFoundException("Field " + updateTaskRequestPackage.getFieldToUpdate() + " does not exist");
            }
            // Optional logging (helpful for auditing)
            log.info("Task with id {} updated field '{}' to '{}'", updateTaskRequestPackage.getId(), field, value);
    }

    //Delete
    @Transactional
    public void deleteTask(long id){
        if(id == 0 || id < 0){
            log.warn("Attempt to delete task with id {} was ignored", id);
            log.error("Failed to delete task with id {}",id);
            throw new IllegalArgumentException("Task id cannot be null or less than zero");
        }
        //checkTask existence
        TaskObjectModel existingTask = retrieveTask(id);
        //task then retrieved via entity manager for java persistence tracking.
        TaskEntity entity = entityManager.find(TaskEntity.class, id);
        entityManager.remove(entity);
    }

    public void validateTaskObjectModel(TaskObjectModel model) {

        if (model == null) {
            throw new IllegalArgumentException("TaskObjectModel cannot be null");
        }

        if (model.getTaskName() == null || model.getTaskName().isBlank()) {
            throw new IllegalArgumentException("Task name cannot be null or blank");
        }

        if (model.getTaskType() == null || model.getTaskType().isBlank()) {
            throw new IllegalArgumentException("Task type cannot be null or blank");
        }

        if (model.getTaskLevel() == null || model.getTaskLevel().isBlank()) {
            throw new IllegalArgumentException("Task level cannot be null or blank");
        }

        if (model.getTaskOwnerId() == null || model.getTaskOwnerId().isBlank()) {
            throw new IllegalArgumentException("Task owner ID cannot be null or blank");
        }
    }
}
