package com.todolist.exceptions;

public class PlayerUsernameNotProvided extends RuntimeException {
    public PlayerUsernameNotProvided(String message) {
        super(message);
    }
}
