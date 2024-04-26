package com.example.test.user;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface UserRepository extends JpaRepository<User, Integer> {

  List<UserDetailsDTO> findAllByBirthDateBetween(LocalDate from, LocalDate to);
}
