package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

  private MockMvc mockMvc;
  private User curUser;

  @BeforeEach
  private void setup () {
    this.mockMvc = MockMvcBuilders.standaloneSetup(new UserController()).build();
    this.curUser = new User("han", 21, "male", "test@test.com", "13755556666");
  }

  @Test
  void should_register_new_user() throws Exception {

    ObjectMapper objectMapper = new ObjectMapper();
    String userJson = objectMapper.writeValueAsString(curUser);

    this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userJson))
        .andExpect(status().isOk());
  }

  @Test
  void should_get_all_user() throws Exception {
    this.should_register_new_user();
    this.mockMvc.perform(get("/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].user_name", is(curUser.getName())))
        .andExpect(jsonPath("$[0].user_age" , is(curUser.getAge())))
        .andExpect(jsonPath("$[0].user_gender", is(curUser.getGender())))
        .andExpect(jsonPath("$[0].user_email", is(curUser.getEmail())))
        .andExpect(jsonPath("$[0].user_phone", is(curUser.getPhone())));
  }
}