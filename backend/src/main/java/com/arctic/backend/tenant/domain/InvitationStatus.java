package com.arctic.backend.tenant.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InvitationStatus {
    PENDING("대기"),
    ACCEPTED("수락"),
    EXPIRED("만료"),
    CANCELED("취소");

    private final String description;
}