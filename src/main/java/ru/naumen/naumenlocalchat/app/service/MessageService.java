package ru.naumen.naumenlocalchat.app.service;

import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import ru.naumen.naumenlocalchat.app.repository.MessageRepository;
import ru.naumen.naumenlocalchat.domain.Chat;
import ru.naumen.naumenlocalchat.domain.GroupChat;
import ru.naumen.naumenlocalchat.domain.Message;
import ru.naumen.naumenlocalchat.exception.EntityNotFoundException;
import ru.naumen.naumenlocalchat.exception.InvalidChatException;

import java.util.List;
import java.util.Map;

/**
 * Сервис сообщений
 */
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatService chatService;
    private final GroupChatService groupChatService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public MessageService(MessageRepository messageRepository,
                          ChatService chatService,
                          GroupChatService groupChatService,
                          SimpMessagingTemplate simpMessagingTemplate) {
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
    public void sendMessage(Message message, Long chatId) throws EntityNotFoundException, InvalidChatException {
        Chat chat = findChatOrGroupChatById(chatId);

        if (!chat.getMembers().contains(message.getSender())) {
            throw new InvalidChatException("Пользователь " + message.getSender().getId() + " не в чате " + chatId);
        }

        message.setChat(chat);
        Message savedMessage = messageRepository.save(message);

        simpMessagingTemplate.convertAndSend("/topic/chat/" + chatId, Map.of("type", "send", "message", savedMessage));
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

    /**
     * Удалить сообщение
     * @param messageId id сообщения
     * @param groupChatId id группового чата
     * @param userId id админа чата
     * @throws EntityNotFoundException если сообщение не найдено
     * @throws InvalidChatException если пользователь не в чате
     */
    public void deleteMessage(Long messageId, Long groupChatId, Long userId) throws EntityNotFoundException, InvalidChatException {
        if (!messageRepository.existsById(messageId)) {
            throw new EntityNotFoundException("Сообщение с id " + messageId + " не найдено");
        }

        GroupChat groupChat = groupChatService.findGroupChatById(groupChatId);

        if (!userId.equals(groupChat.getAdmin().getId())) {
            throw new InvalidChatException("Пользователь " + userId + " не админ в чате " + groupChatId);
        }

        messageRepository.deleteById(messageId);

        simpMessagingTemplate.convertAndSend("/topic/chat/" + groupChatId,
                Map.of("type", "delete", "messageId", messageId));
    }
}
