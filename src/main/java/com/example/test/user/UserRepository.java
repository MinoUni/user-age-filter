package com.example.test.user;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
interface UserRepository extends JpaRepository<User, Integer> {

  @Query(
      """
        SELECT new com.example.test.user.UserDTO(u.id, u.email, u.firstName, u.lastName, u.birthDate, u.address, u.phoneNumber)
        FROM User u
        WHERE u.birthDate
        BETWEEN :from AND :to
      """)
  List<UserDTO> findAllByBirthDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
