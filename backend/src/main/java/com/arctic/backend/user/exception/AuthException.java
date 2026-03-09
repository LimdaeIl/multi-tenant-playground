package com.arctic.backend.user.exception;


import com.arctic.backend.common.exception.AppException;
import com.arctic.backend.common.exception.ErrorCode;

public class AuthException extends AppException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

}
