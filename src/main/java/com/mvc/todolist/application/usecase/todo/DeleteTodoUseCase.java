package com.mvc.todolist.application.usecase.todo;

import com.mvc.todolist.domain.port.TodoRepositoryPort;
import com.mvc.todolist.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteTodoUseCase {

    private final TodoRepositoryPort todoRepositoryPort;

    public void execute(Long id) {
        todoRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + id));

        todoRepositoryPort.deleteById(id);

    }
}
