package ru.naumen.naumenlocalchat.app.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.naumen.naumenlocalchat.app.repository.ChatRepository;
import ru.naumen.naumenlocalchat.app.repository.UserRepository;
import ru.naumen.naumenlocalchat.domain.Chat;
import ru.naumen.naumenlocalchat.domain.CodeType;
import ru.naumen.naumenlocalchat.domain.User;
import ru.naumen.naumenlocalchat.exception.EntityDuplicateException;
import ru.naumen.naumenlocalchat.exception.EntityNotFoundException;
import ru.naumen.naumenlocalchat.exception.InvalidCodeException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Тест сервиса чатов
 */
class ChatServiceTest {

    private ChatService chatService;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CodeService codeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        chatService = new ChatService(chatRepository, userService, userRepository, codeService);
    }

    /**
     * Тест создания чата
     */
    @Test
    void createChat() throws EntityDuplicateException, InvalidCodeException, EntityNotFoundException {
        User user1 = new User("test@test.com", "pass", "f", "l");
        User user2 = new User("test2@test.com", "pass2", "f2", "l2");

        Mockito.when(codeService.getIdByCode(CodeType.BASIC, "12345678")).thenReturn(2L);
        Mockito.when(userService.getUserById(1L)).thenReturn(user1);
        Mockito.when(userService.getUserById(2L)).thenReturn(user2);

        chatService.createChatByInvitationCode("12345678", 1L);

        Mockito.verify(chatRepository).save(Mockito.argThat(chat -> chat.getMembers().contains(user1) && chat.getMembers().contains(user2)));
        Assertions.assertEquals(1, user1.getChats().size());
        Assertions.assertEquals(1, user2.getChats().size());
    }

    /**
     * Тест ошибки при создании дубликата чата
     */
    @Test
    void createChatDuplicateUsers() throws InvalidCodeException, EntityNotFoundException {
        User user1 = new User("test@test.com", "pass", "f", "l");
        User user2 = new User("test2@test.com", "pass2", "f2", "l2");
        Set<User> users = Set.of(user1, user2);
        Mockito.when(chatRepository.existsByMembers(users, 2)).thenReturn(true);

        Mockito.when(codeService.getIdByCode(CodeType.BASIC, "12345678")).thenReturn(2L);
        Mockito.when(userService.getUserById(1L)).thenReturn(user1);
        Mockito.when(userService.getUserById(2L)).thenReturn(user2);

        Exception e = Assertions.assertThrows(EntityDuplicateException.class, () -> chatService.createChatByInvitationCode("12345678", 1L));
        Assertions.assertEquals("Чат с такими участниками уже существует!", e.getMessage());
        Mockito.verify(chatRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тест поиска чата по id
     */
    @Test
    void testFindChatById() throws EntityNotFoundException {
        Chat expectedChat = new Chat();
        Mockito.when(chatRepository.findById(1L)).thenReturn(Optional.of(expectedChat));

        Chat chat = chatService.findChatById(1L);

        Assertions.assertEquals(expectedChat, chat);
    }

    /**
     * Тест ошибки при поиске несуществующего чата
     */
    @Test
    void testFindChatByIdNotFound() {
        Mockito.when(chatRepository.findById(1L)).thenReturn(Optional.empty());

        Exception e = Assertions.assertThrows(EntityNotFoundException.class, () -> chatService.findChatById(1L));
        Assertions.assertEquals("Не найден чат с id 1", e.getMessage());
    }

    /**
     * Тест поиска чатов пользователя
     */
    @Test
    void testFindUserChats() throws EntityNotFoundException {
        User user = new User("test@test.com", "pass", "f", "l");
        List<Chat> expectedChats = List.of(new Chat(), new Chat());

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(chatRepository.findByMembersContaining(user)).thenReturn(expectedChats);

        List<Chat> chats = chatService.findUserChats(1L);

        Assertions.assertEquals(expectedChats, chats);
    }

    /**
     * Тест ошибки при поиске чатов несуществующего пользователя
     */
    @Test
    void testFindUserChatsUserNotFound() throws EntityNotFoundException {
        Mockito.when(userService.getUserById(1L)).thenThrow(new EntityNotFoundException("Пользователь не найден"));

        Exception e = Assertions.assertThrows(EntityNotFoundException.class, () -> chatService.findUserChats(1L));
        Assertions.assertEquals("Пользователь не найден", e.getMessage());
    }

    /**
     * Тест удаления чата по id
     */
    @Test
    void testDeleteChatById() throws EntityNotFoundException {
        User user1 = new User("test@test.com", "pass", "f", "l");
        User user2 = new User("test2@test.com", "pass2", "f2", "l2");
        Chat chat = new Chat(Set.of(user1, user2));

        Mockito.when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

        chatService.deleteChatById(1L);

        Assertions.assertFalse(user1.getChats().contains(chat));
        Assertions.assertFalse(user2.getChats().contains(chat));
        Mockito.verify(chatRepository).delete(chat);
    }
}