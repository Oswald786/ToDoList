package com.todolist.Models;

import lombok.Getter;
import lombok.Setter;

public class userDetailsModel {
    @Getter @Setter
    String username;

    @Getter @Setter
    String password;

    @Getter @Setter
    String role;

    @Getter @Setter
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
