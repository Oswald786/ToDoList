package com.todolist.adaptors.persistence.jpa;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@NoArgsConstructor

@Entity
@Table(name="TASKLIST")
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TASK_ID")
    private Long id;
    
    @Column(name = "TASK_NAME")
    private String taskName;

    @Column(name = "TASKTYPE")
    private String taskType;

    @Column(name = "TASK_LEVEL")
    private String taskLevel;

    @Column(name = "TASK_DESCRIPTION")
    private String taskDescription;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskLevel() {
        return taskLevel;
    }

    public void setTaskLevel(String taskLevel) {
        this.taskLevel = taskLevel;
    }
}