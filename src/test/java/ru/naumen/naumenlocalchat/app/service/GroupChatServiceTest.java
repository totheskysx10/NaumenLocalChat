package ru.naumen.naumenlocalchat.app.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.naumen.naumenlocalchat.app.repository.GroupChatRepository;
import ru.naumen.naumenlocalchat.app.repository.UserRepository;
import ru.naumen.naumenlocalchat.domain.CodeType;
import ru.naumen.naumenlocalchat.domain.GroupChat;
import ru.naumen.naumenlocalchat.domain.User;
import ru.naumen.naumenlocalchat.exception.*;

import java.util.*;

/**
 * Тест сервиса групповых чатов
 */
class GroupChatServiceTest {

    private GroupChatService groupChatService;

    @Mock
    private GroupChatRepository groupChatRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CodeService codeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        groupChatService = new GroupChatService(groupChatRepository, userService, userRepository, codeService);
    }

    /**
     * Тест создания группового чата
     */
    @Test
    void testCreateGroupChat() throws InvalidChatException, EntityNotFoundException {
        User user1 = new User("user1@test.com", "pass1", "f1", "l1");
        User user2 = new User("user2@test.com", "pass2", "f2", "l2");
        User user3 = new User("user3@test.com", "pass3", "f3", "l3");
        GroupChat groupChat = new GroupChat(Set.of(user1, user2, user3), "name");

        groupChatService.createGroupChat(groupChat, 1L);

        Mockito.verify(groupChatRepository).save(groupChat);
        Assertions.assertTrue(user1.getChats().contains(groupChat));
        Assertions.assertTrue(user2.getChats().contains(groupChat));
        Assertions.assertTrue(user3.getChats().contains(groupChat));
    }

    /**
     * Тест ошибки при создании группового чата с менее чем 3 участниками
     */
    @Test
    void testCreateGroupChatWithLessThanThreeMembers() {
        User user1 = new User("user1@test.com", "pass1", "f1", "l1");
        User user2 = new User("user2@test.com", "pass2", "f2", "l2");
        GroupChat groupChat = new GroupChat(Set.of(user1, user2), "name");

        Exception e = Assertions.assertThrows(InvalidChatException.class, () -> groupChatService.createGroupChat(groupChat, 1L));
        Assertions.assertEquals("Количество участников должно быть минимум 3!", e.getMessage());
        Mockito.verify(groupChatRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тест добавления пользователя в групповой чат по коду приглашения
     */
    @Test
    void testInviteUserToChatByInvitationCode() throws EntityNotFoundException, InvalidCodeException, EntityDuplicateException {
        User user1 = new User("user1@test.com", "pass1", "f1", "l1");
        User user2 = new User("user2@test.com", "pass2", "f2", "l2");
        User user3 = new User("user3@test.com", "pass3", "f3", "l3");
        User user4 = new User("user4@test.com", "pass4", "f4", "l4");
        GroupChat groupChat = new GroupChat(new HashSet<>(Set.of(user1, user2, user3)), "name");

        Mockito.when(codeService.getIdByCode(CodeType.GROUP, "12345678")).thenReturn(1L);
        Mockito.when(groupChatRepository.findById(1L)).thenReturn(Optional.of(groupChat));
        Mockito.when(userService.getUserById(4L)).thenReturn(user4);

        groupChatService.inviteUserToChatByInvitationCode("12345678", 4L);

        Assertions.assertTrue(groupChat.getMembers().contains(user4));
        Assertions.assertTrue(user4.getChats().contains(groupChat));
    }

    /**
     * Тест добавления пользователя в групповой чат по коду приглашения, если пользователь уже в чате
     */
    @Test
    void testInviteUserToChatByInvitationCodeIfUserInChat() throws EntityNotFoundException, InvalidCodeException {
        User user1 = new User("user1@test.com", "pass1", "f1", "l1");
        User user2 = new User("user2@test.com", "pass2", "f2", "l2");
        User user3 = new User("user3@test.com", "pass3", "f3", "l3");
        GroupChat groupChat = new GroupChat(new HashSet<>(Set.of(user1, user2, user3)), "name");

        Mockito.when(codeService.getIdByCode(CodeType.GROUP, "12345678")).thenReturn(1L);
        Mockito.when(groupChatRepository.findById(1L)).thenReturn(Optional.of(groupChat));
        Mockito.when(userService.getUserById(3L)).thenReturn(user3);

        Exception e = Assertions.assertThrows(EntityDuplicateException.class,
                () -> groupChatService.inviteUserToChatByInvitationCode("12345678", 3L));

        Assertions.assertEquals("Пользователь с id 3 уже есть в чате с id 1", e.getMessage());
    }

    /**
     * Тест поиска группового чата по id
     */
    @Test
    void testFindGroupChatById() throws EntityNotFoundException {
        GroupChat expectedChat = new GroupChat();
        Mockito.when(groupChatRepository.findById(1L)).thenReturn(Optional.of(expectedChat));

        GroupChat chat = groupChatService.findGroupChatById(1L);

        Assertions.assertEquals(expectedChat, chat);
    }

    /**
     * Тест ошибки при поиске несуществующего группового чата
     */
    @Test
    void testFindGroupChatByIdNotFound() {
        Mockito.when(groupChatRepository.findById(1L)).thenReturn(Optional.empty());

        Exception e = Assertions.assertThrows(EntityNotFoundException.class, () -> groupChatService.findGroupChatById(1L));
        Assertions.assertEquals("Не найден групповой чат с id 1", e.getMessage());
    }

    /**
     * Тест поиска групповых чатов пользователя по названию
     */
    @Test
    void testFindUserGroupChatsByNameContaining() throws EntityNotFoundException {
        User user = new User("user@test.com", "pass", "f", "l");
        List<GroupChat> expectedChats = List.of(new GroupChat(), new GroupChat());

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(groupChatRepository.findByNameContainingIgnoreCaseAndMembersContaining("name", user)).thenReturn(expectedChats);

        List<GroupChat> chats = groupChatService.findUserGroupChatsByNameContaining(1L, "name");

        Assertions.assertEquals(expectedChats, chats);
    }

    /**
     * Тест поиска всех групповых чатов пользователя
     */
    @Test
    void testFindUserGroupChats() throws EntityNotFoundException {
        User user = new User("user@test.com", "pass", "f", "l");
        List<GroupChat> expectedChats = List.of(new GroupChat(), new GroupChat());

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(groupChatRepository.findByMembersContaining(user)).thenReturn(expectedChats);

        List<GroupChat> chats = groupChatService.findUserGroupChats(1L);

        Assertions.assertEquals(expectedChats, chats);
    }

    /**
     * Тест удаления группового чата
     */
    @Test
    void testDeleteGroupChatById() throws EntityNotFoundException {
        User user1 = new User("user1@test.com", "pass1", "f1", "l1");
        User user2 = new User("user2@test.com", "pass2", "f2", "l2");
        GroupChat groupChat = new GroupChat(Set.of(user1, user2), "name");

        Mockito.when(groupChatRepository.findById(1L)).thenReturn(Optional.of(groupChat));

        groupChatService.deleteGroupChatById(1L);

        Assertions.assertFalse(user1.getChats().contains(groupChat));
        Assertions.assertFalse(user2.getChats().contains(groupChat));
        Mockito.verify(groupChatRepository).delete(groupChat);
    }

    /**
     * Тест блокировки пользователя в групповом чате
     */
    @Test
    void testBlockUser() throws EntityNotFoundException, BlacklistException {
        User user1 = new User("user1@test.com", "pass1", "f1", "l1");
        User user2 = new User("user2@test.com", "pass2", "f2", "l2");
        User user3 = new User("user3@test.com", "pass3", "f3", "l3");
        GroupChat groupChat = new GroupChat(new HashSet<>(Set.of(user1, user2, user3)), "name");

        Mockito.when(groupChatRepository.findById(1L)).thenReturn(Optional.of(groupChat));
        Mockito.when(userService.getUserById(1L)).thenReturn(user1);

        groupChatService.blockUser(1L, 1L);

        Assertions.assertTrue(groupChat.getChatBlackList().contains(user1));
        Assertions.assertFalse(groupChat.getMembers().contains(user1));
        Mockito.verify(groupChatRepository).save(groupChat);
    }

    /**
     * Тест ошибки при блокировке пользователя, которого нет в чате
     */
    @Test
    void testBlockUserNotInChat() throws EntityNotFoundException {
        User user = new User("user@test.com", "pass", "f", "l");
        GroupChat groupChat = new GroupChat();

        Mockito.when(groupChatRepository.findById(1L)).thenReturn(Optional.of(groupChat));
        Mockito.when(userService.getUserById(1L)).thenReturn(user);

        Exception e = Assertions.assertThrows(BlacklistException.class, () -> groupChatService.blockUser(1L, 1L));
        Assertions.assertEquals("Пользователь 1 отсутствует в чате 1", e.getMessage());
        Mockito.verify(groupChatRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тест разблокировки пользователя в групповом чате
     */
    @Test
    void testUnblockUser() throws EntityNotFoundException, BlacklistException {
        User user1 = new User("user1@test.com", "pass1", "f1", "l1");
        User user2 = new User("user2@test.com", "pass2", "f2", "l2");
        User user3 = new User("user3@test.com", "pass3", "f3", "l3");
        GroupChat groupChat = new GroupChat(new HashSet<>(Set.of(user1, user2, user3)), "name");
        groupChat.getChatBlackList().add(user1);

        Mockito.when(groupChatRepository.findById(1L)).thenReturn(Optional.of(groupChat));
        Mockito.when(userService.getUserById(1L)).thenReturn(user1);

        groupChatService.unblockUser(1L, 1L);

        Assertions.assertFalse(groupChat.getChatBlackList().contains(user1));
        Assertions.assertTrue(groupChat.getMembers().contains(user1));
        Mockito.verify(groupChatRepository).save(groupChat);
    }

    /**
     * Тест ошибки при разблокировке пользователя, которого нет в чёрном списке
     */
    @Test
    void testUnblockUserNotInBlacklist() throws EntityNotFoundException {
        User user1 = new User("user1@test.com", "pass1", "f1", "l1");
        User user2 = new User("user2@test.com", "pass2", "f2", "l2");
        User user3 = new User("user3@test.com", "pass3", "f3", "l3");
        GroupChat groupChat = new GroupChat(Set.of(user1, user2, user3), "name");

        Mockito.when(groupChatRepository.findById(1L)).thenReturn(Optional.of(groupChat));
        Mockito.when(userService.getUserById(1L)).thenReturn(user1);

        Exception e = Assertions.assertThrows(BlacklistException.class, () -> groupChatService.unblockUser(1L, 1L));
        Assertions.assertEquals("Пользователь 1 не был забанен в чате 1", e.getMessage());
        Mockito.verify(groupChatRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тест выхода пользователя из группового чата
     */
    @Test
    void testLeaveGroupChat() throws EntityNotFoundException, InvalidChatException {
        User user = new User("user@test.com", "pass", "f", "l");
        GroupChat groupChat = new GroupChat();
        groupChat.getMembers().add(user);

        Mockito.when(groupChatRepository.findById(1L)).thenReturn(Optional.of(groupChat));
        Mockito.when(userService.getUserById(1L)).thenReturn(user);

        groupChatService.leaveGroupChat(1L, 1L);

        Assertions.assertFalse(groupChat.getMembers().contains(user));
        Assertions.assertFalse(user.getChats().contains(groupChat));
        Mockito.verify(groupChatRepository).delete(groupChat);
    }

    /**
     * Тест выхода пользователя из группового чата, если участников ещё 3 или более
     */
    @Test
    void testLeaveGroupChatThreeOrMoreMembers() throws EntityNotFoundException, InvalidChatException {
        User user1 = new User("user1@test.com", "pass1", "f1", "l1");
        User user2 = new User("user2@test.com", "pass2", "f2", "l2");
        User user3 = new User("user3@test.com", "pass3", "f3", "l3");
        User user4 = new User("user4@test.com", "pass4", "f4", "l4");
        GroupChat groupChat = new GroupChat(new HashSet<>(Set.of(user1, user2, user3, user4)), "name");

        Mockito.when(groupChatRepository.findById(1L)).thenReturn(Optional.of(groupChat));
        Mockito.when(userService.getUserById(1L)).thenReturn(user1);

        groupChatService.leaveGroupChat(1L, 1L);

        Assertions.assertFalse(groupChat.getMembers().contains(user1));
        Assertions.assertFalse(user1.getChats().contains(groupChat));
        Mockito.verify(groupChatRepository, Mockito.never()).delete(Mockito.any(GroupChat.class));
    }

    /**
     * Тест ошибки при выходе пользователя, не состоящего в чате
     */
    @Test
    void testLeaveGroupChatWhenUserNotMember() throws EntityNotFoundException {
        User user = new User("user@test.com", "pass", "f", "l");
        GroupChat groupChat = new GroupChat();

        Mockito.when(groupChatRepository.findById(1L)).thenReturn(Optional.of(groupChat));
        Mockito.when(userService.getUserById(1L)).thenReturn(user);

        Exception e = Assertions.assertThrows(InvalidChatException.class,
                () -> groupChatService.leaveGroupChat(1L, 1L));
        Assertions.assertEquals("Пользователь 1 не состоит в чате 1", e.getMessage());
    }
}