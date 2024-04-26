package com.example.test.user;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
class UserController {

  private final UserService userService;

  @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<String> add(
      HttpServletRequest request, @Valid @RequestBody NewUserDTO details) {
    Integer userId = userService.create(details);
    return ResponseEntity.status(CREATED)
        .header(HttpHeaders.LOCATION, String.format("%s/%d", request.getRequestURI(), userId))
        .contentType(APPLICATION_JSON)
        .build();
  }

  @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<String> fullUpdate(
      @PathVariable Integer id, @Valid @RequestBody UserFullUpdate details) {
    userService.fullUpdate(id, details);
    return ResponseEntity.status(OK).contentType(APPLICATION_JSON).build();
  }

  @PatchMapping(
      value = "/{id}",
      consumes = APPLICATION_JSON_VALUE,
      produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<String> partialUpdate(
      @PathVariable Integer id, @RequestBody UserPartialUpdateDTO details) {
    userService.partialUpdate(id, details);
    return ResponseEntity.status(OK).contentType(APPLICATION_JSON).build();
  }

  @DeleteMapping(
      value = "/{id}",
      consumes = APPLICATION_JSON_VALUE,
      produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<String> delete(@PathVariable Integer id) {
    Integer userId = userService.delete(id);
    return ResponseEntity.status(OK)
        .contentType(APPLICATION_JSON)
        .body(String.format("User with id <%d> was deleted", userId));
  }
}
