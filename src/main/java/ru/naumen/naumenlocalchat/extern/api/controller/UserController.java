package ru.naumen.naumenlocalchat.extern.api.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.naumen.naumenlocalchat.app.service.UserService;
import ru.naumen.naumenlocalchat.domain.User;
import ru.naumen.naumenlocalchat.exception.*;
import ru.naumen.naumenlocalchat.extern.api.assembler.UserAssembler;
import ru.naumen.naumenlocalchat.extern.api.dto.*;
import ru.naumen.naumenlocalchat.extern.infrastructure.service.SecurityContextService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserAssembler userAssembler;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final SecurityContextService securityContextService;

    public UserController(UserService userService, UserAssembler userAssembler, PasswordEncoder bCryptPasswordEncoder, SecurityContextService securityContextService) {
        this.userService = userService;
        this.userAssembler = userAssembler;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.securityContextService = securityContextService;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> createUser(@RequestBody @Valid RegisterDTO registerDTO) throws EntityDuplicateException, EntityNotFoundException {
        if (!registerDTO.getPassword().equals(registerDTO.getPasswordConfirm())) {
            ErrorDTO errorDTO = new ErrorDTO("Passwords do not match");
            return new ResponseEntity<>(errorDTO, HttpStatus.FORBIDDEN);
        }

        User user = new User(registerDTO.getEmail(),
                bCryptPasswordEncoder.encode(registerDTO.getPassword()),
                registerDTO.getFirstName(),
                registerDTO.getLastName());

        User createdUser = userService.createUser(user);

        return ResponseEntity.ok().body(userAssembler.toModel(createdUser));
    }

    @GetMapping
    public ResponseEntity<UserDTO> getUserById(@RequestParam long userId) throws EntityNotFoundException {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(userAssembler.toModel(user));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(@RequestParam long userId) throws EntityNotFoundException {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(@RequestParam Long userId,
                                              @RequestParam String token,
                                              @Valid @RequestBody UserUpdatePasswordDTO userUpdatePasswordDTO) throws InvalidTokenException, EntityNotFoundException {
        if (!userUpdatePasswordDTO.getPassword().equals(userUpdatePasswordDTO.getPasswordConfirm())) {
            ErrorDTO errorDTO = new ErrorDTO("Passwords do not match");
            return new ResponseEntity<>(errorDTO, HttpStatus.FORBIDDEN);
        }

        String encodedPass = bCryptPasswordEncoder.encode(userUpdatePasswordDTO.getPassword());
        userService.resetPassword(token, userId, encodedPass);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/request-reset-password")
    public ResponseEntity<Void> sendMessageForPasswordReset(@RequestParam Long userId) throws EntityNotFoundException {
        userService.sendMessageForPasswordReset(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/confirm-email")
    public ResponseEntity<Void> confirmEmail(@RequestParam Long userId, @RequestParam String token) throws InvalidTokenException, EntityNotFoundException {
        userService.confirmEmail(token, userId);
        return ResponseEntity.ok().build();

    }

    @PostMapping("/request-confirm-email")
    public ResponseEntity<Void> sendMessageForEmailConfirmation(@RequestParam Long userId) throws EntityNotFoundException {
        userService.sendMessageForEmailConfirmation(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/admin")
    public ResponseEntity<Void> giveAdminRules(@RequestParam Long userId) throws EntityNotFoundException, AdminException {
        userService.giveAdminRules(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/no-admin")
    public ResponseEntity<Void> removeAdminRules(@RequestParam Long userId) throws EntityNotFoundException, AdminException {
        userService.removeAdminRules(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/current-auth-id")
    public ResponseEntity<UserIdDTO> getCurrentUserId() throws AuthException {
        Long userId = securityContextService.getCurrentAuthId();
        return ResponseEntity.ok(new UserIdDTO(userId));
    }
}
