package ru.naumen.naumenlocalchat.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.naumen.naumenlocalchat.domain.GroupChat;
import ru.naumen.naumenlocalchat.domain.User;

import java.util.List;

/**
 * Репозиторий групповых чатов
 */
public interface GroupChatRepository extends JpaRepository<GroupChat, Long> {

    /**
     * Ищет групповые чаты по части названия и участнику
     * @param name часть названия
     * @param user участник
     */
    List<GroupChat> findByNameContainingIgnoreCaseAndMembersContaining(String name, User user);
}
