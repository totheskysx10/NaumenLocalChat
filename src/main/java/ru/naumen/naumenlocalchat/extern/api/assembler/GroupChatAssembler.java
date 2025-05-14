package ru.naumen.naumenlocalchat.extern.api.assembler;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import ru.naumen.naumenlocalchat.domain.GroupChat;
import ru.naumen.naumenlocalchat.domain.User;
import ru.naumen.naumenlocalchat.extern.api.controller.GroupChatController;
import ru.naumen.naumenlocalchat.extern.api.dto.GroupChatDTO;

import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Ассемблер групповых чатов
 */
public class GroupChatAssembler extends RepresentationModelAssemblerSupport<GroupChat, GroupChatDTO> {

    public GroupChatAssembler() {
        super(GroupChatController.class, GroupChatDTO.class);
    }

    @Override
    public GroupChatDTO toModel(GroupChat groupChat) {
        GroupChatDTO groupChatDTO = instantiateModel(groupChat);

        groupChatDTO.setId(groupChat.getId());

        groupChatDTO.setMemberIds(groupChat.getMembers().stream()
                .map(User::getId)
                .collect(Collectors.toSet()));

        groupChatDTO.setName(groupChat.getName());
        groupChatDTO.setAdminId(groupChat.getAdmin().getId());

        if (!groupChat.getChatBlackList().isEmpty()) {
            groupChatDTO.setBlackListIds(groupChat.getChatBlackList().stream()
                    .map(User::getId)
                    .collect(Collectors.toSet()));
        } else {
            groupChatDTO.setBlackListIds(new HashSet<>());
        }

        return groupChatDTO;
    }
}
