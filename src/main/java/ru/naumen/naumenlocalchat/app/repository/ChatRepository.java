package ru.naumen.naumenlocalchat.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.naumen.naumenlocalchat.domain.Chat;

/**
 * Репозиторий чатов
 */
public interface ChatRepository extends JpaRepository<Chat, Long> {
}
