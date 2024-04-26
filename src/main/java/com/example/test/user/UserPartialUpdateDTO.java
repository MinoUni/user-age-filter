package com.example.test.user;

import java.time.LocalDate;

public record UserPartialUpdateDTO(
    String email,
    String firstName,
    String lastName,
    LocalDate birthDate,
    String address,
    String phoneNumber) {}
