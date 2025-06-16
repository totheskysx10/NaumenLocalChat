package ru.naumen.naumenlocalchat.extern.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.naumen.naumenlocalchat.app.service.MessageService;
import ru.naumen.naumenlocalchat.extern.api.assembler.MessageAssembler;
import ru.naumen.naumenlocalchat.extern.api.dto.MessageDTO;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final MessageAssembler messageAssembler;

    public MessageController(MessageService messageService, MessageAssembler messageAssembler) {
        this.messageService = messageService;
        this.messageAssembler = messageAssembler;
    }

    @GetMapping("/chat")
    public ResponseEntity<List<MessageDTO>> findChatMessages(@RequestParam Long chatId) {
        List<MessageDTO> messages = messageService.findChatMessages(chatId).stream()
                .map(messageAssembler::toModel)
                .toList();

        if (messages.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(messages);
    }

    @GetMapping("/chat/search")
    public ResponseEntity<List<MessageDTO>> searchMessages(@RequestParam Long chatId, @RequestParam String query) {
        List<MessageDTO> messages = messageService.searchMessagesInChat(chatId, query).stream()
                .map(messageAssembler::toModel)
                .toList();

        if (messages.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(messages);
    }
}