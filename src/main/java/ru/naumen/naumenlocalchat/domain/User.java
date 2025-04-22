package ru.naumen.naumenlocalchat.domain;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

/**
 * Пользователь
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Уникальный юзернейм
     */
    @Column(unique = true)
    private String username;

    /**
     * Шифрованный пароль
     */
    @Column
    private String password;

    /**
     * Имя
     */
    @Column
    private String firstName;

    /**
     * Фамилия
     */
    @Column
    private String lastName;

    /**
     * Роли
     */
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Role> roles;

    /**
     * Чаты пользователя
     */
    @ManyToMany(mappedBy = "members")
    private List<Chat> chats;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        return Objects.equals(id, user.id)
                && Objects.equals(username, user.username)
                && Objects.equals(password, user.password)
                && Objects.equals(firstName, user.firstName)
                && Objects.equals(lastName, user.lastName)
                && Objects.equals(roles, user.roles)
                && Objects.equals(chats, user.chats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, firstName, lastName, roles, chats);
    }
}
