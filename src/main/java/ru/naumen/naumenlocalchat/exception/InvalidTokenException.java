package ru.naumen.naumenlocalchat.exception;

/**
 * Ошибка, когда токен невалиден и из-за этого невозможно выполнить операцию
 */
public class InvalidTokenException extends Exception {
    public InvalidTokenException(String message) {
        super(message);
    }
}
