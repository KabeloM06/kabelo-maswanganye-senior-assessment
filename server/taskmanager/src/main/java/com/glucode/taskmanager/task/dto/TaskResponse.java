package com.glucode.taskmanager.task.dto;

import com.glucode.taskmanager.task.Task;

import java.time.Instant;

public record TaskResponse(
        Long id,
        String title,
        boolean completed,
        Instant createdAt
) {

    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.isCompleted(),
                task.getCreatedAt()
        );
    }
}