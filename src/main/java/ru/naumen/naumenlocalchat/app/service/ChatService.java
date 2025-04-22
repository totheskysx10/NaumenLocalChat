package ru.naumen.naumenlocalchat.app.service;

import ru.naumen.naumenlocalchat.app.repository.ChatRepository;

/**
 * Сервис чатов
 */
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }
}
