package ru.naumen.naumenlocalchat.extern.api.dto;

import org.springframework.hateoas.RepresentationModel;

import java.util.Set;

public class ChatDTO extends RepresentationModel<ChatDTO> {
    private Long id;
    private Set<Long> memberIds;

    public ChatDTO(Long id, Set<Long> memberIds) {
        this.id = id;
        this.memberIds = memberIds;
    }

    public ChatDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Long> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(Set<Long> memberIds) {
        this.memberIds = memberIds;
    }
}
