package ru.naumen.naumenlocalchat.extern.api.assembler;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import ru.naumen.naumenlocalchat.domain.Message;
import ru.naumen.naumenlocalchat.extern.api.controller.MessageController;
import ru.naumen.naumenlocalchat.extern.api.dto.MessageDTO;

/**
 * Ассемблер сообщений
 */
public class MessageAssembler extends RepresentationModelAssemblerSupport<Message, MessageDTO> {

    public MessageAssembler() {
        super(MessageController.class, MessageDTO.class);
    }

    @Override
    public MessageDTO toModel(Message message) {
        MessageDTO messageDTO = instantiateModel(message);

        messageDTO.setId(message.getId());
        messageDTO.setSenderId(message.getSender().getId());
        messageDTO.setContent(message.getContent());
        messageDTO.setTimestamp(message.getTimestamp());
        messageDTO.setChatId(message.getChat().getId());

        return messageDTO;
    }
}
