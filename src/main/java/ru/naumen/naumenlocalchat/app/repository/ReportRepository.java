package ru.naumen.naumenlocalchat.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.naumen.naumenlocalchat.domain.Report;
import ru.naumen.naumenlocalchat.domain.User;

import java.util.List;

/**
 * Репозиторий отчётов
 */
public interface ReportRepository extends JpaRepository<Report, Long> {

    /**
     * Ищет отчёты пользователя
     * @param user пользователь
     */
    List<Report> findByUser(User user);
}
