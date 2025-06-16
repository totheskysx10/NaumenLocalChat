package ru.naumen.naumenlocalchat.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.naumen.naumenlocalchat.domain.Chat;
import ru.naumen.naumenlocalchat.domain.User;

import java.util.List;
import java.util.Set;

/**
 * Репозиторий чатов
 */
public interface ChatRepository extends JpaRepository<Chat, Long> {

    /**
     * Ищет чаты пользователя
     * @param user пользователь
     */
    List<Chat> findByMembersContaining(User user);

    /**
     * Проверяет, есть ли чат с такими пользователями
     * @param members пользователи
     * @param size размер чата
     */
    @Query("SELECT COUNT(c) > 0 FROM Chat c WHERE SIZE(c.members) = :size " +
            "AND NOT EXISTS (SELECT u FROM User u WHERE u MEMBER OF c.members AND u NOT IN :members) " +
            "AND NOT EXISTS (SELECT u FROM User u WHERE u NOT MEMBER OF c.members AND u IN :members)")
    boolean existsByMembers(@Param("members") Set<User> members, @Param("size") int size);
}
