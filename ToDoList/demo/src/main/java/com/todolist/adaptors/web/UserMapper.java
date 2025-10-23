package com.todolist.adaptors.web;

import com.todolist.Models.userDetailsModel;
import com.todolist.adaptors.persistence.jpa.userEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta")
public interface UserMapper {

    userEntity toEntity(userDetailsModel userDetailsModel);

    userDetailsModel toModel(userEntity userEntity);
}
