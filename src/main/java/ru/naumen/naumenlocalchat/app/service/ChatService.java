package ru.naumen.naumenlocalchat.app.service;

import org.springframework.stereotype.Service;
import ru.naumen.naumenlocalchat.app.repository.ChatRepository;

/**
 * Сервис чатов
 */
@Service
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }
}
