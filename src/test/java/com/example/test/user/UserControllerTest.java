package com.example.test.user;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserService userService;

  @Test
  @DisplayName("when create user with proper data then send 201 status")
  void whenCreateUserWithProperDataThenResponseWithStatusCode201() throws Exception {
    final String requestURI = "/users";
    NewUserDTO userData = new NewUserDTO("test.12@gmail.com", "Mark", "Jovar",
                LocalDate.of(2000, 4, 20), null, null);
    String content = objectMapper.writeValueAsString(userData);

    when(userService.create(eq(userData))).thenReturn(1);

    mockMvc
        .perform(post(requestURI).contentType(APPLICATION_JSON).content(content))
        .andExpectAll(
            status().isCreated(),
            header().string(LOCATION, "/users/1"),
            content().contentType(APPLICATION_JSON));

    verify(userService, times(1)).create(eq(userData));
  }

  @Test
  @DisplayName("when create user with invalid data then send 400 status")
  void whenCreateUserWithInvalidDataThenResponseWithStatusCode400() throws Exception {
    final String requestURI = "/users";
    NewUserDTO userData = new NewUserDTO("test.12gmailcom", null, "  ",
            LocalDate.of(2024, 4, 25), null, null);
    String content = objectMapper.writeValueAsString(userData);

    mockMvc
        .perform(post(requestURI).contentType(APPLICATION_JSON).content(content))
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(APPLICATION_JSON),
            jsonPath("$.timestamp").exists(),
            jsonPath("$.statusCode").value(400),
            jsonPath("$.errorMessage").isNotEmpty(),
            jsonPath("$.validationErrors").isArray(),
            jsonPath("$.validationErrors", hasSize(4)),
            jsonPath("$.validationErrors[?(@.propertyName == \"lastName\" && @.message == \"Last name can't be blank\")]").exists(),
            jsonPath("$.validationErrors[?(@.propertyName == \"email\" && @.message == \"Invalid email format\")]").exists(),
            jsonPath("$.validationErrors[?(@.propertyName == \"firstName\" && @.message == \"First name can't be blank\")]").exists(),
            jsonPath("$.validationErrors[?(@.propertyName == \"birthDate\" && @.message == \"Date must be earlier than current date\")]").exists());

    verify(userService, never()).create(any(NewUserDTO.class));
  }
}
