package ru.naumen.naumenlocalchat.extern.api.dto;

import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

public class MessageDTO extends RepresentationModel<MessageDTO> {

    private Long id;
    private Long senderId;
    private String content;
    private LocalDateTime timestamp;
    private Long chatId;

    public MessageDTO(Long id, Long senderId, String content, LocalDateTime timestamp, Long chatId) {
        this.id = id;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
        this.chatId = chatId;
    }

    public MessageDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
