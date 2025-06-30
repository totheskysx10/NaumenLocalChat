package ru.naumen.naumenlocalchat.domain;

import jakarta.persistence.*;

import java.util.Objects;

/**
 * Отчёт
 */
@Entity
@Table(name = "reports")
public class Report {

    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Содержимое
     */
    @Column(columnDefinition = "text")
    private String content;

    /**
     * Статус генерации
     */
    @Column
    @Enumerated(EnumType.STRING)
    private ReportStatus status;
    /**
     * Пользователь, которому принадлежит отчёт
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Report() {
    }

    public Report(String content, User user, ReportStatus status) {
        this.content = content;
        this.user = user;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return Objects.equals(content, report.content) && status == report.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, status);
    }
}
