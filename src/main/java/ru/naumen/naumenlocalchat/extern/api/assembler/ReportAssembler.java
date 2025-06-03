package ru.naumen.naumenlocalchat.extern.api.assembler;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import ru.naumen.naumenlocalchat.domain.Report;
import ru.naumen.naumenlocalchat.extern.api.controller.ReportController;
import ru.naumen.naumenlocalchat.extern.api.dto.ReportDTO;

/**
 * Ассемблер отчётов
 */
@Component
public class ReportAssembler extends RepresentationModelAssemblerSupport<Report, ReportDTO> {

    public ReportAssembler() {
        super(ReportController.class, ReportDTO.class);
    }

    @Override
    public ReportDTO toModel(Report report) {
        ReportDTO reportDTO = instantiateModel(report);
        reportDTO.setId(report.getId());
        reportDTO.setContent(report.getContent());
        reportDTO.setStatus(String.valueOf(report.getStatus()));

        return reportDTO;
    }
}
