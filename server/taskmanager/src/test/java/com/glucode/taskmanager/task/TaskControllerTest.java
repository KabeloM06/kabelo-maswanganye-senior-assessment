
package com.glucode.taskmanager.task;

import com.glucode.taskmanager.exception.GlobalExceptionHandler;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@Import(GlobalExceptionHandler.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    void getAllTasks_returnsTasks() throws Exception {
        Task task = new Task(
            "Complete assessment",
            "Finish the technical assessment."
        );

        when(taskService.getAllTasks())
            .thenReturn(List.of(task));

        mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title")
                .value("Complete assessment"))
            .andExpect(jsonPath("$[0].description")
                .value("Finish the technical assessment."))
            .andExpect(jsonPath("$[0].completed")
                .value(false));

        verify(taskService).getAllTasks();
    }

    @Test
    void createTask_withValidRequest_returnsCreated() throws Exception {
        Task task = new Task(
            "Buy groceries",
            "Buy groceries for the week."
        );

        when(taskService.createTask(
            "Buy groceries",
            "Buy groceries for the week."
        )).thenReturn(task);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "Buy groceries",
                        "description": "Buy groceries for the week."
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title")
                .value("Buy groceries"))
            .andExpect(jsonPath("$.description")
                .value("Buy groceries for the week."))
            .andExpect(jsonPath("$.completed")
                .value(false));

        verify(taskService).createTask(
            "Buy groceries",
            "Buy groceries for the week."
        );
    }

    @Test
    void createTask_withBlankTitle_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "   ",
                        "description": "Some description."
                    }
                    """))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(taskService);
    }

    @Test
    void createTask_withTitleExceeding200Characters_returnsBadRequest()
            throws Exception {

        String longTitle = "a".repeat(201);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "%s",
                        "description": "Some description."
                    }
                    """.formatted(longTitle)))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(taskService);
    }

    @Test
    void createTask_withDescriptionExceeding2000Characters_returnsBadRequest()
            throws Exception {

        String longDescription = "a".repeat(2001);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "Buy groceries",
                        "description": "%s"
                    }
                    """.formatted(longDescription)))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(taskService);
    }

    @Test
    void createTask_withoutDescription_returnsCreated() throws Exception {
        Task task = new Task("Buy groceries", null);

        when(taskService.createTask("Buy groceries", null))
            .thenReturn(task);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "Buy groceries"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title")
                .value("Buy groceries"))
            .andExpect(jsonPath("$.description")
                .doesNotExist());

        verify(taskService).createTask("Buy groceries", null);
    }

    @Test
    void updateTaskCompletion_withExistingTask_returnsUpdatedTask()
            throws Exception {

        Task task = new Task(
            "Complete assessment",
            "Finish the technical assessment."
        );
        task.setCompleted(true);

        when(taskService.updateTaskCompletion(1L, true))
            .thenReturn(task);

        mockMvc.perform(patch("/api/tasks/1/completion")
                .param("completed", "true"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.completed").value(true));

        verify(taskService).updateTaskCompletion(1L, true);
    }

    @Test
    void updateTaskCompletion_withNonexistentTask_returnsNotFound()
            throws Exception {

        when(taskService.updateTaskCompletion(999L, true))
            .thenThrow(new TaskNotFoundException(999L));

        mockMvc.perform(patch("/api/tasks/999/completion")
                .param("completed", "true"))
            .andExpect(status().isNotFound());

        verify(taskService).updateTaskCompletion(999L, true);
    }

    @Test
    void deleteTask_withExistingTask_returnsNoContent()
            throws Exception {

        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1"))
            .andExpect(status().isNoContent());

        verify(taskService).deleteTask(1L);
    }

    @Test
    void deleteTask_withNonexistentTask_returnsNotFound()
            throws Exception {

        doThrow(new TaskNotFoundException(999L))
            .when(taskService).deleteTask(999L);

        mockMvc.perform(delete("/api/tasks/999"))
            .andExpect(status().isNotFound());

        verify(taskService).deleteTask(999L);
    }
}
