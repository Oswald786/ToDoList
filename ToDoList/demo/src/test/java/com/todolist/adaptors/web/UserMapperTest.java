package com.todolist.adaptors.web;

import com.todolist.Models.TaskObjectModel;
import com.todolist.adaptors.persistence.Jpa.TaskEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {


    private final TaskMapper mapper = new TaskMapperImpl();

    @Test
    @DisplayName("toEntity should correctly map all fields from TaskObjectModel to TaskEntity")
    void toEntity_mapsAllFieldsCorrectly() {
        // Arrange
        TaskObjectModel model = new TaskObjectModel(
                1L,
                "ETHAN",
                "Organize Workspace",
                "Chore",
                "Medium",
                "Sort cables, clean monitor, and clear desk clutter."
        );

        // Act
        TaskEntity entity = mapper.toEntity(model);

        // Assert
        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals("ETHAN", entity.getTaskOwnerId());
        assertEquals("Organize Workspace", entity.getTaskName());
        assertEquals("Chore", entity.getTaskType());
        assertEquals("Medium", entity.getTaskLevel());
        assertEquals("Sort cables, clean monitor, and clear desk clutter.", entity.getTaskDescription());
    }

    @Test
    @DisplayName("toModel should correctly map all fields from TaskEntity to TaskObjectModel")
    void toModel_mapsAllFieldsCorrectly() {
        // Arrange
        TaskEntity entity = new TaskEntity();
        entity.setId(2L);
        entity.setTaskOwnerId("ETHAN");
        entity.setTaskName("Write Project Summary");
        entity.setTaskType("Work");
        entity.setTaskLevel("Hard");
        entity.setTaskDescription("Summarize weekly progress and next sprint objectives.");

        // Act
        TaskObjectModel model = mapper.toModel(entity);

        // Assert
        assertNotNull(model);
        assertEquals(2L, model.getId());
        assertEquals("ETHAN", model.getTaskOwnerId());
        assertEquals("Write Project Summary", model.getTaskName());
        assertEquals("Work", model.getTaskType());
        assertEquals("Hard", model.getTaskLevel());
        assertEquals("Summarize weekly progress and next sprint objectives.", model.getTaskDescription());
    }
}