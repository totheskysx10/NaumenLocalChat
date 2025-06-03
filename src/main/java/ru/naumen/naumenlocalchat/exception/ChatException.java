package ru.naumen.naumenlocalchat.exception;

/**
 * Ошибка создания некорректного чата или некорректного действия с чатом
 */
public class ChatException extends Exception {
  public ChatException(String message) {
    super(message);
  }
}
