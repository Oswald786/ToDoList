package com.todolist.Models;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;

@Serdeable
@Getter
@Setter
public class TaskObjectModel {
        private Long id;


        private String taskOwnerId;


        private String taskName;


        private String taskType;


        private String taskLevel;


        private String taskDescription;

    public TaskObjectModel(Long id, String taskOwner, String taskName, String taskType, String taskLevel, String taskDescription) {
        this.id = id;
        this.taskOwnerId = taskOwner;
        this.taskName = taskName;
        this.taskType = taskType;
        this.taskLevel = taskLevel;
        this.taskDescription = taskDescription;
    }

    public TaskObjectModel() {
    }
}
