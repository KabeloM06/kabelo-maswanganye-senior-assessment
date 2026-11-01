package com.glucode.taskmanager.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(

    @NotBlank(message = "Task title cannot be empty.")
    @Size(max = 200, message = "Task title cannot exceed 200 characters.")
    String title,

    @Size(max = 2000, message = "Task description cannot exceed 2000 characters.")
    String description

) {}