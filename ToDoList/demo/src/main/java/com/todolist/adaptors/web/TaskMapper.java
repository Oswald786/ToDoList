package com.todolist.adaptors.web;


import com.todolist.Models.TaskObjectModel;
import com.todolist.adaptors.persistence.Jpa.TaskEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jsr330")
public interface TaskMapper {

    TaskEntity toEntity(TaskObjectModel taskObjectModel);

    TaskObjectModel toModel(TaskEntity taskEntity);
}
