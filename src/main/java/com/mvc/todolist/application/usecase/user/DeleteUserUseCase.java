package com.mvc.todolist.application.usecase.user;

import com.mvc.todolist.domain.port.UserRepositoryPort;
import com.mvc.todolist.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteUserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public void execute(Long id) {
        if (!userRepositoryPort.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con id: " + id);
        }
        userRepositoryPort.deleteById(id);
    }
}

