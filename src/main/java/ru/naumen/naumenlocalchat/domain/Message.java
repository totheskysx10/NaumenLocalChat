package ru.naumen.naumenlocalchat.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Сообщение
 */
@Entity
@Table(name = "messages")
public class Message {

    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Отправитель
     */
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    /**
     * Текст сообщения
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * Время и дата отправки
     */
    @Column
    private LocalDateTime timestamp;

    /**
     * Чат, куда отправлено сообщение
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    public Message(User sender, String content) {
        this.sender = sender;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public Message() {

    }

    public Chat getChat() {
        return chat;
    }

    public Long getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Message message = (Message) o;

        return Objects.equals(id, message.id)
                && Objects.equals(sender, message.sender)
                && Objects.equals(content, message.content)
                && Objects.equals(timestamp, message.timestamp)
                && Objects.equals(chat, message.chat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sender, content, timestamp, chat);
    }
}
