package com.example.test.user;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.test.exception.InvalidDateRangeException;
import com.example.test.exception.InvalidUserAgeException;
import com.example.test.exception.UserNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  private static final int AGE_CONSTRAINT = 18;

  private final UserRepository userRepository = mock(UserRepository.class);

  private final UserService userService = new UserService(userRepository, AGE_CONSTRAINT);

  @Test
  @DisplayName(
      "when create user with age less than age constraint then throw InvalidUserAgeException")
  void whenCreateUserWithAgeLessThanAgeConstraintThenThrowInvalidUserAgeException() {
    final String exceptionMessage = String.format("User age less than %d", AGE_CONSTRAINT);
    var userData =
        new UserDTO("test.12@gmail.com", "Mark", "Jovar", LocalDate.of(2020, 4, 20), null, null);
    var errorMessage =
        assertThrows(InvalidUserAgeException.class, () -> userService.create(userData));
    assertEquals(exceptionMessage, errorMessage.getMessage());

    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("when create user with age greater than age constraint then return user id")
  void whenCreateUserWithAgeGreaterThanAgeConstraintThenReturnUserId() {
    var userData =
        new UserDTO("test.12@gmail.com", "Mark", "Jovar", LocalDate.of(2005, 4, 20), null, null);
    User newUser = User.builder().id(1).build();

    when(userRepository.save(any(User.class))).thenReturn(newUser);

    var userId = assertDoesNotThrow(() -> userService.create(userData));
    assertEquals(1, userId);

    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  @DisplayName("when delete non-existing user then throw UserNotFoundException")
  void whenDeleteNonExistingUserThenThrowUserNotFoundException() {
    final int userId = 1;
    final String exceptionMessage = String.format("User with id <%d> not found", userId);

    when(userRepository.findById(eq(userId))).thenReturn(Optional.empty());

    var errorMessage = assertThrows(UserNotFoundException.class, () -> userService.delete(userId));
    assertEquals(exceptionMessage, errorMessage.getMessage());

    verify(userRepository, times(1)).findById(eq(userId));
    verify(userRepository, never()).delete(any(User.class));
  }

  @Test
  @DisplayName("when delete existing user then delete successfully")
  void whenDeleteExistingUserThenDeleteSuccessfully() {
    final int userId = 1;
    User user = User.builder().id(userId).build();

    when(userRepository.findById(eq(userId))).thenReturn(Optional.of(user));
    doNothing().when(userRepository).delete(eq(user));

    var deletedUserId = assertDoesNotThrow(() -> userService.delete(userId));
    assertEquals(userId, deletedUserId);

    verify(userRepository, times(1)).findById(eq(userId));
    verify(userRepository, times(1)).delete(eq(user));
  }

  @Test
  @DisplayName("when full update user with all details then update successfully")
  void whenFullUpdateUserWithAllDetailsProvidedThenUpdateSuccessfully() {
    final int userId = 1;
    User user = User.builder().id(userId).build();
    var details =
        new UserDTO(
            "mark.jovar@gmail.com",
            "Mark",
            "Jovar",
            LocalDate.of(2000, 3, 10),
            "Adress",
            "phone number");

    when(userRepository.findById(eq(userId))).thenReturn(Optional.of(user));
    when(userRepository.save(eq(user))).thenReturn(user);

    assertDoesNotThrow(() -> userService.update(userId, details));

    assertEquals(details.getEmail(), user.getEmail());
    assertEquals(details.getFirstName(), user.getFirstName());
    assertEquals(details.getLastName(), user.getLastName());
    assertEquals(details.getBirthDate(), user.getBirthDate());
    assertEquals(details.getAddress(), user.getAddress());
    assertEquals(details.getPhoneNumber(), user.getPhoneNumber());

    verify(userRepository, times(1)).findById(eq(userId));
    verify(userRepository, times(1)).save(eq(user));
  }

  @Test
  @DisplayName("when full update non-existing user then throw UserNotFoundException")
  void whenFullUpdateNotExistingUserWithAllDetailsThenThrowUserNotFoundException() {
    final int userId = 1;
    final String exceptionMessage = String.format("User with id <%d> not found", userId);
    var details =
        new UserDTO(
            "mark.jovar@gmail.com",
            "Mark",
            "Jovar",
            LocalDate.of(2000, 3, 10),
            "Adress",
            "phone number");

    when(userRepository.findById(eq(userId))).thenReturn(Optional.empty());

    var errorMessage =
        assertThrows(UserNotFoundException.class, () -> userService.update(userId, details));

    assertEquals(exceptionMessage, errorMessage.getMessage());

    verify(userRepository, times(1)).findById(eq(userId));
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName(
      "when full update user with age less than age constraint then throw InvalidUserAgeException")
  void whenFullUpdateUserWithAgeLessThanAgeConstraintThenThrowInvalidUserAgeException() {
    final int userId = 1;
    final String exceptionMessage = String.format("User age less than %d", AGE_CONSTRAINT);
    User user = User.builder().firstName("Dummy").lastName("Dumbster").build();
    var details =
        new UserDTO(
            "mark.jovar@gmail.com", "Mark", "Jovar", LocalDate.now(), "Adress", "phone number");

    var errorMessage =
        assertThrows(InvalidUserAgeException.class, () -> userService.update(userId, details));

    assertEquals(exceptionMessage, errorMessage.getMessage());

    assertNotEquals(user.getFirstName(), details.getFirstName());
    assertNotEquals(user.getLastName(), details.getLastName());

    verify(userRepository, never()).findById(eq(userId));
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName(
      "when partial update user with age less than age constraint then throw InvalidUserAgeException")
  void whenPartialUpdateUserWithAgeLessThanAgeConstraintThenThrowInvalidUserAgeException() {
    final int userId = 1;
    final String exceptionMessage = String.format("User age less than %d", AGE_CONSTRAINT);
    User user = User.builder().firstName("Dummy").lastName("Dumbster").build();
    var details = new UserDTO(null, "Mark", "Jovar", LocalDate.now(), null, null);

    var errorMessage =
        assertThrows(InvalidUserAgeException.class, () -> userService.update(userId, details));

    assertEquals(exceptionMessage, errorMessage.getMessage());

    assertNotEquals(user.getFirstName(), details.getFirstName());
    assertNotEquals(user.getLastName(), details.getLastName());

    verify(userRepository, never()).findById(eq(userId));
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("when partial update non-existing user then throw UserNotFoundException")
  void whenPartialUpdateNotExistingUserThenThrowUserNotFoundException() {
    final int userId = 1;
    final String exceptionMessage = String.format("User with id <%d> not found", userId);
    var details = new UserDTO(null, "Mark", "Jovar", LocalDate.of(1950, 4, 10), null, null);

    when(userRepository.findById(eq(userId))).thenReturn(Optional.empty());

    var errorMessage =
        assertThrows(UserNotFoundException.class, () -> userService.update(userId, details));

    assertEquals(exceptionMessage, errorMessage.getMessage());

    verify(userRepository, times(1)).findById(eq(userId));
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("when partial update user then update successfully")
  void whenPartialUpdateUserThenUpdateSuccessfully() {
    final int userId = 1;
    User user =
        User.builder()
            .email("test@gmail.com")
            .firstName("Dummy")
            .lastName("Dumbster")
            .birthDate(LocalDate.of(1950, 4, 10))
            .address("address")
            .phoneNumber("phone")
            .build();
    var details = new UserDTO("  ", "Mark", "Jovar", LocalDate.of(1960, 5, 15), "", null);

    when(userRepository.findById(eq(userId))).thenReturn(Optional.of(user));
    when(userRepository.save(eq(user))).thenReturn(user);

    assertDoesNotThrow(() -> userService.update(userId, details));

    assertEquals(details.getFirstName(), user.getFirstName());
    assertEquals(details.getLastName(), user.getLastName());
    assertEquals(details.getBirthDate(), user.getBirthDate());

    assertNotEquals(details.getEmail(), user.getEmail());
    assertNotEquals(details.getAddress(), user.getAddress());
    assertNotEquals(details.getPhoneNumber(), user.getPhoneNumber());

    verify(userRepository, times(1)).findById(eq(userId));
    verify(userRepository, times(1)).save(eq(user));
  }

  @Test
  @DisplayName("when find all users with proper dates then return list of users")
  void whenFindAllUsersWithValidDatesThenReturnUsersList() {
    final LocalDate from = LocalDate.of(2000, 1, 1);
    final LocalDate to = LocalDate.of(2003, 1, 1);

    when(userRepository.findAllByBirthDateBetween(eq(from), eq(to))).thenReturn(List.of());

    var users = assertDoesNotThrow(() -> userService.getAllByDateBetween(from, to));

    assertNotNull(users);

    verify(userRepository, times(1)).findAllByBirthDateBetween(eq(from), eq(to));
  }

  @Test
  @DisplayName(
      "when find all users with dateFrom after dateTo then throw InvalidDateRangeException")
  void whenFindAllUsersWithInvalidDateRangeThanThrowInvalidDateRangeException() {
    final LocalDate from = LocalDate.of(2005, 1, 1);
    final LocalDate to = LocalDate.of(2003, 1, 1);
    final String exceptionMessage = "DateFrom can't be after to dateTo";

    var errorMessage =
        assertThrows(
            InvalidDateRangeException.class, () -> userService.getAllByDateBetween(from, to));

    assertEquals(exceptionMessage, errorMessage.getMessage());

    verify(userRepository, never()).findAllByBirthDateBetween(eq(from), eq(to));
  }
}
