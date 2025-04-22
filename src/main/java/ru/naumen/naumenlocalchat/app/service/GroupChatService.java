package ru.naumen.naumenlocalchat.app.service;

import ru.naumen.naumenlocalchat.app.repository.GroupChatRepository;

/**
 * Сервис групповых чатов
 */
public class GroupChatService {

    private final GroupChatRepository groupChatRepository;

    public GroupChatService(GroupChatRepository groupChatRepository) {
        this.groupChatRepository = groupChatRepository;
    }
}
