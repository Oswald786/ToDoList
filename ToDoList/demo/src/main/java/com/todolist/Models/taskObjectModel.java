package com.todolist.Models;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Serdeable
@Getter
@Setter
public class taskObjectModel {
        private Long id;


        private String taskOwnerId;


        private String taskName;


        private String taskType;


        private String taskLevel;


        private String taskDescription;

    public taskObjectModel(Long id,String taskOwner, String taskName, String taskType, String taskLevel, String taskDescription) {
        this.id = id;
        this.taskOwnerId = taskOwner;
        this.taskName = taskName;
        this.taskType = taskType;
        this.taskLevel = taskLevel;
        this.taskDescription = taskDescription;
    }

    public taskObjectModel() {
    }
}
