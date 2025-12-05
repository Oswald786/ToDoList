package com.todolist.exceptions;

public class MapperFailedException extends RuntimeException{
    public MapperFailedException(String message) {
        super(message);
    }
}
