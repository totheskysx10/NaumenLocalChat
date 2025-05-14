package ru.naumen.naumenlocalchat.app.service;

import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import ru.naumen.naumenlocalchat.app.repository.MessageRepository;
import ru.naumen.naumenlocalchat.domain.Chat;
import ru.naumen.naumenlocalchat.domain.Message;
import ru.naumen.naumenlocalchat.exception.EntityNotFoundException;

import java.util.List;

/**
 * Сервис сообщений
 */
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatService chatService;
    private final GroupChatService groupChatService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public MessageService(MessageRepository messageRepository, ChatService chatService, GroupChatService groupChatService, SimpMessagingTemplate simpMessagingTemplate) {
        this.messageRepository = messageRepository;
        this.chatService = chatService;
        this.groupChatService = groupChatService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * Отправить сообщение
     * @param message сообщение
     * @param chatId идентификатор чата
     * @throws EntityNotFoundException если не найден чат
     */
    public void sendMessage(Message message, Long chatId) throws EntityNotFoundException {
        Chat chat = findChatOrGroupChatById(chatId);

        message.setChat(chat);
        Message savedMessage = messageRepository.save(message);

        simpMessagingTemplate.convertAndSend("/topic/chat/" + chatId, savedMessage);
    }

    /**
     * Ищет сообщения в чате
     * @param chatId идентификатор чата
     */
    public List<Message> findChatMessages(Long chatId) {
        return messageRepository.findByChatIdOrderByTimestampAsc(chatId);
    }

    /**
     * Ищет сообщения в чате по текстовому запросу
     * @param chatId идентификатор чата
     * @param query запрос
     */
    public List<Message> searchMessagesInChat(Long chatId, String query) {
        return messageRepository.findByChatIdAndContentContainingIgnoreCaseOrderByTimestampAsc(chatId, query);
    }

    /**
     * Ищет обычный или групповой чат по идентификатору
     * @param chatId идентификатор
     * @throws EntityNotFoundException если не найден чат
     */
    private Chat findChatOrGroupChatById(Long chatId) throws EntityNotFoundException {
        try {
            return chatService.findChatById(chatId);
        } catch (EntityNotFoundException e) {
            try {
                return groupChatService.findGroupChatById(chatId);
            } catch (EntityNotFoundException ex) {
                throw new EntityNotFoundException("Не найден ни обычный, ни групповой чат с id " + chatId);
            }
        }
    }

}
