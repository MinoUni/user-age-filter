package com.example.test.user;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.test.exception.InvalidUserAgeException;
import java.time.LocalDate;
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
}
