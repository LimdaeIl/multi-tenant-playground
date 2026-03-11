package com.arctic.backend.tenant.exception;

import com.arctic.backend.common.exception.AppException;
import com.arctic.backend.common.exception.ErrorCode;

public class TenantInvitationException extends AppException {

    public TenantInvitationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public TenantInvitationException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
}