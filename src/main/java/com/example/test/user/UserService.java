package com.example.test.user;

import com.example.test.exception.InvalidUserAgeException;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
    int age = LocalDate.now().getYear() - details.birthDate().getYear();
    if (age < ageConstraint) {
      throw new InvalidUserAgeException(String.format("User age less than %d", ageConstraint));
    }
    User newUser =
        User.builder()
            .email(details.email())
            .firstName(details.firstName())
            .lastName(details.lastName())
            .birthDay(details.birthDate())
            .address(details.address())
            .phoneNumber(details.phoneNumber())
            .build();
    User user = userRepository.save(newUser);
    return user.getId();
  }
}
