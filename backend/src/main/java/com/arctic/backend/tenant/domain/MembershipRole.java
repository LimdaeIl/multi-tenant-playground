package com.arctic.backend.tenant.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MembershipRole {
    OWNER("소유자"),
    ADMIN("관리자"),
    VIEWER("조회자");

    private final String description;
}
