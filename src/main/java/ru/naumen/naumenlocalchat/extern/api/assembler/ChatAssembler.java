package ru.naumen.naumenlocalchat.extern.api.assembler;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import ru.naumen.naumenlocalchat.app.service.UserService;
import ru.naumen.naumenlocalchat.domain.Chat;
import ru.naumen.naumenlocalchat.domain.User;
import ru.naumen.naumenlocalchat.exception.EntityNotFoundException;
import ru.naumen.naumenlocalchat.extern.api.controller.ChatController;
import ru.naumen.naumenlocalchat.extern.api.dto.ChatDTO;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Ассемблер чатов
 */
@Component
public class ChatAssembler extends RepresentationModelAssemblerSupport<Chat, ChatDTO> {

    private final UserService userService;

    public ChatAssembler(UserService userService) {
        super(ChatController.class, ChatDTO.class);
        this.userService = userService;
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

    public Chat toEntity(ChatDTO chatDTO) throws EntityNotFoundException {
        Set<User> members = new HashSet<>();
        for (Long id : chatDTO.getMemberIds()) {
            User user = userService.getUserById(id);
            members.add(user);
        }

        return new Chat(members);
    }
}
