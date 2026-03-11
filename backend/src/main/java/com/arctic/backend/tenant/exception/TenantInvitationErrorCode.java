package com.arctic.backend.tenant.exception;

import com.arctic.backend.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TenantInvitationErrorCode implements ErrorCode {

    INVITATION_NOT_FOUND(HttpStatus.NOT_FOUND, "초대를 찾을 수 없습니다."),
    INVITATION_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 대기 중인 초대가 존재합니다. email: %s"),
    INVITATION_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 초대입니다."),
    INVITATION_NOT_PENDING(HttpStatus.BAD_REQUEST, "처리할 수 없는 초대 상태입니다."),
    INVITATION_EMAIL_MISMATCH(HttpStatus.BAD_REQUEST, "초대 이메일과 계정 이메일이 일치하지 않습니다."),
    INVITATION_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 초대 토큰입니다."),
    TENANT_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 테넌트입니다. tenantCode: %s"),
    TENANT_MEMBERSHIP_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 해당 테넌트에 가입된 사용자입니다. userId: %s, tenantCode: %s"),

    ;

    private final HttpStatus status;
    private final String message;

    @Override
    public HttpStatus status() {
        return status;
    }

    @Override
    public String message() {
        return message;
    }
}