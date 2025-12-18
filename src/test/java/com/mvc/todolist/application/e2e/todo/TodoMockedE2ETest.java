package com.mvc.todolist.application.e2e.todo;

import com.mvc.todolist.application.e2e.base.BaseMockedE2ETest;
import com.mvc.todolist.infrastructure.dto.todo.CreateTodoRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("E2E - Todo con Mock")
public class TodoMockedE2ETest extends BaseMockedE2ETest {

    @Test
    @DisplayName("Debe verificar que el caso de uso se llama con los parámetros correctos")
    void testCreateTodoCallsUseCaseWithCorrectParameters() throws Exception {
        CreateTodoRequest request = new CreateTodoRequest();
        request.setTitle("Nueva Tarea Mock");
        request.setDescription("Descripción mock");

        mockMvc.perform(
                        post("/api/todos")
                                .header("Authorization", bearerToken(userToken))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(request))
                ).andDo(print())
                .andExpect(status().isCreated());

        verify(createTodoUseCase, times(1))
                .execute("Nueva Tarea Mock", "Descripción mock");
    }

}
