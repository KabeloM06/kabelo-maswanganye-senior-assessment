
package com.glucode.taskmanager.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for creating a task.")
public record CreateTaskRequest(

    @Schema(
        description = "Title of the task. Required.",
        example = "Complete assessment",
        maxLength = 200,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Task title cannot be empty.")
    @Size(max = 200, message = "Task title cannot exceed 200 characters.")
    String title,

    @Schema(
        description = "Optional description providing additional task details.",
        example = "Finish the backend implementation and write tests.",
        maxLength = 2000,
        nullable = true
    )
    @Size(max = 2000, message = "Task description cannot exceed 2000 characters.")
    String description

) {}
