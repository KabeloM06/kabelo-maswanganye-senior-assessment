package com.glucode.taskmanager.task;

import com.glucode.taskmanager.task.dto.CreateTaskRequest;
import com.glucode.taskmanager.task.dto.TaskResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskResponse> getAllTasks() {
        return taskService.getAllTasks()
                .stream()
                .map(TaskResponse::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            @Valid @RequestBody CreateTaskRequest request
    ) {
        Task task = taskService.createTask(request.title());
        return TaskResponse.from(task);
    }

    @PatchMapping("/{id}/completion")
    public TaskResponse updateTaskCompletion(
            @PathVariable Long id,
            @RequestParam boolean completed
    ) {
        Task task = taskService.updateTaskCompletion(id, completed);
        return TaskResponse.from(task);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}