package com.mvc.todolist.application.usecase.user;

import com.mvc.todolist.domain.model.User;
import com.mvc.todolist.domain.port.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetUserByIdUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public Optional<User> execute(Long id) {
        return userRepositoryPort.findById(id);
    }
}

