package ru.naumen.naumenlocalchat.domain;

import jakarta.persistence.*;

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

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

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
