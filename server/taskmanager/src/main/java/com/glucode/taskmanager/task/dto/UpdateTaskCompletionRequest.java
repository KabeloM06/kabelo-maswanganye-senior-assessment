
package com.glucode.taskmanager.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for updating a task's completion status.")
public record UpdateTaskCompletionRequest(

    @Schema(
        description = "Whether the task has been completed.",
        example = "true",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    boolean completed

) {}
