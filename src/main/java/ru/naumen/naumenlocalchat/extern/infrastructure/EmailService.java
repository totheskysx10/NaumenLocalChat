package ru.naumen.naumenlocalchat.extern.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Сервис отправки Email
 */
@Component
public class EmailService {

    private final JavaMailSender mailSender;
    private final Logger log = LoggerFactory.getLogger(EmailService.class);

    /**
     * Отправитель сообщения
     */
    private final String from;

    public EmailService(JavaMailSender mailSender,
                        @Value("${spring.mail.username}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    /**
     * Отправляет сообщение по электронной почте
     * @param to получатель
     * @param subject тема
     * @param text текст письма
     */
    public void sendEmail(String to, String subject, String text) {
        CompletableFuture.runAsync(() -> {
            try {
                SimpleMailMessage message = new SimpleMailMessage();

                message.setFrom(from);
                message.setTo(to);
                message.setSubject(subject);
                message.setText(text);

                mailSender.send(message);
                log.info("Отправлено сообщение на адрес {}", to);
            } catch (MailException e) {
                log.error(e.getMessage());
            }
        });
    }
}
