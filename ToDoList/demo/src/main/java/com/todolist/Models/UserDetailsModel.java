package com.todolist.Models;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Serdeable
public class UserDetailsModel {
    long id;

    String username;


    String password;


    String role;


    String email;




    public UserDetailsModel(String userName, String password, String role, String email) {
        this.username = userName;
        this.password = password;
        this.role = role;
        this.email = email;
    }

    public UserDetailsModel() {
    }

}
