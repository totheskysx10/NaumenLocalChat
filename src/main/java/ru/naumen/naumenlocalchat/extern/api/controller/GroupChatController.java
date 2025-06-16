package ru.naumen.naumenlocalchat.extern.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.naumen.naumenlocalchat.app.service.GroupChatService;
import ru.naumen.naumenlocalchat.domain.GroupChat;
import ru.naumen.naumenlocalchat.exception.*;
import ru.naumen.naumenlocalchat.extern.api.assembler.GroupChatAssembler;
import ru.naumen.naumenlocalchat.extern.api.dto.CodeDTO;
import ru.naumen.naumenlocalchat.extern.api.dto.GroupChatDTO;

import java.util.List;

@RestController
@RequestMapping("/group-chats")
public class GroupChatController {

    private final GroupChatService groupChatService;
    private final GroupChatAssembler groupChatAssembler;

    public GroupChatController(GroupChatService groupChatService, GroupChatAssembler groupChatAssembler) {
        this.groupChatService = groupChatService;
        this.groupChatAssembler = groupChatAssembler;
    }

    @PostMapping
    public ResponseEntity<Void> createGroupChat(@RequestBody GroupChat groupChat, @RequestParam Long adminId)
            throws ChatException, EntityNotFoundException {
        groupChatService.createGroupChat(groupChat, adminId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/enter")
    public ResponseEntity<Void> EnterByCode(@RequestParam String invitationCode, @RequestParam Long userId)
            throws InvalidCodeException, EntityNotFoundException, EntityDuplicateException {
        groupChatService.EnterToChatByInvitationCode(invitationCode, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/leave")
    public ResponseEntity<Void> leaveGroupChat(@RequestParam Long groupChatId, @RequestParam Long userId) throws EntityNotFoundException, ChatException {
        groupChatService.leaveGroupChat(groupChatId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<GroupChatDTO> getGroupChatById(@RequestParam Long id) throws EntityNotFoundException {
        GroupChat groupChat = groupChatService.findGroupChatById(id);
        return ResponseEntity.ok(groupChatAssembler.toModel(groupChat));
    }

    @GetMapping("/user-chats")
    public ResponseEntity<List<GroupChatDTO>> getUserGroupChats(@RequestParam Long userId) throws EntityNotFoundException {
        List<GroupChatDTO> chats = groupChatService.findUserGroupChats(userId).stream()
                .map(groupChatAssembler::toModel)
                .toList();

        if (chats.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(chats);
    }

    @GetMapping("/found-user-chats")
    public ResponseEntity<List<GroupChatDTO>> searchUserChatsByName(@RequestParam Long userId, @RequestParam String name) throws EntityNotFoundException {
        List<GroupChatDTO> chats = groupChatService.findUserGroupChatsByNameContaining(userId, name)
                .stream()
                .map(groupChatAssembler::toModel)
                .toList();

        if (chats.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(chats);
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity<Void> deleteGroupChat(@RequestParam Long id) throws EntityNotFoundException {
        groupChatService.deleteGroupChatById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/block")
    public ResponseEntity<Void> blockUser(@RequestParam Long chatId, @RequestParam Long userId)
            throws EntityNotFoundException, BlacklistException {
        groupChatService.blockUser(chatId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unblock")
    public ResponseEntity<Void> unblockUser(@RequestParam Long chatId, @RequestParam Long userId)
            throws EntityNotFoundException, BlacklistException {
        groupChatService.unblockUser(chatId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/invite-user")
    public ResponseEntity<CodeDTO> inviteUser(@RequestParam Long chatId) {
        String code = groupChatService.inviteUser(chatId);
        return ResponseEntity.ok().body(new CodeDTO(code));
    }
}