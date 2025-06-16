package ru.naumen.naumenlocalchat.exception;

/**
 * Ошибка некорректного кода приглашения
 */
public class InvalidCodeException extends Exception {
    public InvalidCodeException(String message) {
        super(message);
    }
}
