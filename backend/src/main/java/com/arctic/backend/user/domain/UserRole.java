package com.arctic.backend.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserRole {
    USER("일반회원"),
    SUPER_ADMIN("플랫폼 관리자");

    private final String description;
}