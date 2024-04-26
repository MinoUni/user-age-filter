package com.example.test.user;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
class UserController {

  private final UserService userService;

  @GetMapping(produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<List<UserDTO>> getAllByDateBetween(
      @PastOrPresent(message = "DateFrom can't be in future")
          @DateTimeFormat(pattern = "dd-MM-yyyy")
          @RequestParam("from")
          LocalDate from,
      @PastOrPresent(message = "DateTo can't be in future")
          @DateTimeFormat(pattern = "dd-MM-yyyy")
          @RequestParam("to")
          LocalDate to) {
    return ResponseEntity.status(OK)
        .contentType(APPLICATION_JSON)
        .body(userService.getAllByDateBetween(from, to));
  }

  @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<String> add(
      HttpServletRequest request, @Validated(UserDTO.Create.class) @RequestBody UserDTO details) {
    Integer userId = userService.create(details);
    return ResponseEntity.status(CREATED)
        .header(HttpHeaders.LOCATION, String.format("%s/%d", request.getRequestURI(), userId))
        .contentType(APPLICATION_JSON)
        .build();
  }

  @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<String> fullUpdate(
      @PathVariable Integer id, @Validated(UserDTO.FullUpdate.class) @RequestBody UserDTO details) {
    userService.update(id, details);
    return ResponseEntity.status(OK).contentType(APPLICATION_JSON).build();
  }

  @PatchMapping(
      value = "/{id}",
      consumes = APPLICATION_JSON_VALUE,
      produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<String> partialUpdate(
      @PathVariable Integer id, @Validated(UserDTO.PartialUpdate.class) @RequestBody UserDTO details) {
    userService.update(id, details);
    return ResponseEntity.status(OK).contentType(APPLICATION_JSON).build();
  }

  @DeleteMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<String> delete(@PathVariable Integer id) {
    Integer userId = userService.delete(id);
    return ResponseEntity.status(OK)
        .contentType(APPLICATION_JSON)
        .body(String.format("User with id <%d> was deleted", userId));
  }
}
