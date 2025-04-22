package ru.naumen.naumenlocalchat.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.naumen.naumenlocalchat.domain.Chat;
import ru.naumen.naumenlocalchat.domain.User;

import java.util.List;

/**
 * Репозиторий чатов
 */
public interface ChatRepository extends JpaRepository<Chat, Long> {

    /**
     * Ищет чаты конкретного пользователя
     * @param user пользователя
     */
    List<Chat> findByMembersContaining(User user);
}
