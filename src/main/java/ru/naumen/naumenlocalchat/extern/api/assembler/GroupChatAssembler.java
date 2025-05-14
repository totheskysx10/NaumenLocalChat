package ru.naumen.naumenlocalchat.extern.api.assembler;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import ru.naumen.naumenlocalchat.app.service.UserService;
import ru.naumen.naumenlocalchat.domain.GroupChat;
import ru.naumen.naumenlocalchat.domain.User;
import ru.naumen.naumenlocalchat.exception.EntityNotFoundException;
import ru.naumen.naumenlocalchat.extern.api.controller.GroupChatController;
import ru.naumen.naumenlocalchat.extern.api.dto.GroupChatDTO;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Ассемблер групповых чатов
 */
@Component
public class GroupChatAssembler extends RepresentationModelAssemblerSupport<GroupChat, GroupChatDTO> {

    private final UserService userService;

    public GroupChatAssembler(UserService userService) {
        super(GroupChatController.class, GroupChatDTO.class);
        this.userService = userService;
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

    public GroupChat toEntity(GroupChatDTO groupChatDTO) throws EntityNotFoundException {
        Set<User> members = new HashSet<>();
        for (Long id : groupChatDTO.getMemberIds()) {
            User user = userService.getUserById(id);
            members.add(user);
        }

        return new GroupChat(members, groupChatDTO.getName());
    }
}
