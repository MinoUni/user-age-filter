package com.example.test.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record UserFullUpdate(
    @NotBlank(message = BLANK_PROPERTY_MESSAGE)
    @Pattern(regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,3}$", message = "Invalid email format")
    String email,

    @NotBlank(message = BLANK_PROPERTY_MESSAGE)
    String firstName,

    @NotBlank(message = BLANK_PROPERTY_MESSAGE)
    String lastName,

    @NotNull(message = "Property is required")
    @Past(message = "Date must be earlier than current date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate birthDate,

    @NotBlank(message = BLANK_PROPERTY_MESSAGE)
    String address,

    @NotBlank(message = BLANK_PROPERTY_MESSAGE)
    String phoneNumber) {

  private static final String BLANK_PROPERTY_MESSAGE = "Property can't be blank";
}
