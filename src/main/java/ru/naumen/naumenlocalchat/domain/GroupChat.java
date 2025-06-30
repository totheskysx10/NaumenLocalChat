package ru.naumen.naumenlocalchat.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import ru.naumen.naumenlocalchat.app.serializer.MembersSerializer;
import ru.naumen.naumenlocalchat.app.serializer.UserIdSerializer;

import java.util.HashSet;
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
    @JsonSerialize(using = UserIdSerializer.class)
    private User admin;

    /**
     * Заблокированные участники (чёрный список)
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "groupchat_blacklist",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonSerialize(using = MembersSerializer.class)
    private Set<User> chatBlackList;

    public GroupChat(Set<User> members, String name) {
        super(members);
        this.name = name;
        this.chatBlackList = new HashSet<>();
    }

    public GroupChat() {

    }

    public String getName() {
        return name;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public Set<User> getChatBlackList() {
        return chatBlackList;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        GroupChat groupChat = (GroupChat) o;

        return Objects.equals(name, groupChat.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }
}
