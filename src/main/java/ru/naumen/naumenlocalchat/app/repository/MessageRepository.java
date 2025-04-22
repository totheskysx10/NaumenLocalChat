package ru.naumen.naumenlocalchat.app.repository;

import ru.naumen.naumenlocalchat.domain.Message;

import java.util.List;

/**
 * Репозиторий сообщений
 */
public interface MessageRepository {

    /**
     * Ищет сообщения по идентификатору чата, сортированные по времени
     * @param chatId идентификатор чата
     */
    List<Message> findByChatIdOrderByTimestampAsc(Long chatId);

    /**
     * Ищет сообщения по идентификатору чата и контенту, сортированные по времени
     * @param chatId идентификатор чата
     * @param content контент
     */
    List<Message> findByChatIdAndContentContainingIgnoreCaseOrderByTimestampAsc(Long chatId, String content);
}
