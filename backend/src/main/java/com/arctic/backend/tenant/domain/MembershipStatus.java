package com.arctic.backend.tenant.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MembershipStatus {
    INVITED("초대됨"),
    ACTIVE("활성"),
    SUSPENDED("중지"),
    LEFT("탈퇴");

    private final String description;
}