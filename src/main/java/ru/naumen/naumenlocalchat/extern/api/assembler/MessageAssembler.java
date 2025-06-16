package ru.naumen.naumenlocalchat.extern.api.assembler;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import ru.naumen.naumenlocalchat.app.service.UserService;
import ru.naumen.naumenlocalchat.domain.Message;
import ru.naumen.naumenlocalchat.exception.EntityNotFoundException;
import ru.naumen.naumenlocalchat.extern.api.controller.MessageController;
import ru.naumen.naumenlocalchat.extern.api.dto.MessageDTO;

/**
 * Ассемблер сообщений
 */
@Component
public class MessageAssembler extends RepresentationModelAssemblerSupport<Message, MessageDTO> {

    private final UserService userService;

    public MessageAssembler(UserService userService) {
        super(MessageController.class, MessageDTO.class);
        this.userService = userService;
    }

    @Override
    public MessageDTO toModel(Message message) {
        MessageDTO messageDTO = instantiateModel(message);

        messageDTO.setId(message.getId());
        messageDTO.setSenderId(message.getSender().getId());
        messageDTO.setContent(message.getContent());
        messageDTO.setTimestamp(message.getTimestamp());

        return messageDTO;
    }

    public Message toEntity(MessageDTO messageDTO) throws EntityNotFoundException {
        return new Message(userService.getUserById(messageDTO.getSenderId()), messageDTO.getContent());
    }
}
