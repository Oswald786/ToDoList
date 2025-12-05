package com.todolist.exceptions;

public class PlayerStatsNotFound extends RuntimeException{
    public PlayerStatsNotFound(String message) {
        super(message);
    }
}
