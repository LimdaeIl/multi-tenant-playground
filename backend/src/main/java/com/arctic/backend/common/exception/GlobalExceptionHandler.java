package com.arctic.backend.common.exception;


import com.arctic.backend.common.response.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j(topic = "GlobalExceptionHandler")
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String PROBLEM_BASE_URI = "about:blank";

    // AppException (도메인 에러)
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleApp(
            AppException ex,
            HttpServletRequest request
    ) {
        ErrorCode code = ex.getErrorCode();
        String title = ((Enum<?>) code).name();

        return ResponseEntity.status(code.status())
                .body(ErrorResponse.problem(
                        problemType(title),
                        title,
                        code.status(),
                        ex.getMessage(),
                        request.getRequestURI(),
                        title,
                        null
                ));
    }

    //  @Valid Body 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBody(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<ErrorResponse.FieldError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> ErrorResponse.FieldError.of(
                        e.getField(),
                        e.getDefaultMessage()))
                .toList();

        AppErrorCode code = AppErrorCode.INVALID_INPUT_VALUE;
        String title = code.name();

        return ResponseEntity.badRequest()
                .body(ErrorResponse.problem(
                        problemType(title),
                        title,
                        HttpStatus.BAD_REQUEST,
                        code.message(),
                        request.getRequestURI(),
                        title,
                        errors
                ));
    }

    // @RequestParam / @PathVariable 검증 실패
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        List<ErrorResponse.FieldError> errors = ex.getConstraintViolations().stream()
                .map(v -> {
                    String path = v.getPropertyPath().toString();
                    String field = path.contains(".")
                            ? path.substring(path.lastIndexOf('.') + 1)
                            : path;
                    return ErrorResponse.FieldError.of(field, v.getMessage());
                })
                .toList();

        AppErrorCode code = AppErrorCode.INVALID_INPUT_VALUE;
        String title = code.name();

        return ResponseEntity.badRequest()
                .body(ErrorResponse.problem(
                        problemType(title),
                        title,
                        HttpStatus.BAD_REQUEST,
                        code.message(),
                        request.getRequestURI(),
                        title,
                        errors
                ));
    }

    // 잘못된 JSON
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        AppErrorCode code = AppErrorCode.INVALID_INPUT_VALUE;
        String title = code.name();

        return ResponseEntity.badRequest()
                .body(ErrorResponse.problem(
                        problemType(title),
                        title,
                        HttpStatus.BAD_REQUEST,
                        code.message(),
                        request.getRequestURI(),
                        title,
                        null
                ));
    }


    // HTTP 메서드 오류
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request
    ) {
        AppErrorCode code = AppErrorCode.METHOD_NOT_ALLOWED;
        String title = code.name();

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorResponse.problem(
                        problemType(title),
                        title,
                        HttpStatus.METHOD_NOT_ALLOWED,
                        code.message(),
                        request.getRequestURI(),
                        title,
                        null
                ));
    }

    // JPA 엔티티 미존재
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request
    ) {
        AppErrorCode code = AppErrorCode.ENTITY_NOT_FOUND;
        String title = code.name();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.problem(
                        problemType(title),
                        title,
                        HttpStatus.NOT_FOUND,
                        code.message(),
                        request.getRequestURI(),
                        title,
                        null
                ));
    }

    //최후의 방어선
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAny(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("처리되지 않은 예외", ex);

        AppErrorCode code = AppErrorCode.INTERNAL_SERVER_ERROR;
        String title = code.name();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.problem(
                        problemType(title),
                        title,
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        code.message(),
                        request.getRequestURI(),
                        title,
                        null
                ));
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            Exception ex,
            HttpServletRequest request
    ) {
        AppErrorCode code = AppErrorCode.ACCESS_DENIED;
        String title = code.name();

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.problem(
                        problemType(title),
                        title,
                        HttpStatus.FORBIDDEN,
                        "권한이 없습니다.",
                        request.getRequestURI(),
                        title,
                        null
                ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(
            AuthenticationException ex,
            HttpServletRequest request
    ) {
        AppErrorCode code = AppErrorCode.UNAUTHORIZED;
        String title = code.name();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.problem(
                        problemType(title),
                        title,
                        HttpStatus.UNAUTHORIZED,
                        "인증이 필요합니다.",
                        request.getRequestURI(),
                        title,
                        null
                ));
    }

    private String problemType(String title) {
        return PROBLEM_BASE_URI + title.toLowerCase().replace('_', '-');
    }
}
