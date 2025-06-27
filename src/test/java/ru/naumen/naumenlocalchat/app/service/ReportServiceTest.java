package ru.naumen.naumenlocalchat.app.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import ru.naumen.naumenlocalchat.app.repository.ReportRepository;
import ru.naumen.naumenlocalchat.domain.*;
import ru.naumen.naumenlocalchat.exception.EntityNotFoundException;
import ru.naumen.naumenlocalchat.exception.FileDuplicateException;
import ru.naumen.naumenlocalchat.exception.ReportException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Тесты сервиса отчётов
 */
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserService userService;

    @Mock
    private MessageService messageService;

    @Mock
    private S3Service s3Service;

    private ReportService reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reportService = new ReportService(reportRepository, userService, messageService, s3Service);
    }

    /**
     * Тест создания отчёта
     */
    @Test
    void createReport() throws EntityNotFoundException, ReportException {
        User user = new User("user1@test.com", "pass1", "f1", "l1");

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(reportRepository.save(Mockito.any(Report.class))).thenAnswer(invocation -> {
            Report report = invocation.getArgument(0);
            report.setId(1L);
            return report;
        });

        Report report = reportService.createReport(1L);

        Assertions.assertEquals(1L, report.getId());
        Assertions.assertEquals(ReportStatus.GENERATION, report.getStatus());
        Assertions.assertEquals("Report generation is in progress...", report.getContent());
    }

    /**
     * Тест создания отчёта, если он уже создаётся
     */
    @Test
    void createReportAlreadyCreating() throws EntityNotFoundException, ReportException {
        User user = new User("user1@test.com", "pass1", "f1", "l1");

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(reportRepository.save(Mockito.any(Report.class))).thenAnswer(invocation -> {
            Report report = invocation.getArgument(0);
            report.setId(1L);
            return report;
        });

        reportService.createReport(1L);

        Exception e = Assertions.assertThrows(ReportException.class, () -> reportService.createReport(1L));
        Assertions.assertEquals("Отчёт уже создаётся для пользователя 1", e.getMessage());
    }

    /**
     * Тест создания отчёта, если он уже создан раннее
     */
    @Test
    void createSecondReport() throws EntityNotFoundException, ReportException, InterruptedException {
        User user = new User("user1@test.com", "pass1", "f1", "l1");

        Mockito.when(userService.getUserById(1L)).thenReturn(user);

        AtomicLong idCounter = new AtomicLong(1);
        Mockito.when(reportRepository.save(Mockito.any(Report.class))).thenAnswer(invocation -> {
            Report report = invocation.getArgument(0);
            report.setId(idCounter.getAndIncrement());
            return report;
        });

        Report report1 = reportService.createReport(1L);
        Thread.sleep(100);
        Report report2 = reportService.createReport(2L);

        Assertions.assertEquals(1L, report1.getId());
        Assertions.assertEquals(2L, report2.getId());
        Assertions.assertEquals(ReportStatus.GENERATION, report2.getStatus());
        Assertions.assertEquals("Report generation is in progress...", report2.getContent());
    }

    /**
     * Тест генерации отчёта
     */
    @Test
    void generateReport() throws EntityNotFoundException, InterruptedException, ReportException, FileDuplicateException, IOException {
        User user = new User("user1@test.com", "pass1", "f1", "l1");
        user.setId(1L);
        Chat chat1 = new Chat();
        chat1.setId(1L);
        user.getChats().add(chat1);
        Message message = new Message(user, "message", chat1);

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Report reportToSave = new Report("Report generation is in progress...", user, ReportStatus.GENERATION);
        reportToSave.setId(1L);
        Mockito.when(reportRepository.save(Mockito.eq(reportToSave))).thenAnswer(invocation -> {
            Report report = invocation.getArgument(0);
            report.setId(1L);
            return report;
        });
        Mockito.when(messageService.findChatMessages(1L)).thenReturn(new ArrayList<>(List.of(message)));
        Mockito.when(s3Service.uploadFile(Mockito.any(CustomMultipartFile.class), Mockito.anyString())).thenReturn("link");
        Mockito.when(reportRepository.findById(1L)).thenReturn(Optional.of(reportToSave));

        reportService.createReport(1L);
        Thread.sleep(100);
        Report report = reportRepository.findById(1L).get();

        Assertions.assertEquals(1L, report.getId());
        Assertions.assertEquals(ReportStatus.READY, report.getStatus());
        Assertions.assertEquals("link", report.getContent());
    }

    /**
     * Тест получения отчёта по id
     */
    @Test
    void getReportById() throws EntityNotFoundException {
        User user = new User("user1@test.com", "pass1", "f1", "l1");
        Report report = new Report("Report generation is in progress...", user, ReportStatus.GENERATION);

        Mockito.when(reportRepository.findById(1L)).thenReturn(Optional.of(report));

        Report foundReport = reportService.getReportById(1L);

        Assertions.assertEquals(report, foundReport);
    }

    /**
     * Тест получения отчёта по id, если не найден
     */
    @Test
    void getReportByIdNotFound() {
        Mockito.when(reportRepository.findById(1L)).thenReturn(Optional.empty());

        Exception e = Assertions.assertThrows(EntityNotFoundException.class, () -> reportService.getReportById(1L));
        Assertions.assertEquals("Не найден отчёт с id: 1", e.getMessage());
    }

    /**
     * Тест получения отчётов пользователя
     */
    @Test
    void findUserReports() throws EntityNotFoundException {
        User user = new User("user1@test.com", "pass1", "f1", "l1");
        Report report = new Report("Report generation is in progress...", user, ReportStatus.GENERATION);

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(reportRepository.findByUser(user)).thenReturn(List.of(report));

        List<Report> foundReports = reportService.findUserReports(1L);

        Assertions.assertEquals(1, foundReports.size());
        Assertions.assertEquals(report, foundReports.getFirst());
    }
}