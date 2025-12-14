package com.todolist.exceptions;

public class PlayerUsernameNotProvidedException extends RuntimeException {
    public PlayerUsernameNotProvidedException(String message) {
        super(message);
    }
}
