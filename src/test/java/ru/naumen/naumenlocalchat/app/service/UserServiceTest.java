package ru.naumen.naumenlocalchat.app.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.naumen.naumenlocalchat.app.repository.UserRepository;
import ru.naumen.naumenlocalchat.domain.*;
import ru.naumen.naumenlocalchat.exception.InvalidTokenException;
import ru.naumen.naumenlocalchat.exception.EntityDuplicateException;
import ru.naumen.naumenlocalchat.exception.EntityNotFoundException;
import ru.naumen.naumenlocalchat.extern.infrastructure.EmailService;

import java.util.List;
import java.util.Optional;


/**
 * Тест сервиса пользователей
 */
class UserServiceTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        String resetPasswordLinkTemplate = "http://test/reset-password?{id}&token=";
        String confirmEmailLinkTemplate = "http://test/confirm-email?{id}&token=";

        userService = new UserService(
                userRepository,
                tokenService,
                emailService,
                confirmEmailLinkTemplate,
                resetPasswordLinkTemplate
        );
    }

    /**
     * Тест создания пользователя
     */
    @Test
    void testCreateUser() throws EntityDuplicateException {
        User user = new User("test@test.com", "pass", "f", "l");
        Mockito.when(userRepository.existsByEmail("test@test.com")).thenReturn(false);

        userService.createUser(user);

        Mockito.verify(userRepository).save(user);
        Assertions.assertEquals(List.of(Role.USER), user.getRoles());
        Assertions.assertTrue(user.getChats().isEmpty());
    }

    /**
     * Тест ошибки при создании дубликата пользователя
     */
    @Test
    void testCreateUserDuplicate() {
        User user = new User("test@test.com", "pass", "f", "l");
        Mockito.when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        Exception e = Assertions.assertThrows(EntityDuplicateException.class, () -> userService.createUser(user));
        Assertions.assertEquals("Пользователь с Email test@test.com уже существует!", e.getMessage());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    /**
     * Тест получения пользователя по ID
     */
    @Test
    void testGetUserById() throws EntityNotFoundException {
        User user = new User("test@test.com", "pass", "f", "l");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(1L);

        Assertions.assertEquals(user, foundUser);
    }

    /**
     * Тест ошибки при получении несуществующего пользователя
     */
    @Test
    void testGetUserByIdNotFound() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception e = Assertions.assertThrows(EntityNotFoundException.class, () -> userService.getUserById(1L));
        Assertions.assertEquals("Пользователь с Id 1 не найден!", e.getMessage());
    }

    /**
     * Тест удаления пользователя
     */
    @Test
    void testDeleteUser() throws EntityNotFoundException {
        User user = new User("test@test.com", "pass", "f", "l");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        Mockito.verify(userRepository).delete(user);
    }

    /**
     * Тест отправки сообщения для подтверждения email
     */
    @Test
    void testSendMessageForEmailConfirmation() throws EntityNotFoundException {
        User user = new User("test@test.com", "pass", "f", "l");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(tokenService.generateToken(TokenType.EMAIL_CONFIRM, 1L)).thenReturn("token");

        userService.sendMessageForEmailConfirmation(1L);

        Mockito.verify(emailService).sendEmail(
                "test@test.com",
                "NaumenLocalChat - Подтверждение почты",
                """
                    <html>
                        <body>
                            <p>Чтобы подтвердить адрес электронной почты, пройдите по ссылке:</p>
                            <a href="http://test/confirm-email?id=1&token=token">Подтвердить</a>
                        </body>
                    </html>""");
    }

    /**
     * Тест отправки сообщения для сброса пароля
     */
    @Test
    void testSendMessageForPasswordReset() throws EntityNotFoundException {
        User user = new User("test@test.com", "pass", "f", "l");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(tokenService.generateToken(TokenType.RESET_PASSWORD, 1L)).thenReturn("token");

        userService.sendMessageForPasswordReset(1L);

        Mockito.verify(emailService).sendEmail(
                "test@test.com",
                "NaumenLocalChat - Восстановление пароля",
                """
                    <html>
                        <body>
                            <p>Чтобы сменить пароль и восстановить доступ, пройдите по ссылке (действует в течение 20 минут):</p>
                            <a href="http://test/reset-password?id=1&token=token">Сбросить пароль</a>
                        </body>
                    </html>""");
    }

    /**
     * Тест подтверждения email с валидным токеном
     */
    @Test
    void testConfirmEmail() throws EntityNotFoundException, InvalidTokenException {
        User user = new User("test@test.com", "pass", "f", "l");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(tokenService.isTokenValid(TokenType.EMAIL_CONFIRM, "token", 1L)).thenReturn(true);

        userService.confirmEmail("token", 1L);

        Assertions.assertTrue(user.isEmailConfirmed());
        Mockito.verify(tokenService).invalidateToken(TokenType.EMAIL_CONFIRM, "token");
    }

    /**
     * Тест ошибки при подтверждении email с невалидным токеном
     */
    @Test
    void testConfirmEmailInvalidToken() {
        Mockito.when(tokenService.isTokenValid(TokenType.EMAIL_CONFIRM, "token", 1L)).thenReturn(false);

        Exception e = Assertions.assertThrows(InvalidTokenException.class, () -> userService.confirmEmail("token", 1L));
        Assertions.assertEquals("Email пользователя с Id 1 не подтверждён!", e.getMessage());
    }

    /**
     * Тест сброса пароля с валидным токеном
     */
    @Test
    void testResetPassword() throws EntityNotFoundException, InvalidTokenException {
        User user = new User("test@test.com", "pass", "f", "l");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(tokenService.isTokenValid(TokenType.RESET_PASSWORD, "token", 1L)).thenReturn(true);

        userService.resetPassword("token", 1L, "newEncodedPassword");

        Assertions.assertEquals("newEncodedPassword", user.getPassword());
        Mockito.verify(tokenService).invalidateToken(TokenType.RESET_PASSWORD, "token");
    }

    /**
     * Тест ошибки при сбросе пароля с невалидным токеном
     */
    @Test
    void testResetPasswordInvalidToken() {
        Mockito.when(tokenService.isTokenValid(TokenType.RESET_PASSWORD, "token", 1L)).thenReturn(false);

        Exception e = Assertions.assertThrows(InvalidTokenException.class, () -> userService.resetPassword("token", 1L, "newPassword"));
        Assertions.assertEquals("Пароль пользователя с Id 1 не обновлён!", e.getMessage());
    }
}