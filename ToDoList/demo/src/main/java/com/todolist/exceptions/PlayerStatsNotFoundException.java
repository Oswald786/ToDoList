package com.todolist.exceptions;

public class PlayerStatsNotFoundException extends RuntimeException{
    public PlayerStatsNotFoundException(String message) {
        super(message);
    }
}
