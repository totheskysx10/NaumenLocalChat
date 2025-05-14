package ru.naumen.naumenlocalchat.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.naumen.naumenlocalchat.app.repository.UserRepository;
import ru.naumen.naumenlocalchat.domain.EmailData;
import ru.naumen.naumenlocalchat.domain.Role;
import ru.naumen.naumenlocalchat.domain.TokenType;
import ru.naumen.naumenlocalchat.domain.User;
import ru.naumen.naumenlocalchat.exception.InvalidTokenException;
import ru.naumen.naumenlocalchat.exception.EntityDuplicateException;
import ru.naumen.naumenlocalchat.exception.EntityNotFoundException;
import ru.naumen.naumenlocalchat.extern.infrastructure.EmailService;

import java.util.List;
import java.util.Optional;

/**
 * Сервис пользователей
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final String confirmEmailLinkTemplate;
    private final String resetPasswordLinkTemplate;
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository,
                       TokenService tokenService,
                       EmailService emailService,
                       @Value("${app.url.confirm-email}") String confirmEmailUrlTemplate,
                       @Value("${app.url.reset-password}") String resetPasswordUrlTemplate) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.confirmEmailLinkTemplate = confirmEmailUrlTemplate;
        this.resetPasswordLinkTemplate = resetPasswordUrlTemplate;
    }

    /**
     * Создаёт пользователя
     * @param user пользователь
     * @throws EntityDuplicateException если пользователь с email уже есть
     */
    public void createUser(User user) throws EntityDuplicateException {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EntityDuplicateException("Пользователь с Email " + user.getEmail() + " уже существует!");
        }

        user.setRoles(List.of(Role.USER));

        userRepository.save(user);
        log.info("Создан пользователь с email {} и id {}", user.getEmail(), user.getId());
    }

    /**
     * Ищет пользователя по идентификатору
     * @param userId идентификатор
     * @throws EntityNotFoundException если пользователь не найден
     */
    public User getUserById(Long userId) throws EntityNotFoundException {
        Optional<User> user = userRepository.findById(userId);
        return user.orElseThrow(() -> new EntityNotFoundException("Пользователь с Id " + userId + " не найден!"));
    }

    /**
     * Удаляет пользователя
     * @param userId идентификатор пользователя
     */
    public void deleteUser(Long userId) throws EntityNotFoundException {
        User user = getUserById(userId);
        userRepository.delete(user);
        log.info("Удалён пользователь с email {} и id {}", user.getEmail(), user.getId());
    }

    /**
     * Отправляет на почту сообщение для подтверждения Email
     * @param userId идентификатор пользователя
     */
    public void sendMessageForEmailConfirmation(Long userId) throws EntityNotFoundException {
        User user = getUserById(userId);
        String token = tokenService.generateToken(TokenType.EMAIL_CONFIRM, userId);
        String confirmEmailLink = confirmEmailLinkTemplate.replace("{id}", "id=" + userId.toString()) + token;

        String subject = EmailData.CONFIRM_EMAIL.getEmailSubject();
        String message = String.format(EmailData.CONFIRM_EMAIL.getEmailMessage(), confirmEmailLink);

        emailService.sendEmail(user.getEmail(), subject, message);
        log.info("Отправлена заявка на подтверждение email {}", user.getEmail());
    }

    /**
     * Отправляет на почту сообщение для сброса пароля
     * @param userId идентификатор пользователя
     */
    public void sendMessageForPasswordReset(Long userId) throws EntityNotFoundException {
        User user = getUserById(userId);
        String token = tokenService.generateToken(TokenType.RESET_PASSWORD, userId);
        String resetPasswordLink = resetPasswordLinkTemplate.replace("{id}", "id=" + userId.toString()) + token;

        String subject = EmailData.RESET_PASSWORD.getEmailSubject();
        String message = String.format(EmailData.RESET_PASSWORD.getEmailMessage(), resetPasswordLink);

        emailService.sendEmail(user.getEmail(), subject, message);
        log.info("Отправлена заявка на сброс пароля пользователя с id {}", userId);
    }

    /**
     * Подтверждает email пользователя
     * @param token токен
     * @param userId идентификатор пользователя
     * @throws InvalidTokenException если токен невалиден
     */
    public void confirmEmail(String token, Long userId) throws EntityNotFoundException, InvalidTokenException {
        boolean tokenValid = tokenService.isTokenValid(TokenType.EMAIL_CONFIRM, token, userId);

        if (tokenValid) {
            User user = getUserById(userId);
            user.setEmailConfirmed(true);
            tokenService.invalidateToken(TokenType.EMAIL_CONFIRM, token);
            log.info("Email пользователя с id {} подтверждён", userId);
        } else {
            throw new InvalidTokenException("Email пользователя с Id " + userId + " не подтверждён!");
        }
    }

    /**
     * Обновляет пароль пользователя
     * @param token токен
     * @param userId идентификатор пользователя
     * @param newEncodedPassword шифрованный новый пароль
     * @throws InvalidTokenException если токен невалиден
     */
    public void resetPassword(String token, Long userId, String newEncodedPassword) throws EntityNotFoundException, InvalidTokenException {
        boolean tokenValid = tokenService.isTokenValid(TokenType.RESET_PASSWORD, token, userId);

        if (tokenValid) {
            User user = getUserById(userId);
            user.setPassword(newEncodedPassword);
            tokenService.invalidateToken(TokenType.RESET_PASSWORD, token);
            log.info("Пароль пользователя с id {} обновлён", userId);
        } else {
            throw new InvalidTokenException("Пароль пользователя с Id " + userId + " не обновлён!");
        }
    }
}
