package com.todolist.Models;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.Setter;

@Serdeable
@Getter
@Setter
public class UpdateTaskRequestPackage {

    long id;

    String fieldToUpdate;

    String replacementValue;

    public UpdateTaskRequestPackage(long id, String replacementValue, String fieldToUpdate) {
        this.id = id;
        this.replacementValue = replacementValue;
        this.fieldToUpdate = fieldToUpdate;
    }
}
