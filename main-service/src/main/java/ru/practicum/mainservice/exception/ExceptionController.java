package ru.practicum.mainservice.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class ExceptionController {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        String reason = "Incorrectly made request.";
        String message = e.getMessage();
        return new ApiError(HttpStatus.BAD_REQUEST.getReasonPhrase(), reason, message, prepareResponseTimeStamp());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolationException(final ConstraintViolationException e) {
        String reason = "Incorrectly made request.";
        String message = e.getMessage();
        return new ApiError(HttpStatus.BAD_REQUEST.getReasonPhrase(), reason, message, prepareResponseTimeStamp());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgumentException(final IllegalArgumentException e) {
        String reason = "Incorrectly made request.";
        String message = e.getMessage();
        return new ApiError(HttpStatus.BAD_REQUEST.getReasonPhrase(), reason, message, prepareResponseTimeStamp());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        String reason = e.getBody().getDetail();
        String message = "Field: " + Objects.requireNonNull(e.getBindingResult().getFieldError()).getField() +
                " error: " + e.getBindingResult().getFieldError().getDefaultMessage();
        return new ApiError(HttpStatus.BAD_REQUEST.getReasonPhrase(), reason, message, prepareResponseTimeStamp());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        String reason = "Incorrectly made request.";
        String message = e.getMessage();
        log.error("e: ", e);
        return new ApiError(HttpStatus.NOT_FOUND.getReasonPhrase(), reason, message, prepareResponseTimeStamp());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final DataIntegrityViolationException e) {
        String reason = "Integrity constraint has been violated";
        String message = "could not execute statement; constraint " + e.getMostSpecificCause();
        return new ApiError(HttpStatus.CONFLICT.getReasonPhrase(), reason, message, prepareResponseTimeStamp());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final ConflictException e) {
        String reason = "For the requested operation the conditions are not met.";
        String message = e.getMessage();
        return new ApiError(HttpStatus.CONFLICT.getReasonPhrase(), reason, message, prepareResponseTimeStamp());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final ValidationException e) {
        String reason = "Incorrectly made request.";
        String message = e.getMessage();
        return new ApiError(HttpStatus.BAD_REQUEST.getReasonPhrase(), reason, message, prepareResponseTimeStamp());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleNotAllowedException(final NotAllowedException e) {
        String reason = "Not allowed";
        String message = e.getMessage();
        return new ApiError(HttpStatus.FORBIDDEN.getReasonPhrase(), reason, message, prepareResponseTimeStamp());
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
        return LocalDateTime.now().format(DATE_FORMATTER);
    }
}
