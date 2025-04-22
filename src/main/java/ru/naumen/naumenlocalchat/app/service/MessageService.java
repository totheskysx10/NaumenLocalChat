package ru.naumen.naumenlocalchat.app.service;

import ru.naumen.naumenlocalchat.app.repository.MessageRepository;

/**
 * Сервис сообщений
 */
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
}
