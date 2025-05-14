package ru.naumen.naumenlocalchat.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.naumen.naumenlocalchat.domain.User;

import java.util.Optional;

/**
 * Репозиторий пользователей
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Проверяет существование пользователя по email
     * @param email адрес почты
     */
    boolean existsByEmail(String email);

    /**
     * Ищет пользователей по email
     * @param email адрес почты
     */
    Optional<User> findByEmail(String email);
}
