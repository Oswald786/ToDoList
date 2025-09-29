package com.todolist.Models;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
@Serdeable
public class taskObjectModel {
        private Long id;


        private String taskName;


        private String taskType;


        private String taskLevel;


        private String taskDescription;

    public taskObjectModel(Long id, String taskName, String taskType, String taskLevel, String taskDescription) {
        this.id = id;
        this.taskName = taskName;
        this.taskType = taskType;
        this.taskLevel = taskLevel;
        this.taskDescription = taskDescription;
    }

    public taskObjectModel() {
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskLevel() {
        return taskLevel;
    }

    public void setTaskLevel(String taskLevel) {
        this.taskLevel = taskLevel;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }
}
