package ru.naumen.naumenlocalchat.extern.api.dto;

/**
 * Ответ при возникновении ошибки
 */
public class ErrorDTO {

    /**
     * Сообщение с ошибкой
     */
    private String message;

    public ErrorDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
