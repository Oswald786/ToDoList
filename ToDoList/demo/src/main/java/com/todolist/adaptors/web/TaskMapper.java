package com.todolist.adaptors.web;


import com.todolist.Models.taskObjectModel;
import com.todolist.adaptors.persistence.jpa.TaskEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jsr330")
public interface TaskMapper {

    TaskEntity toEntity(taskObjectModel taskObjectModel);

    taskObjectModel toModel(TaskEntity taskEntity);
}
