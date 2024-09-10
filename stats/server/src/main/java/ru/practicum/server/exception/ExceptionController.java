package ru.practicum.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolationException(ConstraintViolationException e) {
        String reason = "Incorrectly made request.";
        String message = e.getMessage();
        return new ApiError(HttpStatus.BAD_REQUEST.getReasonPhrase(), reason, message, prepareResponseTimeStamp());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(ValidationException e) {
        String reason = "Incorrectly made request.";
        String message = e.getMessage();
        return new ApiError(HttpStatus.BAD_REQUEST.getReasonPhrase(), reason, message, prepareResponseTimeStamp());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String reason = "Incorrectly made request.";
        String message = e.getMessage();
        return new ApiError(HttpStatus.BAD_REQUEST.getReasonPhrase(), reason, message, prepareResponseTimeStamp());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(final Exception e) {
        String reason = "Something went wrong";
        String message = e.getMessage();
        log.error("Exception: {}", e.getClass().getSimpleName());
        log.error("Error: ", e);
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                reason, message, prepareResponseTimeStamp());
    }

    private String prepareResponseTimeStamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
