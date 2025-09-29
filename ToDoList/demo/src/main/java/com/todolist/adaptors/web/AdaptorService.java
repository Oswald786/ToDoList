package com.todolist.adaptors.web;


import com.todolist.Models.taskObjectModel;
import com.todolist.Models.updateTaskRequestPackage;
import com.todolist.adaptors.persistence.jpa.TaskEntity;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class AdaptorService {
    @PersistenceContext
    EntityManager entityManager;

    @Inject
    TaskMapper taskMapper;


    public AdaptorService() {

    }
//-----------------------------------------CRUD BASED OPERATIONS BELOW FOR DATABASE INTERACTION----------------------------------------------------

    //Create
    @Transactional
    public void createTask(taskObjectModel taskObjectModel){
        entityManager.persist(taskMapper.toEntity(taskObjectModel));
        System.out.println("Task created");
    }


    //Read
    @Transactional
    public ArrayList<taskObjectModel> fetchAllTaskModels(){
        ArrayList<taskObjectModel> taskObjectModels = new ArrayList<>();
        TypedQuery<TaskEntity> query = entityManager.createQuery("SELECT t FROM TaskEntity t", TaskEntity.class);
        List<TaskEntity> resultList = query.getResultList();
        for(TaskEntity taskEntity: resultList){
           taskObjectModels.add(taskMapper.toModel(taskEntity));
        }
        return taskObjectModels;
    }

    @Transactional
    public taskObjectModel retrieveTask(Long id){
        TaskEntity entity = entityManager.find(TaskEntity.class, id);
        return taskMapper.toModel(entity);
    }


    //Update
    @Transactional
    public void updateTask(updateTaskRequestPackage updateTaskRequestPackage) {
        TaskEntity entity = entityManager.find(TaskEntity.class, updateTaskRequestPackage.getId());
        if (entity == null) {
            throw new IllegalArgumentException("Task with id " + updateTaskRequestPackage.getId() + " does not exist");
        }
        if (updateTaskRequestPackage.getFieldToUpdate() == null || updateTaskRequestPackage.getFieldToUpdate().isBlank()) {
            throw new IllegalArgumentException("Field to update cannot be null or blank");
        }
        if (updateTaskRequestPackage.getReplacementValue() == null || updateTaskRequestPackage.getReplacementValue().isBlank()) {
            throw new IllegalArgumentException("Replacement value cannot be null or blank");
        }
        switch (updateTaskRequestPackage.getFieldToUpdate()) {
            case "taskName":
                entity.setTaskName(updateTaskRequestPackage.getReplacementValue());
                break;
            case "taskType":
                entity.setTaskType(updateTaskRequestPackage.getReplacementValue());
                break;
            case "taskLevel":
                entity.setTaskLevel(updateTaskRequestPackage.getReplacementValue());
                break;
            case "taskDescription":
                entity.setTaskDescription(updateTaskRequestPackage.getReplacementValue());
                break;
            default:
                throw new IllegalArgumentException("Field " + updateTaskRequestPackage.getFieldToUpdate() + " does not exist");
        }
    }
    //Delete
    @Transactional
    public void deleteTask(long id){
        TaskEntity entity = entityManager.find(TaskEntity.class, id);
        if (entity == null) {
            throw new IllegalArgumentException("Task with id " + id + " does not exist");
        }
        entityManager.remove(entity);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public TaskMapper getTaskMapper() {
        return taskMapper;
    }

    public void setTaskMapper(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
