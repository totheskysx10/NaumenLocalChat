package ru.naumen.naumenlocalchat.extern.api.controller;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import ru.naumen.naumenlocalchat.app.service.MessageService;
import ru.naumen.naumenlocalchat.domain.Message;
import ru.naumen.naumenlocalchat.exception.ChatException;
import ru.naumen.naumenlocalchat.exception.EntityNotFoundException;
import ru.naumen.naumenlocalchat.extern.api.assembler.MessageAssembler;
import ru.naumen.naumenlocalchat.extern.api.dto.MessageDTO;

@Controller
public class MessageWebsocketController {

    private final MessageService messageService;
    private final MessageAssembler messageAssembler;

    public MessageWebsocketController(MessageService messageService, MessageAssembler messageAssembler) {
        this.messageService = messageService;
        this.messageAssembler = messageAssembler;
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageDTO messageDTO, @Header("chatId") Long chatId) throws EntityNotFoundException, ChatException {
        Message message = messageAssembler.toEntity(messageDTO);
        messageService.sendMessage(message, chatId);
    }

    @MessageMapping("/chat.deleteMessage")
    public void deleteMessage(@Header("messageId") Long messageId,
                                    @Header("groupChatId") Long groupChatId,
                                    @Header("userId") Long userId) throws EntityNotFoundException, ChatException {
        messageService.deleteMessage(messageId, groupChatId, userId);
    }
}
