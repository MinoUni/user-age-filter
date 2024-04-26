package com.example.test.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class UserDTO {

  private Integer id;

  @NotBlank(
      groups = {UserDTO.Create.class, UserDTO.FullUpdate.class},
      message = "Property can't be blank")
  @Pattern(
      groups = {UserDTO.Create.class, UserDTO.FullUpdate.class, UserDTO.PartialUpdate.class},
      regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,3}$",
      message = "Invalid email format")
  private String email;

  @NotBlank(
      groups = {UserDTO.Create.class, UserDTO.FullUpdate.class},
      message = "Property can't be blank")
  private String firstName;

  @NotBlank(
      groups = {UserDTO.Create.class, UserDTO.FullUpdate.class},
      message = "Property can't be blank")
  private String lastName;

  @NotNull(
      groups = {UserDTO.Create.class, UserDTO.FullUpdate.class},
      message = "Property is required")
  @Past(
      groups = {UserDTO.Create.class, UserDTO.FullUpdate.class, UserDTO.PartialUpdate.class},
      message = "Date must be earlier than current date")
  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate birthDate;

  @NotBlank(
      groups = {UserDTO.FullUpdate.class},
      message = "Property can't be blank")
  private String address;

  @NotBlank(
      groups = {UserDTO.FullUpdate.class},
      message = "Property can't be blank")
  private String phoneNumber;

  public UserDTO(
          String email,
          String firstName,
          String lastName,
          LocalDate birthDate,
          String address,
          String phoneNumber) {
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.birthDate = birthDate;
    this.address = address;
    this.phoneNumber = phoneNumber;
  }

  public interface Create {}

  public interface FullUpdate {}

  public interface PartialUpdate {}
}
