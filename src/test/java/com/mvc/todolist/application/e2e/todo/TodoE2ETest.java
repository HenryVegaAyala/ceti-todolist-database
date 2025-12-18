package com.mvc.todolist.application.e2e.todo;

import com.mvc.todolist.application.e2e.base.BaseTodoE2ETest;
import com.mvc.todolist.infrastructure.dto.todo.CreateTodoRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("E2E - Todo")
public class TodoE2ETest extends BaseTodoE2ETest {

    @AfterEach
    void tearDown() {
        cleanupTestTodos();
    }

    @Test
    @DisplayName("Debe crear una nueva tarea exitosamente")
    void testCreateTodoSuccess() throws Exception {
        CreateTodoRequest request = new CreateTodoRequest();
        request.setTitle("Nueva Tarea E2E");
        request.setDescription("Descripción de la nueva Tarea E2E");

        mockMvc.perform(
                        post("/api/todos")
                                .header("Authorization", bearerToken(userToken))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request))
                ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Nueva Tarea E2E"))
                .andExpect(jsonPath("$.description").value("Descripción de la nueva Tarea E2E"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }
}
