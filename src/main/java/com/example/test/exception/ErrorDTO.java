package com.example.test.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ErrorDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ssa")
    private LocalDateTime timestamp;

    private int statusCode;

    private String errorMessage;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ValidationErrorDTO> validationErrors;

    public ErrorDTO(int statusCode, String errorMessage) {
        this.timestamp = LocalDateTime.now();
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }
}
