package com.todolist.adaptors.web;


import com.todolist.Models.taskObjectModel;
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
public class AdaptorController {
    @PersistenceContext
    EntityManager entityManager;

    @Inject
    TaskMapper taskMapper;


    public AdaptorController() {

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
    public void updateTask(long id,String fieldToUpdate,String replacementValue) {
        TaskEntity entity = entityManager.find(TaskEntity.class, id);
        if (entity == null) {
            throw new IllegalArgumentException("Task with id " + id + " does not exist");
        }
        if (fieldToUpdate == null || fieldToUpdate.isBlank()) {
            throw new IllegalArgumentException("Field to update cannot be null or blank");
        }
        if (replacementValue == null || replacementValue.isBlank()) {
            throw new IllegalArgumentException("Replacement value cannot be null or blank");
        }
        switch (fieldToUpdate) {
            case "taskName":
                entity.setTaskName(replacementValue);
                break;
            case "taskType":
                entity.setTaskType(replacementValue);
                break;
            case "taskLevel":
                entity.setTaskLevel(replacementValue);
                break;
            case "taskDescription":
                entity.setTaskDescription(replacementValue);
                break;
            default:
                throw new IllegalArgumentException("Field " + fieldToUpdate + " does not exist");
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
}
