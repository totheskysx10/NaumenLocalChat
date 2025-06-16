package ru.naumen.naumenlocalchat.extern.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.naumen.naumenlocalchat.app.service.ReportService;
import ru.naumen.naumenlocalchat.domain.Report;
import ru.naumen.naumenlocalchat.exception.EntityNotFoundException;
import ru.naumen.naumenlocalchat.exception.ReportException;
import ru.naumen.naumenlocalchat.extern.api.assembler.ReportAssembler;
import ru.naumen.naumenlocalchat.extern.api.dto.ReportDTO;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;
    private final ReportAssembler reportAssembler;

    public ReportController(ReportService reportService, ReportAssembler reportAssembler) {
        this.reportService = reportService;
        this.reportAssembler = reportAssembler;
    }

    @PostMapping("/generate")
    public ResponseEntity<ReportDTO> createReport(@RequestParam Long userId) throws EntityNotFoundException, ReportException {
        Report report = reportService.createReport(userId);
        return ResponseEntity.ok(reportAssembler.toModel(report));
    }

    @GetMapping
    public ResponseEntity<ReportDTO> getReportById(@RequestParam Long reportId) throws EntityNotFoundException {
        Report report = reportService.getReportById(reportId);
        return ResponseEntity.ok(reportAssembler.toModel(report));
    }

    @GetMapping("/user-reports")
    public ResponseEntity<List<ReportDTO>> getUserReports(@RequestParam Long userId) throws EntityNotFoundException {
        List<ReportDTO> reports = reportService.findUserReports(userId).stream()
                .map(reportAssembler::toModel)
                .toList();

        if (reports.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(reports);
    }
}