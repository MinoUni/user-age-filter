package com.example.test.user;

import com.example.test.exception.InvalidUserAgeException;
import com.example.test.exception.UserNotFoundException;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class UserService {

  private final UserRepository userRepository;
  private final int ageConstraint;

  public UserService(UserRepository userRepository, @Value("${application.age-constraint}") int ageConstraint) {
    this.userRepository = userRepository;
    this.ageConstraint = ageConstraint;
  }

  @Transactional
  public Integer create(NewUserDTO details) {
    verifyAge(details.birthDate());
    User newUser =
        User.builder()
            .email(details.email())
            .firstName(details.firstName())
            .lastName(details.lastName())
            .birthDate(details.birthDate())
            .address(details.address())
            .phoneNumber(details.phoneNumber())
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
  public void fullUpdate(Integer id, UserFullUpdate details) {
    verifyAge(details.birthDate());
    var user = getUser(id);
    user.setEmail(details.email());
    user.setFirstName(details.firstName());
    user.setLastName(details.lastName());
    user.setBirthDate(details.birthDate());
    user.setAddress(details.address());
    user.setPhoneNumber(details.phoneNumber());
    userRepository.save(user);
  }

  private void verifyAge(LocalDate birthDate) {
    int age = LocalDate.now().getYear() - birthDate.getYear();
    if (age < ageConstraint) {
      throw new InvalidUserAgeException(String.format("User age less than %d", ageConstraint));
    }
  }

  private User getUser(Integer id) {
    return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(String.format("User with id <%d> not found", id)));
  }
}
