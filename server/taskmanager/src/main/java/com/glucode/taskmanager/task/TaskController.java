
package com.glucode.taskmanager.task;

import com.glucode.taskmanager.task.dto.CreateTaskRequest;
import com.glucode.taskmanager.task.dto.TaskResponse;
import com.glucode.taskmanager.task.dto.UpdateTaskCompletionRequest;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(
    name = "Tasks",
    description = "Operations for creating and managing tasks."
)
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(
        summary = "Get all tasks",
        description = "Retrieves all tasks, including their descriptions and completion status."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Tasks retrieved successfully.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TaskResponse.class)
            )
        )
    })
    @GetMapping
    public List<TaskResponse> getAllTasks() {
        return taskService.getAllTasks()
            .stream()
            .map(TaskResponse::from)
            .toList();
    }

    @Operation(
        summary = "Create a task",
        description = "Creates a new task. The title is required; the description is optional."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Task created successfully.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TaskResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request. The title is blank or exceeds 200 characters, or the description exceeds 2000 characters.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.glucode.taskmanager.exception.ApiError.class)
            )
        )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
        @Valid @RequestBody CreateTaskRequest request
    ) {
        Task task = taskService.createTask(
            request.title(),
            request.description()
        );

        return TaskResponse.from(task);
    }

    @Operation(
        summary = "Update task completion",
        description = "Changes whether a task is marked as completed."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Task completion status updated successfully.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TaskResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No task exists with the supplied ID.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.glucode.taskmanager.exception.ApiError.class)
            )
        )
    })
    @PatchMapping("/{id}/completion")
    public TaskResponse updateTaskCompletion(
        @Parameter(
            description = "ID of the task to update.",
            example = "1",
            required = true
        )
        @PathVariable Long id,
        @Valid @RequestBody UpdateTaskCompletionRequest request
    ) {
        Task task = taskService.updateTaskCompletion(
            id,
            request.completed()
        );

        return TaskResponse.from(task);
    }

    @Operation(
        summary = "Delete a task",
        description = "Permanently deletes a task by its ID."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Task deleted successfully."
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No task exists with the supplied ID.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.glucode.taskmanager.exception.ApiError.class)
            )
        )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(
        @Parameter(
            description = "ID of the task to delete.",
            example = "1",
            required = true
        )
        @PathVariable Long id
    ) {
        taskService.deleteTask(id);
    }
}
