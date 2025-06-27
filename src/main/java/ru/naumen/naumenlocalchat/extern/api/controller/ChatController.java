package ru.naumen.naumenlocalchat.extern.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.naumen.naumenlocalchat.app.service.ChatService;
import ru.naumen.naumenlocalchat.domain.Chat;
import ru.naumen.naumenlocalchat.exception.AuthException;
import ru.naumen.naumenlocalchat.exception.EntityDuplicateException;
import ru.naumen.naumenlocalchat.exception.EntityNotFoundException;
import ru.naumen.naumenlocalchat.exception.InvalidCodeException;
import ru.naumen.naumenlocalchat.extern.api.assembler.ChatAssembler;
import ru.naumen.naumenlocalchat.extern.api.dto.ChatDTO;
import ru.naumen.naumenlocalchat.extern.api.dto.CodeDTO;
import ru.naumen.naumenlocalchat.extern.infrastructure.service.SecurityContextService;

import java.util.List;

@RestController
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;
    private final ChatAssembler chatAssembler;
    private final SecurityContextService securityContextService;

    public ChatController(ChatService chatService, ChatAssembler chatAssembler, SecurityContextService securityContextService) {
        this.chatService = chatService;
        this.chatAssembler = chatAssembler;
        this.securityContextService = securityContextService;
    }

    @PostMapping("/create-chat")
    public ResponseEntity<Void> createChatByInvitationCode(@RequestParam String invitationCode) throws EntityDuplicateException, EntityNotFoundException, InvalidCodeException, AuthException {
        chatService.createChatByInvitationCode(invitationCode, securityContextService.getCurrentAuthId());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<ChatDTO> findChatById(@RequestParam Long chatId) throws EntityNotFoundException {
        Chat chat = chatService.findChatById(chatId);
        return ResponseEntity.ok().body(chatAssembler.toModel(chat));
    }

    @GetMapping("/user-chats")
    public ResponseEntity<List<ChatDTO>> findCurrentUserChats() throws EntityNotFoundException, AuthException {
        List<ChatDTO> chats = chatService.findUserChats(securityContextService.getCurrentAuthId()).stream()
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
    public ResponseEntity<CodeDTO> inviteUser() throws AuthException {
        String code = chatService.inviteUser(securityContextService.getCurrentAuthId());
        return ResponseEntity.ok().body(new CodeDTO(code));
    }
}
