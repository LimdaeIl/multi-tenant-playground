package com.arctic.backend.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserRole {
    USER("일반회원"),
    ADMIN("관리자"),
    MANAGER("매니저");

    private final String description;

}
