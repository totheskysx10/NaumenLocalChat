package ru.naumen.naumenlocalchat.extern.api.dto;

import org.springframework.hateoas.RepresentationModel;

import java.util.Set;

public class GroupChatDTO extends RepresentationModel<GroupChatDTO> {
    private Long id;
    private Set<Long> memberIds;
    private String name;
    private Long adminId;
    private Set<Long> blackListIds;

    public GroupChatDTO(Set<Long> blackListIds, Long adminId, String name, Set<Long> memberIds, Long id) {
        this.blackListIds = blackListIds;
        this.adminId = adminId;
        this.name = name;
        this.memberIds = memberIds;
        this.id = id;
    }

    public GroupChatDTO(Long id) {
        this.id = id;
    }

    public Set<Long> getBlackListIds() {
        return blackListIds;
    }

    public void setBlackListIds(Set<Long> blackListIds) {
        this.blackListIds = blackListIds;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Long> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(Set<Long> memberIds) {
        this.memberIds = memberIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
