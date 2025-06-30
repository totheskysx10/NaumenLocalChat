package ru.naumen.naumenlocalchat.exception;

/**
 * Исключение аутентификации
 */
public class AuthException extends Exception {
    public AuthException(String message) {
        super(message);
    }
}
