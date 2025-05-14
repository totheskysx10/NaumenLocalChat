package ru.naumen.naumenlocalchat.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.naumen.naumenlocalchat.app.repository.GroupChatRepository;
import ru.naumen.naumenlocalchat.app.repository.UserRepository;
import ru.naumen.naumenlocalchat.domain.CodeType;
import ru.naumen.naumenlocalchat.domain.GroupChat;
import ru.naumen.naumenlocalchat.domain.User;
import ru.naumen.naumenlocalchat.exception.*;

import java.util.List;
import java.util.Optional;

/**
 * Сервис групповых чатов
 */
@Service
public class GroupChatService {

    private final GroupChatRepository groupChatRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final CodeService codeService;
    private final Logger log = LoggerFactory.getLogger(GroupChatService.class);

    public GroupChatService(GroupChatRepository groupChatRepository,
                            UserService userService,
                            UserRepository userRepository,
                            CodeService codeService) {
        this.groupChatRepository = groupChatRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.codeService = codeService;
    }

    /**
     * Создаёт новый групповой чат
     * @param groupChat чат
     * @throws InvalidChatException если участников менее трёх
     */
    public void createGroupChat(GroupChat groupChat, Long adminId) throws InvalidChatException, EntityNotFoundException {
        if (groupChat.getMembers().size() < 3) {
            throw new InvalidChatException("Количество участников должно быть минимум 3!");
        }

        groupChat.getMembers().forEach(member -> {
            member.getChats().add(groupChat);
            userRepository.save(member);
        });

        User admin = userService.getUserById(adminId);
        groupChat.setAdmin(admin);

        groupChatRepository.save(groupChat);
        log.info("Создан групповой чат с id {}", groupChat.getId());
    }

    /**
     * Добавляет пользователя в групповой чат по коду приглашения
     * @param invitationCode код приглашения
     * @param invitedUserId Id пользователя, который перешёл по коду (текущий id авторизации)
     * @throws EntityDuplicateException если пользователь уже есть в чате
     */
    public void inviteUserToChatByInvitationCode(String invitationCode, Long invitedUserId) throws InvalidCodeException, EntityNotFoundException, EntityDuplicateException {
        Long groupChatId = codeService.getIdByCode(CodeType.GROUP, invitationCode);
        GroupChat groupChat = findGroupChatById(groupChatId);
        User invitedUser = userService.getUserById(invitedUserId);

        if (!groupChat.getMembers().contains(invitedUser)) {
            groupChat.getMembers().add(invitedUser);
            invitedUser.getChats().add(groupChat);
            groupChatRepository.save(groupChat);
            userRepository.save(invitedUser);
        } else {
            throw new EntityDuplicateException("Пользователь с id " + invitedUserId + " уже есть в чате с id " + groupChatId);
        }
    }

    /**
     * Пользователь выходит из группового чата. Если остаётся менее 3 человек, чат удаляется
     * @param groupChatId идентификатор чата
     * @param userId идентификатор пользователя
     * @throws EntityNotFoundException если чат или пользователь не найден
     * @throws InvalidChatException если пользователь не является участником чата
     */
    public void leaveGroupChat(Long groupChatId, Long userId) throws EntityNotFoundException, InvalidChatException {
        GroupChat groupChat = findGroupChatById(groupChatId);
        User user = userService.getUserById(userId);

        if (!groupChat.getMembers().contains(user)) {
            throw new InvalidChatException("Пользователь " + userId + " не состоит в чате " + groupChatId);
        }

        groupChat.getMembers().remove(user);
        user.getChats().remove(groupChat);

        if (groupChat.getMembers().size() < 3) {
            deleteGroupChatById(groupChatId);
        }

        groupChatRepository.save(groupChat);
        userRepository.save(user);
        log.info("Пользователь {} вышел из чата {}", userId, groupChatId);
    }

    /**
     * Ищет групповой чат по Id
     * @param id идентификатор
     * @throws EntityNotFoundException если чат не найден
     */
    public GroupChat findGroupChatById(long id) throws EntityNotFoundException {
        Optional<GroupChat> groupChat = groupChatRepository.findById(id);
        return groupChat.orElseThrow(() -> new EntityNotFoundException("Не найден групповой чат с id " + id));
    }

    /**
     * Ищет групповые чаты пользователя по названию (его части)
     * @param userId идентификатор пользователя
     * @param name название или его часть
     */
    public List<GroupChat> findUserGroupChatsByNameContaining(Long userId, String name) throws EntityNotFoundException {
        User user = userService.getUserById(userId);
        return groupChatRepository.findByNameContainingIgnoreCaseAndMembersContaining(name, user);
    }

    /**
     * Ищет групповые чаты пользователя
     * @param userId идентификатор пользователя
     */
    public List<GroupChat> findUserGroupChats(Long userId) throws EntityNotFoundException {
        User user = userService.getUserById(userId);
        return groupChatRepository.findByMembersContaining(user);
    }

    /**
     * Удаляет групповой чат по Id
     * @param id идентификатор
     * @throws EntityNotFoundException если чат не найден
     */
    public void deleteGroupChatById(Long id) throws EntityNotFoundException {
        GroupChat groupChat = findGroupChatById(id);

        groupChat.getMembers().forEach(member -> {
            member.getChats().remove(groupChat);
        });

        groupChatRepository.delete(groupChat);
        log.info("Удалён групповой чат с id {}", groupChat.getId());
    }

    /**
     * Блокирует пользователя в групповом чате
     * @param groupChatId идентификатор чата
     * @param userId идентификатор пользователя
     * @throws BlacklistException если пользователя нет в чате
     */
    public void blockUser(Long groupChatId, Long userId) throws EntityNotFoundException, BlacklistException {
        GroupChat groupChat = findGroupChatById(groupChatId);
        User user = userService.getUserById(userId);

        if (groupChat.getMembers().contains(user)) {
            groupChat.getMembers().remove(user);
            groupChat.getChatBlackList().add(user);
            groupChatRepository.save(groupChat);
            log.info("Забанен пользователь {} в групповом чате с id {}", userId, groupChatId);
        } else {
            throw new BlacklistException("Пользователь " + userId + " отсутствует в чате " + groupChatId);
        }
    }

    /**
     * Разблокирует пользователя в групповом чате
     * @param groupChatId идентификатор чата
     * @param userId идентификатор пользователя
     * @throws BlacklistException если пользователя нет в чёрном списке
     */
    public void unblockUser(Long groupChatId, Long userId) throws EntityNotFoundException, BlacklistException {
        GroupChat groupChat = findGroupChatById(groupChatId);
        User user = userService.getUserById(userId);

        if (groupChat.getChatBlackList().contains(user)) {
            groupChat.getMembers().add(user);
            groupChat.getChatBlackList().remove(user);
            user.getChats().remove(groupChat);
            groupChatRepository.save(groupChat);
            log.info("Разбанен пользователь {} в групповом чате с id {}", userId, groupChatId);
        } else {
            throw new BlacklistException("Пользователь " + userId + " не был забанен в чате " + groupChatId);
        }
    }
}
