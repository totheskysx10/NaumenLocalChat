package ru.naumen.naumenlocalchat.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import ru.naumen.naumenlocalchat.app.serializer.MembersSerializer;
import ru.naumen.naumenlocalchat.app.serializer.UserIdSerializer;

import java.util.*;

/**
 * Чат
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "chats")
public class Chat {

    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Участники чата
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "chat_members",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonSerialize(using = MembersSerializer.class)
    private Set<User> members;

    public Chat() {
        this.members = new HashSet<>();
    }

    public Chat(Set<User> members) {
        this.members = members;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<User> getMembers() {
        return members;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Chat chat = (Chat) o;

        return Objects.equals(id, chat.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
