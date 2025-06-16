package ru.naumen.naumenlocalchat.domain;

/**
 * Данные для отправки письма на электронную почту
 */
public enum EmailData {

    /**
     * Сброс пароля
     */
    RESET_PASSWORD("NaumenLocalChat - Восстановление пароля",
            """
                    <html>
                        <body>
                            <p>Чтобы сменить пароль и восстановить доступ, пройдите по ссылке (действует в течение 20 минут):</p>
                            <a href="%s">Сбросить пароль</a>
                        </body>
                    </html>"""),

    /**
     * Подтверждение Email
     */
    CONFIRM_EMAIL("NaumenLocalChat - Подтверждение почты",
            """
                    <html>
                        <body>
                            <p>Чтобы подтвердить адрес электронной почты, пройдите по ссылке:</p>
                            <a href="%s">Подтвердить</a>
                        </body>
                    </html>""");

    /**
     * Тема письма
     */
    private final String emailSubject;

    /**
     * Текст письма
     */
    private final String emailMessage;

    EmailData(String emailSubject, String emailMessage) {
        this.emailSubject = emailSubject;
        this.emailMessage = emailMessage;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public String getEmailMessage() {
        return emailMessage;
    }
}
