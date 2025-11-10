package com.todolist.adaptors.web;

import com.todolist.Models.UserDetailsModel;
import com.todolist.adaptors.persistence.Jpa.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jsr330")
public interface UserMapper {

    UserEntity toEntity(UserDetailsModel userDetailsModel);

    UserDetailsModel toModel(UserEntity userEntity);
}
