package com.glucode.taskmanager.task;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

public Task createTask(String title, String description) {
    if (title == null || title.isBlank()) {
        throw new IllegalArgumentException("Task title cannot be empty.");
    }

    String trimmedTitle = title.trim();

    if (trimmedTitle.length() > 200) {
        throw new IllegalArgumentException(
            "Task title cannot exceed 200 characters."
        );
    }

    if (description != null && description.length() > 2000) {
        throw new IllegalArgumentException(
            "Task description cannot exceed 2000 characters."
        );
    }

    Task task = new Task(trimmedTitle, description);
    return taskRepository.save(task);
}

    public Task updateTaskCompletion(Long id, boolean completed) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        task.setCompleted(completed);

        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        taskRepository.delete(task);
    }
}