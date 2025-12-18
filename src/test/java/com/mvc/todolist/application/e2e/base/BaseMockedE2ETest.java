package com.mvc.todolist.application.e2e.base;

import com.mvc.todolist.application.usecase.todo.*;
import com.mvc.todolist.domain.port.TodoRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest()
@ActiveProfiles("test")
@Import(BaseMockedE2ETest.MockedUseCasesConfig.class)
public abstract class BaseMockedE2ETest extends BaseE2ETest {

    @Autowired
    protected CreateTodoUseCase createTodoUseCase;

    @Autowired
    protected UpdateTodoUseCase updateTodoUseCase;

    @Autowired
    protected DeleteTodoUseCase deleteTodoUseCase;

    @Autowired
    protected GetTodoByIdUseCase getTodoByIdUseCase;

    @Autowired
    protected GetAllTodosUseCase getAllTodosUseCase;

    @BeforeEach
    @Override
    public void baseSetup() {
        super.baseSetup();

        Mockito.reset(
                createTodoUseCase, updateTodoUseCase, deleteTodoUseCase, getTodoByIdUseCase, getAllTodosUseCase
        );
    }

    @TestConfiguration
    static class MockedUseCasesConfig {

        @Bean
        @Primary
        public CreateTodoUseCase createTodoUseCase(TodoRepositoryPort todoRepositoryPort) {
            return Mockito.spy(new CreateTodoUseCase(todoRepositoryPort));
        }

        @Bean
        @Primary
        public UpdateTodoUseCase updateTodoUseCase(TodoRepositoryPort todoRepositoryPort) {
            return Mockito.spy(new UpdateTodoUseCase(todoRepositoryPort));
        }

        @Bean
        @Primary
        public DeleteTodoUseCase deleteTodoUseCase(TodoRepositoryPort todoRepositoryPort) {
            return Mockito.spy(new DeleteTodoUseCase(todoRepositoryPort));
        }

        @Bean
        @Primary
        public GetTodoByIdUseCase getTodoByIdUseCase(TodoRepositoryPort todoRepositoryPort) {
            return Mockito.spy(new GetTodoByIdUseCase(todoRepositoryPort));
        }

        @Bean
        @Primary
        public GetAllTodosUseCase getAllTodosUseCase(TodoRepositoryPort todoRepositoryPort) {
            return Mockito.spy(new GetAllTodosUseCase(todoRepositoryPort));
        }

    }
}
