package ru.naumen.naumenlocalchat.exception;

/**
 * Ошибка действий с выдачей прав админа
 */
public class AdminException extends Exception {
  public AdminException(String message) {
    super(message);
  }
}
