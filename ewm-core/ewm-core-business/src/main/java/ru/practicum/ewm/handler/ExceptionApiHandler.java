package ru.practicum.ewm.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.common.ApiError;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.exception.IntegrityException;
import ru.practicum.ewm.exception.AccessException;
import ru.practicum.ewm.exception.ObjectNotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionApiHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleBadRequestException(ValidationException exception) {
        String reason = "For the requested operation the conditions are not met.";
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        return ResponseEntity
                .status(badRequest)
                .body(formApiError(exception, badRequest, reason));
    }

    @ExceptionHandler(IntegrityException.class)
    public ResponseEntity<ApiError> handleConflictException(IntegrityException exception) {
        String reason = "Integrity constraint has been violated.";
        HttpStatus conflict = HttpStatus.CONFLICT;
        return ResponseEntity
                .status(conflict)
                .body(formApiError(exception, conflict, reason));
    }

    @ExceptionHandler(AccessException.class)
    public ResponseEntity<ApiError> handleForbiddenException(AccessException exception) {
        String reason = "For the requested operation an access misconfiguration caused by on the client-side.";
        HttpStatus forbidden = HttpStatus.FORBIDDEN;
        return ResponseEntity
                .status(forbidden)
                .body(formApiError(exception, forbidden, reason));
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(ObjectNotFoundException exception) {
        String reason = "The required object was not found.";
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        return ResponseEntity
                .status(notFound)
                .body(formApiError(exception, notFound, reason));
    }

    private ApiError formApiError(Exception exception, HttpStatus httpStatus, String reason) {
        return ApiError.builder()
                .errors(getStackTraceElements(exception))
                .message(exception.getMessage())
                .reason(reason)
                .status(httpStatus.toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    private List<String> getStackTraceElements(Exception exception) {
        return Arrays.stream(exception.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());
    }
}