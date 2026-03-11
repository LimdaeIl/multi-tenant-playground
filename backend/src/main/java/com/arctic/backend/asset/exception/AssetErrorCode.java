package com.arctic.backend.asset.exception;

import com.arctic.backend.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AssetErrorCode implements ErrorCode {

    ASSET_NOT_FOUND(HttpStatus.NOT_FOUND, "자산을 찾을 수 없습니다. assetId: %s"),
    ASSET_CODE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 자산 코드입니다. tenantId: %s, code: %s"),
    ASSET_ACCESS_DENIED(HttpStatus.FORBIDDEN, "자산 관리 권한이 없습니다."),
    ASSET_CODE_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 자산 코드입니다.");

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