package ru.naumen.naumenlocalchat.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.naumen.naumenlocalchat.app.repository.UserRepository;
import ru.naumen.naumenlocalchat.domain.EmailData;
import ru.naumen.naumenlocalchat.domain.Role;
import ru.naumen.naumenlocalchat.domain.TokenType;
import ru.naumen.naumenlocalchat.domain.User;
import ru.naumen.naumenlocalchat.exception.AdminException;
import ru.naumen.naumenlocalchat.exception.InvalidTokenException;
import ru.naumen.naumenlocalchat.exception.EntityDuplicateException;
import ru.naumen.naumenlocalchat.exception.EntityNotFoundException;
import ru.naumen.naumenlocalchat.extern.infrastructure.service.EmailService;

import java.util.*;

/**
 * Сервис пользователей
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final String confirmEmailLinkTemplate;
    private final String resetPasswordLinkTemplate;
    private final String ROLE_PREFIX = "ROLE_";

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
    public User createUser(User user) throws EntityDuplicateException, EntityNotFoundException {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EntityDuplicateException("Пользователь с Email " + user.getEmail() + " уже существует!");
        }

        user.setRoles(Set.of(Role.USER));

        userRepository.save(user);
        sendMessageForEmailConfirmation(user.getId());
        log.info("Создан пользователь с email {} и id {}", user.getEmail(), user.getId());

        return user;
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
        String confirmEmailLink = confirmEmailLinkTemplate.replace("{id}", "userId=" + userId.toString()) + token;

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
        String resetPasswordLink = resetPasswordLinkTemplate.replace("{id}", "userId=" + userId.toString()) + token;

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
            userRepository.save(user);
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
            userRepository.save(user);
            log.info("Пароль пользователя с id {} обновлён", userId);
        } else {
            throw new InvalidTokenException("Пароль пользователя с Id " + userId + " не обновлён!");
        }
    }

    /**
     * Выдаёт пользователю права админа
     * @param userId идентификатор пользователя
     * @throws AdminException если пользователь уже админ
     */
    public void giveAdminRules(long userId) throws EntityNotFoundException, AdminException {
        User user = getUserById(userId);

        if (user.getRoles().contains(Role.ADMIN)) {
            throw new AdminException("Пользователь " + userId + " уже админ");
        }

        user.getRoles().add(Role.ADMIN);
        userRepository.save(user);
    }

    /**
     * Забирает у пользователя права админа
     * @param userId идентификатор пользователя
     * @throws AdminException если пользователь уже не админ
     */
    public void removeAdminRules(long userId) throws EntityNotFoundException, AdminException {
        User user = getUserById(userId);

        if (!user.getRoles().contains(Role.ADMIN)) {
            throw new AdminException("Пользователь " + userId + " уже не админ");
        }

        user.getRoles().remove(Role.ADMIN);
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> foundUser = userRepository.findByEmail(username);

        if (foundUser.isPresent()) {
            User user = foundUser.get();
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), mapUserRoles(user.getRoles()));
        } else {
            throw new UsernameNotFoundException("Пользователь с именем " + username + " не найден!");
        }
    }

    /**
     * Маппит роли пользователя в SimpleGrantedAuthority
     * @param roles роли
     */
    private Collection<? extends GrantedAuthority> mapUserRoles(Set<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
                .toList();
    }
}
