package ru.naumen.naumenlocalchat.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.naumen.naumenlocalchat.domain.Message;

import java.util.List;

/**
 * Репозиторий сообщений
 */
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Ищет сообщения по идентификатору чата, сортированные по времени
     * @param chatId идентификатор чата
     */
    List<Message> findByChatIdOrderByTimestampAsc(Long chatId);

    /**
     * Ищет сообщения по идентификатору чата и текстовому запросу, сортированные по времени
     * @param chatId идентификатор чата
     * @param query запрос
     */
    List<Message> findByChatIdAndContentContainingIgnoreCaseOrderByTimestampAsc(Long chatId, String query);
}
