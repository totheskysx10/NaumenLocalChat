package ru.naumen.naumenlocalchat.exception;

/**
 * Ошибка дублирования файла в S3
 */
public class FileDuplicateException extends Exception {
    public FileDuplicateException(String message) {
        super(message);
    }
}
