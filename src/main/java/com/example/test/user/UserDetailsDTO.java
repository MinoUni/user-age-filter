package com.example.test.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class UserDetailsDTO {

  private Integer id;

  private String email;

  private String firstName;

  private String lastName;

  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate birthDate;

  private String address;

  private String phoneNumber;
}
