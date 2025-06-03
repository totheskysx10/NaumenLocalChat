package ru.naumen.naumenlocalchat.exception;

/**
 * Ошибка создания некорректного чата или некорректного действия с чатом
 */
public class InvalidChatException extends Exception {
  public InvalidChatException(String message) {
    super(message);
  }
}
