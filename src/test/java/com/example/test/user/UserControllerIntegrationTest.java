package com.example.test.user;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private UserService userService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  @Order(1)
  @DisplayName("when create user with proper data then send 201 status")
  void whenCreateUserWithProperDataThenResponseWithStatusCode201() throws Exception {
    final String requestURI = "/users";
    NewUserDTO userData =
        new NewUserDTO("test.12@gmail.com", "Mark", "Jovar", LocalDate.of(2000, 4, 20), null, null);
    String content = objectMapper.writeValueAsString(userData);
    mockMvc
        .perform(post(requestURI).contentType(APPLICATION_JSON).content(content))
        .andExpectAll(
            status().isCreated(),
            header().exists(LOCATION),
            content().contentType(APPLICATION_JSON));
  }

  @Test
  @Order(2)
  @DisplayName("when create user with invalid data then send 400 status")
  void whenCreateUserWithInvalidDataThenResponseWithStatusCode400() throws Exception {
    final String requestURI = "/users";
    NewUserDTO userData =
        new NewUserDTO("test.12gmailcom", null, "  ", LocalDate.now(), null, null);
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
            jsonPath(
                    "$.validationErrors[?(@.propertyName == \"lastName\" && @.message == \"Last name can't be blank\")]")
                .exists(),
            jsonPath(
                    "$.validationErrors[?(@.propertyName == \"email\" && @.message == \"Invalid email format\")]")
                .exists(),
            jsonPath(
                    "$.validationErrors[?(@.propertyName == \"firstName\" && @.message == \"First name can't be blank\")]")
                .exists(),
            jsonPath(
                    "$.validationErrors[?(@.propertyName == \"birthDate\" && @.message == \"Date must be earlier than current date\")]")
                .exists());
  }

  @Test
  @Order(3)
  @DisplayName("when full update user with all details then return 200 status")
  void whenFullFullUpdateUserWithAllDetailsThenResponseWithStatusCode200() throws Exception {
    final int userId = 1;
    UserFullUpdate details =
        new UserFullUpdate(
            "mark.jovar@gmail.com", "Mark", "Jovar", LocalDate.of(2004, 4, 25), "address", "phone");
    String content = objectMapper.writeValueAsString(details);

    mockMvc
        .perform(put("/users/{id}", userId).contentType(APPLICATION_JSON).content(content))
        .andExpectAll(status().isOk(), content().contentType(APPLICATION_JSON));
  }

  @Test
  @Order(4)
  @DisplayName("when full update user with non all details then return 400 status")
  void whenFullUpdateUserWithNotAllDetailsThenResponseWithStatusCode400() throws Exception {
    final int userId = 1;
    UserFullUpdate details = new UserFullUpdate(null, null, null, null, null, null);
    String content = objectMapper.writeValueAsString(details);

    mockMvc
        .perform(put("/users/{id}", userId).contentType(APPLICATION_JSON).content(content))
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(APPLICATION_JSON),
            jsonPath("$.timestamp").exists(),
            jsonPath("$.statusCode").value(400),
            jsonPath("$.errorMessage").isNotEmpty(),
            jsonPath("$.validationErrors").isArray(),
            jsonPath("$.validationErrors", hasSize(6)),
            jsonPath(
                    "$.validationErrors[?(@.propertyName == \"lastName\" && @.message == \"Property can't be blank\")]")
                .exists(),
            jsonPath(
                    "$.validationErrors[?(@.propertyName == \"email\" && @.message == \"Property can't be blank\")]")
                .exists(),
            jsonPath(
                    "$.validationErrors[?(@.propertyName == \"firstName\" && @.message == \"Property can't be blank\")]")
                .exists(),
            jsonPath(
                    "$.validationErrors[?(@.propertyName == \"address\" && @.message == \"Property can't be blank\")]")
                .exists(),
            jsonPath(
                    "$.validationErrors[?(@.propertyName == \"phoneNumber\" && @.message == \"Property can't be blank\")]")
                .exists(),
            jsonPath(
                    "$.validationErrors[?(@.propertyName == \"birthDate\" && @.message == \"Property is required\")]")
                .exists());
  }

  @Test
  @Order(5)
  @DisplayName("when delete existing user then return 200 status")
  void whenDeleteExistingUserThenResponseWithStatusCode200() throws Exception {
    final int userId = 1;
    mockMvc
        .perform(delete("/users/{id}", userId).contentType(APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            content().contentType(APPLICATION_JSON),
            content().string(String.format("User with id <%d> was deleted", userId)));
  }

  @Test
  @Order(6)
  @DisplayName("when full update not existing user with all details then return 404 status")
  void whenFullUpdateNotExistingUserWithAllDetailsThenResponseWithStatusCode404() throws Exception {
    final int userId = 999;
    UserFullUpdate details =
        new UserFullUpdate(
            "mark.jovar@gmail.com", "Mark", "Jovar", LocalDate.of(2004, 4, 25), "address", "phone");
    String content = objectMapper.writeValueAsString(details);

    mockMvc
        .perform(put("/users/{id}", userId).contentType(APPLICATION_JSON).content(content))
        .andExpectAll(
            status().isNotFound(),
            content().contentType(APPLICATION_JSON),
            jsonPath("$.timestamp").exists(),
            jsonPath("$.statusCode").value(404),
            jsonPath("$.errorMessage").value(String.format("User with id <%d> not found", userId)),
            jsonPath("$.validationErrors").doesNotExist());
  }

  @Test
  @Order(8)
  @DisplayName("when delete non-existing user then return 404 status")
  void whenDeleteNonExistingUserThenResponseWithStatusCode404() throws Exception {
    final int userId = 3;
    mockMvc
        .perform(delete("/users/{id}", userId).contentType(APPLICATION_JSON))
        .andExpectAll(
            status().isNotFound(),
            content().contentType(APPLICATION_JSON),
            jsonPath("$.timestamp").exists(),
            jsonPath("$.statusCode").value(404),
            jsonPath("$.errorMessage").value(String.format("User with id <%d> not found", userId)),
            jsonPath("$.validationErrors").doesNotExist());
  }

  @Test
  @Order(8)
  @DisplayName("when partial update user then return 200 status")
  void whenPartialUpdateUserThenResponseWithStatusCode200() throws Exception {
    final int userId = 1;
    UserPartialUpdateDTO details = new UserPartialUpdateDTO(null, null, null, null, null, null);
    String content = objectMapper.writeValueAsString(details);

    mockMvc
        .perform(patch("/users/{id}", userId).contentType(APPLICATION_JSON).content(content))
        .andExpectAll(status().isOk(), content().contentType(APPLICATION_JSON));
  }

  @Test
  @Order(9)
  @DisplayName("when partial update non-existing user then return 404 status")
  void whenPartialUpdateNotExistingUserThenResponseWithStatusCode404() throws Exception {
    final int userId = 999;
    String exceptionMessage = String.format("User with id <%d> not found", userId);
    UserPartialUpdateDTO details = new UserPartialUpdateDTO(null, null, null, null, null, null);
    String content = objectMapper.writeValueAsString(details);

    mockMvc
        .perform(patch("/users/{id}", userId).contentType(APPLICATION_JSON).content(content))
        .andExpectAll(
            status().isNotFound(),
            content().contentType(APPLICATION_JSON),
            jsonPath("$.timestamp").exists(),
            jsonPath("$.statusCode").value(404),
            jsonPath("$.errorMessage").value(exceptionMessage),
            jsonPath("$.validationErrors").doesNotExist());
  }

  @Test
  @Order(9)
  @DisplayName("when partial update user with age less than age constraint then return 400 status")
  void whenPartialUpdateUserWithAgeLessThanAgeConstraintThenResponseWithStatusCode400()
      throws Exception {
    final int userId = 1;
    String exceptionMessage = String.format("User age less than %d", 18);
    UserPartialUpdateDTO details = new UserPartialUpdateDTO(null, null, null, LocalDate.now(), null, null);
    String content = objectMapper.writeValueAsString(details);

    mockMvc
        .perform(patch("/users/{id}", userId).contentType(APPLICATION_JSON).content(content))
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(APPLICATION_JSON),
            jsonPath("$.timestamp").exists(),
            jsonPath("$.statusCode").value(400),
            jsonPath("$.errorMessage").value(exceptionMessage),
            jsonPath("$.validationErrors").doesNotExist());
  }
}
