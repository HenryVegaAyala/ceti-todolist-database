package com.mvc.todolist.application.unit.usecase.todo;

import com.mvc.todolist.application.usecase.todo.CreateTodoUseCase;
import com.mvc.todolist.domain.model.Todo;
import com.mvc.todolist.domain.port.TodoRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit - CreateTodoUseCase")
public class CreateTodoUseCaseTest {

    @Mock
    private TodoRepositoryPort todoRepositoryPort;

    @InjectMocks
    private CreateTodoUseCase createTodoUseCase;

    private String validTitle;
    private String validDescription;

    @BeforeEach
    void setUp() {
        validTitle = "Test Todo Title";
        validDescription = "Test Todo Description";
    }

    @Test
    @DisplayName("Debe crear un tarea exitosamente con título y descripción válidos")
    void TestShouldCreateTodoSuccessfully() {
        // Given
        Todo expectedTodo = Todo.builder()
                .id(1L)
                .title(validTitle)
                .description(validDescription)
                .completed(false)
                .build();

        when(todoRepositoryPort.save(any(Todo.class))).thenReturn(expectedTodo);

        // When
        Todo result = createTodoUseCase.execute(validTitle, validDescription);

        // Then
        assertNotNull(result, "El todo creado no debe ser null");
        assertEquals(1L, result.getId(), "El ID debe ser 1");
        assertEquals(validTitle, result.getTitle(), "El título debe coincidir");
        assertEquals(validDescription, result.getDescription(), "El descripcion debe coincidir");
        assertFalse(result.isCompleted(), "El task debe ser false por que esta marcado como no completado");

        verify(todoRepositoryPort, times(1)).save(any(Todo.class));
    }

    @Test
    @DisplayName("Debe crear un tarea con completed=false por defecto")
    void TestShouldTodoWithCompleteFalseByDefault() {
        // Given
        Todo savedTodo = Todo.builder()
                .id(2L)
                .title(validTitle)
                .description(validDescription)
                .completed(false)
                .build();

        when(todoRepositoryPort.save(any(Todo.class))).thenReturn(savedTodo);

        // When
        Todo result = createTodoUseCase.execute(validTitle, validDescription);

        //Then
        assertFalse(result.isCompleted(), "El task debe ser false por defecto");

        verify(todoRepositoryPort).save(argThat(todo -> !todo.isCompleted()));
    }

}
