package ru.naumen.naumenlocalchat.domain;

import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Групповой чат
 */
@Entity
@Table(name = "group_chats")
public class GroupChat extends Chat {

    /**
     * Название чата
     */
    @Column(nullable = false)
    private String name;

    /**
     * Админ чата
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    /**
     * Заблокированные участники (чёрный список)
     */
    @ManyToMany
    @JoinTable(
            name = "groupchat_blacklist",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> chatBlackList;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        GroupChat groupChat = (GroupChat) o;

        return Objects.equals(name, groupChat.name)
                && Objects.equals(admin, groupChat.admin)
                && Objects.equals(chatBlackList, groupChat.chatBlackList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, admin, chatBlackList);
    }
}
