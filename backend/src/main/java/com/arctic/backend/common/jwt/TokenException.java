package com.arctic.backend.common.jwt;


import com.arctic.backend.common.exception.AppException;
import com.arctic.backend.common.exception.ErrorCode;

public class TokenException extends AppException {

    public TokenException(ErrorCode errorCode) {
        super(errorCode);
    }

    public TokenException(JwtErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
}
