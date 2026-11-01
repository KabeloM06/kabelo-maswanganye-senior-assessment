
package com.glucode.taskmanager.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Standard API error response.")
public record ApiError(

    @Schema(
        description = "Timestamp when the error occurred.",
        example = "2026-11-01T08:30:00Z"
    )
    Instant timestamp,

    @Schema(
        description = "HTTP status code.",
        example = "400"
    )
    int status,

    @Schema(
        description = "HTTP status description.",
        example = "Bad Request"
    )
    String error,

    @Schema(
        description = "Details explaining why the request failed.",
        example = "Task title cannot be empty."
    )
    String message

) {}
