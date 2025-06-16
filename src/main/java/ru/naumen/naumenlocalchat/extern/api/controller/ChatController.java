package ru.naumen.naumenlocalchat.extern.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.naumen.naumenlocalchat.app.service.ChatService;
import ru.naumen.naumenlocalchat.domain.Chat;
import ru.naumen.naumenlocalchat.exception.EntityDuplicateException;
import ru.naumen.naumenlocalchat.exception.EntityNotFoundException;
import ru.naumen.naumenlocalchat.exception.InvalidCodeException;
import ru.naumen.naumenlocalchat.extern.api.assembler.ChatAssembler;
import ru.naumen.naumenlocalchat.extern.api.dto.ChatDTO;
import ru.naumen.naumenlocalchat.extern.api.dto.CodeDTO;

import java.util.List;

@RestController
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;
    private final ChatAssembler chatAssembler;

    public ChatController(ChatService chatService, ChatAssembler chatAssembler) {
        this.chatService = chatService;
        this.chatAssembler = chatAssembler;
    }

    @PostMapping("/create-chat")
    public ResponseEntity<Void> createChatByInvitationCode(@RequestParam String invitationCode, @RequestParam Long userId) throws EntityDuplicateException, EntityNotFoundException, InvalidCodeException {
        chatService.createChatByInvitationCode(invitationCode, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<ChatDTO> findChatById(@RequestParam Long chatId) throws EntityNotFoundException {
        Chat chat = chatService.findChatById(chatId);
        return ResponseEntity.ok().body(chatAssembler.toModel(chat));
    }

    @GetMapping("/user-chats")
    public ResponseEntity<List<ChatDTO>> findUserChats(@RequestParam Long userId) throws EntityNotFoundException {
        List<ChatDTO> chats = chatService.findUserChats(userId).stream()
                .map(chatAssembler::toModel)
                .toList();

        if (chats.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(chats);
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity<Void> deleteChatById(@RequestParam Long chatId) throws EntityNotFoundException {
        chatService.deleteChatById(chatId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/invite-user")
    public ResponseEntity<CodeDTO> inviteUser(@RequestParam Long userId) {
        String code = chatService.inviteUser(userId);
        return ResponseEntity.ok().body(new CodeDTO(code));
    }
}
