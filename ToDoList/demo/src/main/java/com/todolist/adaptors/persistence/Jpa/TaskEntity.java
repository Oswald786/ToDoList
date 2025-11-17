package com.todolist.adaptors.persistence.Jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor

@Entity
@Table(name="TASK_LIST")
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TASK_ID")
    @Getter
    @Setter
    private Long id;

    @Column(name = "TASK_OWNER")
    @Getter
    @Setter
    private String taskOwnerId;

    @Column(name = "TASK_NAME")
    @Getter
    @Setter
    private String taskName;

    @Column(name = "TASK_TYPE")
    @Getter
    @Setter
    private String taskType;

    @Column(name = "TASK_LEVEL")
    @Getter
    @Setter
    private String taskLevel;

    @Column(name = "TASK_DESCRIPTION")
    @Getter
    @Setter
    private String taskDescription;
}