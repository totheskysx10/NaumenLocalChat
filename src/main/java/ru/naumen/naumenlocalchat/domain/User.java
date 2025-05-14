package ru.naumen.naumenlocalchat.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
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
     * Email
     */
    @Column
    private String email;

    /**
     * Флаг, подтверждён ли email
     */
    @Column
    private boolean emailConfirmed;

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
    @ManyToMany(mappedBy = "members", fetch = FetchType.EAGER)
    private List<Chat> chats;

    public User(String email,
                String password,
                String firstName,
                String lastName) {
        this.email = email;
        this.emailConfirmed = false;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = new ArrayList<>();
        this.chats = new ArrayList<>();
    }

    public User() {
    }

    public Long getId() {
        return id;
    }


    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Chat> getChats() {
        return chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }

    public boolean isEmailConfirmed() {
        return emailConfirmed;
    }

    public void setEmailConfirmed(boolean emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        return emailConfirmed == user.emailConfirmed
                && Objects.equals(id, user.id)
                && Objects.equals(email, user.email)
                && Objects.equals(password, user.password)
                && Objects.equals(firstName, user.firstName)
                && Objects.equals(lastName, user.lastName)
                && Objects.equals(roles, user.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, emailConfirmed, password, firstName, lastName, roles);
    }

    /**
     * Удаляет пользователя из чатов при удалении
     */
    @PreRemove
    private void removeFromChats() {
        for (Chat chat : new HashSet<>(chats)) {
            chat.getMembers().remove(this);
        }
    }
}
