package ru.naumen.naumenlocalchat.app.service;

import ru.naumen.naumenlocalchat.app.repository.UserRepository;

/**
 * Сервис пользователей
 */
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
