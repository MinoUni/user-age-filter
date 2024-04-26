package com.example.test.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      @NonNull MethodArgumentNotValidException e,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status,
      @NonNull WebRequest request) {
    var validationErrors =
        e.getBindingResult().getFieldErrors().stream()
            .map(error -> new ValidationErrorDTO(error.getField(), error.getDefaultMessage()))
            .toList();
    var errorDTO = new ErrorDTO(status.value(), "Validation failed");
    errorDTO.setValidationErrors(validationErrors);
    return ResponseEntity.status(status).contentType(APPLICATION_JSON).body(errorDTO);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
    var constraintViolations =
        e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).toList();
    return ResponseEntity.status(BAD_REQUEST)
        .contentType(APPLICATION_JSON)
        .body(Map.of("errors", constraintViolations));
  }

  @ExceptionHandler(InvalidDateRangeException.class)
  public ResponseEntity<ErrorDTO> handleInvalidDateRangeException(InvalidDateRangeException e) {
    return ResponseEntity.status(BAD_REQUEST)
            .contentType(APPLICATION_JSON)
            .body(new ErrorDTO(BAD_REQUEST.value(), e.getMessage()));
  }

  @ExceptionHandler(InvalidUserAgeException.class)
  public ResponseEntity<ErrorDTO> handleInvalidUserAgeException(InvalidUserAgeException e) {
    return ResponseEntity.status(BAD_REQUEST)
        .contentType(APPLICATION_JSON)
        .body(new ErrorDTO(BAD_REQUEST.value(), e.getMessage()));
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorDTO> handleUserNotFoundException(UserNotFoundException e) {
    return ResponseEntity.status(NOT_FOUND)
        .contentType(APPLICATION_JSON)
        .body(new ErrorDTO(NOT_FOUND.value(), e.getMessage()));
  }
}
