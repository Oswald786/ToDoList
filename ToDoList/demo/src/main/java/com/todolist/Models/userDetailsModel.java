package com.todolist.Models;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class userDetailsModel {

    String username;


    String password;


    String role;


    String email;




    public userDetailsModel(String username, String password, String role, String email) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
    }

    public userDetailsModel() {
    }

}
