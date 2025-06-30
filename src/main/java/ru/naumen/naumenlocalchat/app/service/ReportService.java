package ru.naumen.naumenlocalchat.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.naumen.naumenlocalchat.app.repository.ReportRepository;
import ru.naumen.naumenlocalchat.domain.*;
import ru.naumen.naumenlocalchat.exception.EntityNotFoundException;
import ru.naumen.naumenlocalchat.exception.ReportException;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Сервис отчётов
 */
@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserService userService;
    private final MessageService messageService;
    private final S3Service s3Service;
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    private final Logger log = LoggerFactory.getLogger(ReportService.class);

    /**
     * ID пользователя -> задача генерации отчёта
     */
    private final ConcurrentHashMap<Long, CompletableFuture<Void>> tasks = new ConcurrentHashMap<>();

    private static final String ZIP_NAME_TEMPLATE = "user_%d_%s.zip";
    private static final String JSON_NAME_TEMPLATE = "chat_%d.json";
    private static final String NEW_REPORT_CONTENT = "Report generation is in progress...";
    private static final String ERROR_REPORT_CONTENT = "Report generation error: %s";

    public ReportService(ReportRepository reportRepository, UserService userService, MessageService messageService, S3Service s3Service) {
        this.reportRepository = reportRepository;
        this.userService = userService;
        this.messageService = messageService;
        this.s3Service = s3Service;
    }

    /**
     * Создаёт пустой отчёт и запускает его генерацию
     * @param userId идентификатор пользователя, которому генерируется отчёт
     * @return отчёт
     */
    public Report createReport(Long userId) throws EntityNotFoundException, ReportException {
        User user = userService.getUserById(userId);
        Report report = new Report(NEW_REPORT_CONTENT, user, ReportStatus.GENERATION);
        reportRepository.save(report);

        CompletableFuture<Void> existing = tasks.putIfAbsent(userId, new CompletableFuture<>());
        if (existing != null) {
            throw new ReportException("Отчёт уже создаётся для пользователя " + userId);
        }

        generateReport(report.getId(), userId);
        log.info("Создан отчёт с id {}", report.getId());
        return report;
    }

    /**
     * Получает отчёт по идентификатору
     * @param reportId id отчёта
     * @throws EntityNotFoundException если отчёт не найден
     */
    public Report getReportById(Long reportId) throws EntityNotFoundException {
        Optional<Report> report = reportRepository.findById(reportId);
        return report.orElseThrow(() -> new EntityNotFoundException("Не найден отчёт с id: " + reportId));
    }

    /**
     * Получает отчёты пользователя
     * @param userId id пользователя
     */
    public List<Report> findUserReports(Long userId) throws EntityNotFoundException {
        User user = userService.getUserById(userId);
        return reportRepository.findByUser(user);
    }

    /**
     * Генерирует отчёт для пользователя асинхронно
     * @param reportId id отчёта
     */
    private void generateReport(Long reportId, Long userId) {
        CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {
            Optional<Report> optionalReport = reportRepository.findById(reportId);

            if (optionalReport.isEmpty()) {
                return;
            }

            Report report = optionalReport.get();

            try {
                String reportLink = createZip(userId);
                report.setContent(reportLink);
                report.setStatus(ReportStatus.READY);
                reportRepository.save(report);
                log.info("Завершена генерация отчёта с id {}", report.getId());
            } catch (Exception e) {
                report.setContent(String.format(ERROR_REPORT_CONTENT, e.getMessage()));
                report.setStatus(ReportStatus.ERROR);
                reportRepository.save(report);

                throw new CompletionException(e);
            } finally {
                tasks.remove(userId);
            }
        });

        tasks.put(userId, task);
    }

    /**
     * Создаёт ZIP архив с JSON чатов
     * @param userId id пользователя
     * @return ссылка на архив
     */
    private String createZip(Long userId) throws Exception {
        User user = userService.getUserById(userId);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String zipName = String.format(ZIP_NAME_TEMPLATE, userId, timestamp);
        Path zipPath = Paths.get(zipName);

        try (FileOutputStream fos = new FileOutputStream(zipName);
        ZipOutputStream zos = new ZipOutputStream(fos)) {
            for (Chat chat : user.getChats()) {
                Long chatId = chat.getId();
                List<Message> messages = messageService.findChatMessages(chatId);

                byte[] jsonBytes = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(messages);

                ZipEntry zipEntry = new ZipEntry(String.format(JSON_NAME_TEMPLATE, chatId));
                zos.putNextEntry(zipEntry);
                zos.write(jsonBytes);
                zos.closeEntry();
            }
            log.info("Создан архив с отчётом для пользователя {}", userId);
        }

        byte[] zipBytes = Files.readAllBytes(zipPath);
        MultipartFile multipartFile = new CustomMultipartFile(
                zipPath.getFileName().toString(),
                zipBytes,
                "application/zip"
        );

        String link = s3Service.uploadFile(multipartFile, userId.toString());
        Files.deleteIfExists(zipPath);
        return link;
    }
}
