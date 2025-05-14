package ru.naumen.naumenlocalchat.exception;

/**
 * Ошибка дублирования сущности при её сохранении или при заполнении списков
 */
public class EntityDuplicateException extends Exception {
    public EntityDuplicateException(String message) {
        super(message);
    }
}
