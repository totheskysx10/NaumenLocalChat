package ru.naumen.naumenlocalchat.extern.api.assembler;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import ru.naumen.naumenlocalchat.domain.User;
import ru.naumen.naumenlocalchat.extern.api.controller.UserController;
import ru.naumen.naumenlocalchat.extern.api.dto.UserDTO;

import java.util.stream.Collectors;

/**
 * Ассемблер пользователей
 */
@Component
public class UserAssembler extends RepresentationModelAssemblerSupport<User, UserDTO> {

    public UserAssembler() {
        super(UserController.class, UserDTO.class);
    }

    @Override
    public UserDTO toModel(User user) {
        UserDTO userDTO = instantiateModel(user);

        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());

        userDTO.setRoles(user.getRoles().stream()
                .map(Enum::toString)
                .collect(Collectors.toList()));

        return userDTO;
    }
}
