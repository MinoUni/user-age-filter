package com.example.test.exception;

public class InvalidUserAgeException extends RuntimeException {

  public InvalidUserAgeException(String message) {
    super(message);
  }
}
