package com.todolist.ExceptionHandling;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Serdeable
public class ExceptionMessageModel {

    String message;
    String error;
    String timestamp;
    String path;

    public ExceptionMessageModel(String message, String error, String timestamp, String path) {
        this.message = message;
        this.error = error;
        this.timestamp = timestamp;
        this.path = path;
    }

    public ExceptionMessageModel() {
    }

    @Override
    public String toString() {
        return "ExceptionMessageModel{" +
                "message='" + message + '\'' +
                ", error='" + error + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
