package com.example.test.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

record NewUserDTO(
    @NotBlank(message = "Email can't be blank")
    @Pattern(regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,3}$", message = "Invalid email format")
    String email,

    @NotBlank(message = "First name can't be blank")
    String firstName,

    @NotBlank(message = "Last name can't be blank")
    String lastName,

    @NotNull(message = "Date is required")
    @Past(message = "Date must be earlier than current date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate birthDate,

    String address,

    String phoneNumber) {}
