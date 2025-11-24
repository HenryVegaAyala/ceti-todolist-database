package com.mvc.todolist.application.usecase.user;

import com.mvc.todolist.domain.model.User;
import com.mvc.todolist.domain.port.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllUsersUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public List<User> execute() {
        return userRepositoryPort.findAll();
    }
}

