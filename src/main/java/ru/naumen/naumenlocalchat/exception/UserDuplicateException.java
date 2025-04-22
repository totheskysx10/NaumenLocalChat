package ru.naumen.naumenlocalchat.exception;

/**
 * Ошибка дублирования пользователей
 */
public class UserDuplicateException extends Exception {
    public UserDuplicateException(String message) {
        super(message);
    }
}
