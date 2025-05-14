package ru.naumen.naumenlocalchat.extern.api.assembler;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import ru.naumen.naumenlocalchat.domain.Chat;
import ru.naumen.naumenlocalchat.domain.User;
import ru.naumen.naumenlocalchat.extern.api.controller.ChatController;
import ru.naumen.naumenlocalchat.extern.api.dto.ChatDTO;

import java.util.stream.Collectors;

/**
 * Ассемблер чатов
 */
public class ChatAssembler extends RepresentationModelAssemblerSupport<Chat, ChatDTO> {

    public ChatAssembler() {
        super(ChatController.class, ChatDTO.class);
    }

    @Override
    public ChatDTO toModel(Chat chat) {
        ChatDTO chatDTO = instantiateModel(chat);

        chatDTO.setId(chat.getId());

        chatDTO.setMemberIds(chat.getMembers().stream()
                .map(User::getId)
                .collect(Collectors.toSet()));

        return chatDTO;
    }
}
