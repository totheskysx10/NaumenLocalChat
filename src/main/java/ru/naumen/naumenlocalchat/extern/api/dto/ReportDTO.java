package ru.naumen.naumenlocalchat.extern.api.dto;

import org.springframework.hateoas.RepresentationModel;

public class ReportDTO extends RepresentationModel<ReportDTO> {

    private Long id;
    private String content;
    private String status;

    public ReportDTO(Long id, String content, String status) {
        this.id = id;
        this.content = content;
        this.status = status;
    }

    public ReportDTO() {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
