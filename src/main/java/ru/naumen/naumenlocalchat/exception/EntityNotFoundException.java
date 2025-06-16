package ru.naumen.naumenlocalchat.exception;

/**
 * Ошибка, когда сущность не найдена
 */
public class EntityNotFoundException extends Exception {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
