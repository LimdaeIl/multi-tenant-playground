package com.arctic.backend.common.exception;


import org.springframework.http.HttpStatus;

public interface ErrorCode {

    HttpStatus status();

    String message();

    default String format(Object... args) {
        return String.format(message(), args);
    }
}
