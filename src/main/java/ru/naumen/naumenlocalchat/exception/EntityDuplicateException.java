package ru.naumen.naumenlocalchat.exception;

/**
 * Ошибка дублирования
 */
public class EntityDuplicateException extends Exception {
    public EntityDuplicateException(String message) {
        super(message);
    }
}
