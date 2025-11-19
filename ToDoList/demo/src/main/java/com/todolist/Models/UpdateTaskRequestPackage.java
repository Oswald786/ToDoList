package com.todolist.Models;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Serdeable
@Getter
@Setter
@NoArgsConstructor
public class UpdateTaskRequestPackage {

    long id;

    String replacementValue;

    String fieldToUpdate;

    public UpdateTaskRequestPackage(long id, String replacementValue, String fieldToUpdate) {
        this.id = id;
        this.replacementValue = replacementValue;
        this.fieldToUpdate = fieldToUpdate;
    }
}
