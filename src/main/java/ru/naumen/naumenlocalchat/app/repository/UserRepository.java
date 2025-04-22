package ru.naumen.naumenlocalchat.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.naumen.naumenlocalchat.domain.User;

import java.util.Optional;

/**
 * Репозиторий пользователей
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Ищет пользователей по никнейму
     * @param username никнейм
     */
    Optional<User> findByUsername(String username);
}
