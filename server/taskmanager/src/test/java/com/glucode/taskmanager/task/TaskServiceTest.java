package com.glucode.taskmanager.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task("Complete assessment");
    }

    @Test
    void getAllTasks_returnsAllTasks() {
        // Arrange
        when(taskRepository.findAll()).thenReturn(List.of(task));

        // Act
        List<Task> result = taskService.getAllTasks();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Complete assessment", result.get(0).getTitle());

        verify(taskRepository).findAll();
    }

    @Test
    void createTask_withValidTitle_savesTask() {
        // Arrange
        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Task result = taskService.createTask("New task");

        // Assert
        assertEquals("New task", result.getTitle());
        assertFalse(result.isCompleted());

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void createTask_withNullTitle_throwsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(null)
        );

        verifyNoInteractions(taskRepository);
    }

    @Test
    void createTask_withBlankTitle_throwsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask("   ")
        );

        verifyNoInteractions(taskRepository);
    }

    @Test
    void createTask_withTitleExceeding200Characters_throwsException() {
        // Arrange
        String longTitle = "a".repeat(201);

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(longTitle)
        );

        verifyNoInteractions(taskRepository);
    }

    @Test
    void createTask_withSurroundingSpaces_trimsTitle() {
        // Arrange
        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Task result = taskService.createTask("  Buy groceries  ");

        // Assert
        assertEquals("Buy groceries", result.getTitle());

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTaskCompletion_withExistingTask_updatesCompletion() {
        // Arrange
        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Task result = taskService.updateTaskCompletion(1L, true);

        // Assert
        assertTrue(result.isCompleted());

        verify(taskRepository).findById(1L);
        verify(taskRepository).save(task);
    }

    @Test
    void updateTaskCompletion_withNonexistentTask_throwsException() {
        // Arrange
        when(taskRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                TaskNotFoundException.class,
                () -> taskService.updateTaskCompletion(999L, true)
        );

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deleteTask_withExistingTask_deletesTask() {
        // Arrange
        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        // Act
        taskService.deleteTask(1L);

        // Assert
        verify(taskRepository).findById(1L);
        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTask_withNonexistentTask_throwsException() {
        // Arrange
        when(taskRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                TaskNotFoundException.class,
                () -> taskService.deleteTask(999L)
        );

        verify(taskRepository, never()).delete(any(Task.class));
    }
}