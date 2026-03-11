package com.arctic.backend.user.exception;

import com.arctic.backend.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    USER_NOT_FOUND_BY_ID(HttpStatus.NOT_FOUND, "회원: 회원을 찾을 수 없습니다. 회원 ID: %s"),
    USER_NOT_FOUND_BY_EMAIL(HttpStatus.NOT_FOUND, "인증/인가: 가입된 회원이 아닙니다. 이메일: %s"),
    USER_PASSWORD_INCORRECT(HttpStatus.FORBIDDEN, "인증/인가: 비밀번호가 틀렸습니다."),

    EMAIL_DUPLICATE(HttpStatus.CONFLICT, "인증/인가: 현재 이메일과 동일합니다."),
    EMAIL_EXISTS(HttpStatus.CONFLICT, "인증/인가: 이미 존재하는 이메일입니다."),

    TENANT_NOT_FOUND(HttpStatus.NOT_FOUND, "테넌트: 존재하지 않는 테넌트입니다. tenantCode=%s"),
    TENANT_INACTIVE(HttpStatus.FORBIDDEN, "테넌트: 비활성 상태의 테넌트입니다. tenantCode=%s"),
    TENANT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "테넌트: 접근 권한이 없습니다. tenantCode=%s"),

    EMAIL_AUTH_LOCKED(HttpStatus.FORBIDDEN, "인증/인가: 이메일 인증 시도 횟수를 초과하여 잠시 차단되었습니다."),
    EMAIL_AUTH_COOLDOWN(HttpStatus.TOO_MANY_REQUESTS, "인증/인가: 이메일 인증 코드는 잠시 후 다시 요청할 수 있습니다."),
    EMAIL_AUTH_EXPIRED(HttpStatus.BAD_REQUEST, "인증/인가: 이메일 인증 코드가 만료되었거나 존재하지 않습니다."),
    EMAIL_AUTH_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "인증/인가: 이메일 인증 코드가 일치하지 않습니다."),
    EMAIL_AUTH_NOT_VERIFIED(HttpStatus.UNAUTHORIZED, "인증/인가: 인증되지 않은 이메일입니다."),
    PHONE_AUTH_NOT_VERIFIED(HttpStatus.UNAUTHORIZED, "인증/인가: 인증되지 않은 휴대전화번호입니다."),

    SOCIAL_EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "소셜 로그인: 동일 이메일로 이미 가입된 계정이 있습니다."),
    SOCIAL_EXCHANGE_CODE_INVALID(HttpStatus.BAD_REQUEST, "소셜 로그인: 교환 코드가 만료되었거나 유효하지 않습니다."),
    TENANT_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 테넌트입니다. tenantCode: %s"),
    TENANT_MEMBERSHIP_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 해당 테넌트에 가입된 사용자입니다. userId: %s, tenantCode: %s"),
    TENANT_PRIMARY_NOT_FOUND(HttpStatus.NOT_FOUND, "기본 테넌트를 찾을 수 없습니다."),
    TENANT_MEMBERSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, "테넌트 멤버십을 찾을 수 없습니다. tenantCode: %s"),
    TENANT_MEMBERSHIP_INACTIVE(HttpStatus.BAD_REQUEST, "활성 상태의 테넌트 멤버십이 아닙니다. tenantCode: %s")


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