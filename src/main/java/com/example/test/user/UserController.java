package com.example.test.user;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
class UserController {

  private final UserService userService;

  @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<NewUserDTO> add(
      HttpServletRequest request, @Valid @RequestBody NewUserDTO details) {
    Integer userId = userService.create(details);
    return ResponseEntity.status(CREATED)
        .header(HttpHeaders.LOCATION, String.format("%s/%d", request.getRequestURI(), userId))
        .contentType(APPLICATION_JSON)
        .build();
  }
}
