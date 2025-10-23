package com.todolist.Models;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class updateTaskRequestPackage {

    long id;

    String fieldToUpdate;

    String replacementValue;

    public updateTaskRequestPackage(long id, String replacementValue, String fieldToUpdate) {
        this.id = id;
        this.replacementValue = replacementValue;
        this.fieldToUpdate = fieldToUpdate;
    }

    public updateTaskRequestPackage() {
    }

    public String getFieldToUpdate() {
        return fieldToUpdate;
    }

    public void setFieldToUpdate(String fieldToUpdate) {
        this.fieldToUpdate = fieldToUpdate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReplacementValue() {
        return replacementValue;
    }

    public void setReplacementValue(String replacementValue) {
        this.replacementValue = replacementValue;
    }
}
