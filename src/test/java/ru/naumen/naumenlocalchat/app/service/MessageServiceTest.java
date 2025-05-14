package ru.naumen.naumenlocalchat.app.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import ru.naumen.naumenlocalchat.app.repository.MessageRepository;
import ru.naumen.naumenlocalchat.domain.Chat;
import ru.naumen.naumenlocalchat.domain.GroupChat;
import ru.naumen.naumenlocalchat.domain.Message;
import ru.naumen.naumenlocalchat.domain.User;
import ru.naumen.naumenlocalchat.exception.EntityNotFoundException;

import java.util.List;
import java.util.Set;

/**
 * Тест сервиса сообщений
 */
class MessageServiceTest {

    private MessageService messageService;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatService chatService;

    @Mock
    private GroupChatService groupChatService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        messageService = new MessageService(
                messageRepository,
                chatService,
                groupChatService,
                simpMessagingTemplate
        );
    }

    /**
     * Тест отправки сообщения в обычный чат
     */
    @Test
    void testSendMessageToChat() throws EntityNotFoundException {
        User user1 = new User("user1@test.com", "pass1", "f1", "l1");
        User user2 = new User("user2@test.com", "pass2", "f2", "l2");
        Chat chat = new Chat(Set.of(user1, user2));
        Message message = new Message(user1, "message");

        Mockito.when(chatService.findChatById(1L)).thenReturn(chat);
        Mockito.when(messageRepository.save(message)).thenReturn(message);

        messageService.sendMessage(message, 1L);

        Assertions.assertEquals(chat, message.getChat());
        Mockito.verify(messageRepository).save(message);
        Mockito.verify(simpMessagingTemplate).convertAndSend("/topic/chat/1", message);
    }

    /**
     * Тест отправки сообщения в групповой чат
     */
    @Test
    void testSendMessageToGroupChat() throws EntityNotFoundException {
        User user1 = new User("user1@test.com", "pass1", "f1", "l1");
        User user2 = new User("user2@test.com", "pass2", "f2", "l2");
        User user3 = new User("user3@test.com", "pass3", "f3", "l3");
        GroupChat groupChat = new GroupChat(Set.of(user1, user2, user3), "name");
        Message message = new Message(user1, "message");

        Mockito.when(chatService.findChatById(2L)).thenThrow(new EntityNotFoundException("Not found"));
        Mockito.when(groupChatService.findGroupChatById(2L)).thenReturn(groupChat);
        Mockito.when(messageRepository.save(message)).thenReturn(message);

        messageService.sendMessage(message, 2L);

        Assertions.assertEquals(groupChat, message.getChat());
        Mockito.verify(messageRepository).save(message);
        Mockito.verify(simpMessagingTemplate).convertAndSend("/topic/chat/2", message);
    }

    /**
     * Тест ошибки при отправке сообщения в несуществующий чат
     */
    @Test
    void testSendMessageToNonExistentChat() throws EntityNotFoundException {
        User user1 = new User("user1@test.com", "pass1", "f1", "l1");
        Message message = new Message(user1, "message");

        Mockito.when(chatService.findChatById(1L)).thenThrow(new EntityNotFoundException("Not found"));
        Mockito.when(groupChatService.findGroupChatById(1L)).thenThrow(new EntityNotFoundException("Not found"));

        Exception e = Assertions.assertThrows(EntityNotFoundException.class, () -> messageService.sendMessage(message, 1L));
        Assertions.assertEquals("Не найден ни обычный, ни групповой чат с id 1", e.getMessage());
        Mockito.verify(messageRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тест поиска сообщений в чате
     */
    @Test
    void testFindChatMessages() {
        User user1 = new User("user1@test.com", "pass1", "f1", "l1");
        User user2 = new User("user2@test.com", "pass2", "f2", "l2");

        Message message1 = new Message(user1, "message1");
        Message message2 = new Message(user2, "message2");
        List<Message> expectedMessages = List.of(message1, message2);

        Mockito.when(messageRepository.findByChatIdOrderByTimestampAsc(1L)).thenReturn(expectedMessages);

        List<Message> messages = messageService.findChatMessages(1L);

        Assertions.assertEquals(expectedMessages, messages);
        Mockito.verify(messageRepository).findByChatIdOrderByTimestampAsc(1L);
    }

    /**
     * Тест поиска сообщений по текстовому запросу
     */
    @Test
    void testSearchMessagesInChat() {
        User user1 = new User("user1@test.com", "pass1", "f1", "l1");
        User user2 = new User("user2@test.com", "pass2", "f2", "l2");

        Message message1 = new Message(user1, "message1");
        Message message2 = new Message(user2, "message2");
        List<Message> expectedMessages = List.of(message1, message2);

        Mockito.when(messageRepository.findByChatIdAndContentContainingIgnoreCaseOrderByTimestampAsc(1L, "message")).thenReturn(expectedMessages);

        List<Message> actualMessages = messageService.searchMessagesInChat(1L, "message");

        Assertions.assertEquals(expectedMessages, actualMessages);
        Mockito.verify(messageRepository).findByChatIdAndContentContainingIgnoreCaseOrderByTimestampAsc(1L, "message");
    }
}