package ru.naumen.naumenlocalchat.exception;

/**
 * Ошибка дублирования при сохранении сущности или при заполнении списков
 */
public class EntityDuplicateException extends Exception {
    public EntityDuplicateException(String message) {
        super(message);
    }
}
