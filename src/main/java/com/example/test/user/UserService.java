package com.example.test.user;

import com.example.test.exception.InvalidDateRangeException;
import com.example.test.exception.InvalidUserAgeException;
import com.example.test.exception.UserNotFoundException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class UserService {

  private final UserRepository userRepository;
  private final int ageConstraint;

  public UserService(
      UserRepository userRepository,
      @Value("${application.age-constraint}") int ageConstraint) {
    this.userRepository = userRepository;
    this.ageConstraint = ageConstraint;
  }

  public List<UserDTO> getAllByDateBetween(LocalDate from, LocalDate to) {
    if (from.isAfter(to)) {
      throw new InvalidDateRangeException("DateFrom can't be after to dateTo");
    }
    return userRepository.findAllByBirthDateBetween(from, to);
  }

  @Transactional
  public Integer create(UserDTO details) {
    verifyAge(details.getBirthDate());
    User newUser =
        User.builder()
            .email(details.getEmail())
            .firstName(details.getFirstName())
            .lastName(details.getLastName())
            .birthDate(details.getBirthDate())
            .address(details.getAddress())
            .phoneNumber(details.getPhoneNumber())
            .build();
    User user = userRepository.save(newUser);
    return user.getId();
  }

  @Transactional
  public Integer delete(Integer id) {
    var user = getUser(id);
    userRepository.delete(user);
    return user.getId();
  }

  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public void update(Integer id, UserDTO details) {
    if (details.getBirthDate() != null) {
      verifyAge(details.getBirthDate());
    }
    var user = getUser(id);
    user.setBirthDate(details.getBirthDate());
    // Boilerplate code can be replaced with mapstruct or reflection
    if (details.getEmail() != null && !details.getEmail().isBlank()) {
      user.setEmail(details.getEmail());
    }
    if (details.getFirstName() != null && !details.getFirstName().isBlank()) {
      user.setFirstName(details.getFirstName());
    }
    if (details.getLastName() != null && !details.getLastName().isBlank()) {
      user.setLastName(details.getLastName());
    }
    if (details.getAddress() != null && !details.getAddress().isBlank()) {
      user.setAddress(details.getAddress());
    }
    if (details.getPhoneNumber() != null && !details.getPhoneNumber().isBlank()) {
      user.setPhoneNumber(details.getPhoneNumber());
    }
    userRepository.save(user);
  }

  private void verifyAge(LocalDate birthDate) {
    int age = LocalDate.now().getYear() - birthDate.getYear();
    if (age < ageConstraint) {
      throw new InvalidUserAgeException(String.format("User age less than %d", ageConstraint));
    }
  }

  private User getUser(Integer id) {
    return userRepository
        .findById(id)
        .orElseThrow(
            () -> new UserNotFoundException(String.format("User with id <%d> not found", id)));
  }
}
