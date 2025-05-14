package ru.naumen.naumenlocalchat.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.naumen.naumenlocalchat.app.repository.ChatRepository;
import ru.naumen.naumenlocalchat.app.repository.UserRepository;
import ru.naumen.naumenlocalchat.domain.Chat;
import ru.naumen.naumenlocalchat.domain.User;
import ru.naumen.naumenlocalchat.exception.EntityDuplicateException;
import ru.naumen.naumenlocalchat.exception.EntityNotFoundException;
import ru.naumen.naumenlocalchat.exception.InvalidChatException;

import java.util.List;
import java.util.Optional;

/**
 * Сервис чатов
 */
@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final Logger log = LoggerFactory.getLogger(ChatService.class);

    /**
     * Количество участников не группового чата
     */
    private static final Integer CHAT_SIZE = 2;

    public ChatService(ChatRepository chatRepository, UserService userService, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * Создаёт новый чат
     * @param chat чат
     * @throws EntityDuplicateException если чат с такими участниками уже существует
     * @throws InvalidChatException если участников не двое
     */
    public void createChat(Chat chat) throws EntityDuplicateException, InvalidChatException {
        if (CHAT_SIZE != chat.getMembers().size()) {
            throw new InvalidChatException("Количество участников должно быть 2!");
        }

        if (chatRepository.existsByMembers(chat.getMembers(), CHAT_SIZE)) {
            throw new EntityDuplicateException("Чат с такими участниками уже существует!");
        }

        chat.getMembers().forEach(member -> {
            member.getChats().add(chat);
            userRepository.save(member);
        });

        chatRepository.save(chat);
        log.info("Создан чат с id {}", chat.getId());
    }

    /**
     * Ищет чат по Id
     * @param id идентификатор
     * @throws EntityNotFoundException если чат не найден
     */
    public Chat findChatById(Long id) throws EntityNotFoundException {
        Optional<Chat> chats = chatRepository.findById(id);
        return chats.orElseThrow(() -> new EntityNotFoundException("Не найден чат с id " + id));
    }

    /**
     * Ищет чаты пользователя
     * @param userId идентификатор пользователя
     */
    public List<Chat> findUserChats(Long userId) throws EntityNotFoundException {
        User user = userService.getUserById(userId);
        return chatRepository.findByMembersContaining(user);
    }

    /**
     * Удаляет чат по Id
     * @param id идентификатор
     * @throws EntityNotFoundException если чат не найден
     */
    public void deleteChatById(Long id) throws EntityNotFoundException {
        Chat chat = findChatById(id);

        chat.getMembers().forEach(member -> {
            member.getChats().remove(chat);
        });

        chatRepository.delete(chat);
        log.info("Удалён чат с id {}", chat.getId());
    }
}
