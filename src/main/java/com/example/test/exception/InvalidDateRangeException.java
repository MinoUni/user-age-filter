package com.example.test.exception;

public class InvalidDateRangeException extends RuntimeException {

  public InvalidDateRangeException(String message) {
    super(message);
  }
}
