package ru.naumen.naumenlocalchat.domain;

import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;

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
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(
            name = "chat_members",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Chat chat = (Chat) o;

        return Objects.equals(id, chat.id) && Objects.equals(members, chat.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, members);
    }
}
