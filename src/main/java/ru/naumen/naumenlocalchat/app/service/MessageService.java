package ru.naumen.naumenlocalchat.app.service;

import org.springframework.stereotype.Service;
import ru.naumen.naumenlocalchat.app.repository.MessageRepository;

/**
 * Сервис сообщений
 */
@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
}
