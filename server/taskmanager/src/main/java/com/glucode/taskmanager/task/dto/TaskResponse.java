
package com.glucode.taskmanager.task.dto;

import com.glucode.taskmanager.task.Task;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Task information returned by the API.")
public record TaskResponse(

    @Schema(
        description = "Unique identifier of the task.",
        example = "1"
    )
    Long id,

    @Schema(
        description = "Title of the task.",
        example = "Complete assessment"
    )
    String title,

    @Schema(
        description = "Optional description of the task.",
        example = "Finish the backend implementation and write tests.",
        nullable = true
    )
    String description,

    @Schema(
        description = "Whether the task has been completed.",
        example = "false"
    )
    boolean completed,

    @Schema(
        description = "Timestamp indicating when the task was created.",
        example = "2026-11-01T08:30:00Z"
    )
    Instant createdAt

) {
    public static TaskResponse from(Task task) {
        return new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.isCompleted(),
            task.getCreatedAt()
        );
    }
}
