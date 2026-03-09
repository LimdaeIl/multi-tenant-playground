package com.arctic.backend.user.exception;

import com.arctic.backend.common.exception.AppException;
import com.arctic.backend.common.exception.ErrorCode;

public class UserException extends AppException {

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }


}
