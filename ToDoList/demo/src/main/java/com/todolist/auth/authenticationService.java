package com.todolist.auth;

import com.todolist.adaptors.persistence.jpa.userEntity;

public class authenticationService {

//    Register → hash password → save user.
//
//    Login → verify password → generate JWT.
//
//    Protected route → check isAuthenticated → allow or deny.


    PasswordHasher passwordHasher = new PasswordHasher();


    public String register(String username, String password,String email){
        //hash password
        String hashedPassword = passwordHasher.hashPassword(password);

        //save user
        userEntity user = new userEntity();
        user.setUserName(username);
        user.setPassword(hashedPassword);
        user.setRole("user");
        user.setEMAIL(email);

        //confirmation

        //generate JWT

        //return JWT

        //confirmation
        return hashedPassword;
    }

    public boolean login(String username, String password){
        String hashedPassword = passwordHasher.hashPassword(password);
        return passwordHasher.checkPassword(password, hashedPassword);
    }
}
