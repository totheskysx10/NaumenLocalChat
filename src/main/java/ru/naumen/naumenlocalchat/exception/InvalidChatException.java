package ru.naumen.naumenlocalchat.exception;

/**
 * Исключение, когда чат некорректен
 */
public class InvalidChatException extends Exception {
    public InvalidChatException(String message) {
        super(message);
    }
}
