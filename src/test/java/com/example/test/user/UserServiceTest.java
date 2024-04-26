package com.example.test.user;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.test.exception.InvalidUserAgeException;
import com.example.test.exception.UserNotFoundException;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  private final UserRepository userRepository = mock(UserRepository.class);

  private final UserService userService = new UserService(userRepository, 18);

  @Test
  @DisplayName(
      "when create user with age less than age constraint then throw InvalidUserAgeException")
  void whenCreateUserWithAgeLessThanAgeConstraintThenThrowInvalidUserAgeException() {
    NewUserDTO userData =
        new NewUserDTO("test.12@gmail.com", "Mark", "Jovar", LocalDate.of(2020, 4, 20), null, null);
    var errorMessage =
        assertThrows(InvalidUserAgeException.class, () -> userService.create(userData));
    assertEquals("User age less than 18", errorMessage.getMessage());

    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("when create user with age greater than age constraint then return user id")
  void whenCreateUserWithAgeGreaterThanAgeConstraintThenReturnUserId() {
    NewUserDTO userData =
        new NewUserDTO("test.12@gmail.com", "Mark", "Jovar", LocalDate.of(2005, 4, 20), null, null);
    User newUser = User.builder().id(1).build();

    when(userRepository.save(any(User.class))).thenReturn(newUser);

    var userId = assertDoesNotThrow(() -> userService.create(userData));
    assertEquals(1, userId);

    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  @DisplayName("when delete non-existing user then return 404 status")
  void whenDeleteNonExistingUserThenResponseWithStatusCode404() {
    final int userId = 1;
    final String message = String.format("User with id <%d> not found", userId);
    
    when(userRepository.findById(eq(userId)))
            .thenThrow(new UserNotFoundException(message));
    
    var errorMessage = assertThrows(UserNotFoundException.class, () -> userService.delete(userId));
    assertEquals(message, errorMessage.getMessage());
    
    verify(userRepository, times(1)).findById(eq(userId));
    verify(userRepository, never()).delete(any(User.class));
  }

  @Test
  @DisplayName("when delete existing user then return 200 status")
  void whenDeleteExistingUserThenResponseWithStatusCode200() {
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
  @DisplayName("when full update user with all details then return 200 status")
  void whenFullFullUpdateUserWithAllDetailsProvidedThenResponseWithStatusCode200() {
    final int userId = 1;
    User user = User.builder().id(userId).build();
    var details = new UserFullUpdate("mark.jovar@gmail.com", "Mark", "Jovar",
            LocalDate.of(2000, 3, 10), "Adress", "phone number");

    when(userRepository.findById(eq(userId))).thenReturn(Optional.of(user));
    when(userRepository.save(eq(user))).thenReturn(user);

    assertDoesNotThrow(() -> userService.fullUpdate(userId, details));

    verify(userRepository, times(1)).findById(eq(userId));
    verify(userRepository, times(1)).save(eq(user));
  }
}
