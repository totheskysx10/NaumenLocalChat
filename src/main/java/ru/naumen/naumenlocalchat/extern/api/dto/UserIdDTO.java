package ru.naumen.naumenlocalchat.extern.api.dto;

public class UserIdDTO {

    public UserIdDTO(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    private Long userId;
}
