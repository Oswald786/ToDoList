package com.todolist.adaptors.web;


import com.todolist.Models.taskObjectModel;
import com.todolist.adaptors.persistence.jpa.TaskEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta")
public interface TaskMapper {

    TaskEntity toEntity(taskObjectModel taskObjectModel);

    taskObjectModel toModel(TaskEntity taskEntity);

}
