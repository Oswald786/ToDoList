package com.todolist.Models;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Serdeable
public class UserDetailsModel {
    long id;

    String userName;


    String password;


    String role;


    String EMAIL;




    public UserDetailsModel(String userName, String password, String role, String EMAIL) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.EMAIL = EMAIL;
    }

    public UserDetailsModel() {
    }

}
