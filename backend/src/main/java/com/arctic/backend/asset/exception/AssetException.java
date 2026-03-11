package com.arctic.backend.asset.exception;

import com.arctic.backend.common.exception.AppException;
import com.arctic.backend.common.exception.ErrorCode;

public class AssetException extends AppException {

    public AssetException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AssetException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }


}
