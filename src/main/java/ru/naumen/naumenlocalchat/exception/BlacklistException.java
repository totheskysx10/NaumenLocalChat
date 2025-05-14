package ru.naumen.naumenlocalchat.exception;

/**
 * Исключение работы с чёрным списком группового чата
 */
public class BlacklistException extends Exception {
    public BlacklistException(String message) {
        super(message);
    }
}
