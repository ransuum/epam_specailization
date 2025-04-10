package org.epam.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeParseException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralExceptions(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", ex);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatusException(ResponseStatusException ex) {
        return buildResponse((HttpStatus) ex.getStatusCode(), ex.getReason(), ex);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ApiError> handleDateTimeParseException(DateTimeParseException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Date format is not correct", ex);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Resource not found", ex);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(BadCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Authentication failed", ex);
    }

    @ExceptionHandler(CredentialException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(CredentialException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Authentication failed", ex);
    }

    @ExceptionHandler(PermissionException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(PermissionException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Permission denied", ex);
    }

    private ResponseEntity<ApiError> buildResponse(HttpStatus status, String message, Exception ex) {
        return ResponseEntity.status(status)
                .body(new ApiError(status.value(), message, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(HttpStatus.BAD_REQUEST.value(), "Validation failed", String.join(", ", errors)));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();

        return buildResponse("Constraint violation", errors);
    }

    private ResponseEntity<ApiError> buildResponse(String message, List<String> errors) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(HttpStatus.BAD_REQUEST.value(), message, String.join(", ", errors)));
    }
}
