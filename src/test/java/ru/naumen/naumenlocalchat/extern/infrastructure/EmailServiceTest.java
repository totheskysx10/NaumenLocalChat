package ru.naumen.naumenlocalchat.extern.infrastructure;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Тест сервиса Email
 */
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    private EmailService emailService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.emailService = new EmailService(mailSender, "test@mail.ru");
    }

    /**
     * Тест отправки Email
     */
    @Test
    public void testSendEmail() throws InterruptedException {
        String receiver = "test@test.com";
        String subject = "Test Subject";
        String content = "Test Content";

        emailService.sendEmail(receiver, subject, content);

        Thread.sleep(100);

        ArgumentCaptor<SimpleMailMessage> argument = ArgumentCaptor.forClass(SimpleMailMessage.class);
        Mockito.verify(mailSender, Mockito.times(1)).send(argument.capture());

        Assertions.assertEquals(receiver, argument.getValue().getTo()[0]);
        Assertions.assertEquals(subject, argument.getValue().getSubject());
        Assertions.assertEquals(content, argument.getValue().getText());
    }
}