package ru.naumen.naumenlocalchat.extern.infrastructure.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.naumen.naumenlocalchat.app.repository.UserRepository;
import ru.naumen.naumenlocalchat.domain.User;

import java.util.Objects;
import java.util.Optional;

/**
 * Сервис контекста безопасности
 */
@Component
public class SecurityContextService {

    private final UserRepository userRepository;

    public SecurityContextService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Проверяет, авторизован ли в данный момент пользователь с переданным id.
     *
     * @param userId id пользователя
     * @return true, если пользователь с userId в данный момент авторизован
     */
    public boolean isCurrentAuthId(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            Object principal = auth.getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.User) {
                String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();

                Optional<User> user = userRepository.findByEmail(username);

                if (user.isPresent()) {
                    return Objects.equals(user.get().getId(), userId);
                }
            }
        }

        return false;
    }
}
