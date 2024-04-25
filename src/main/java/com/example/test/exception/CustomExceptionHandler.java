package com.example.test.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      @NonNull MethodArgumentNotValidException e,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status,
      @NonNull WebRequest request) {
    var validationErrors = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> new ValidationErrorDTO(error.getField(), error.getDefaultMessage()))
            .toList();
    var errorDTO = new ErrorDTO(status.value(), "Validation failed");
    errorDTO.setValidationErrors(validationErrors);
    return ResponseEntity.status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .body(errorDTO);
  }

  @ExceptionHandler(InvalidUserAgeException.class)
  public ResponseEntity<ErrorDTO> handleInvalidUserAgeException(InvalidUserAgeException e) {
    return ResponseEntity.status(BAD_REQUEST)
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ErrorDTO(BAD_REQUEST.value(), e.getMessage()));
  }
}
