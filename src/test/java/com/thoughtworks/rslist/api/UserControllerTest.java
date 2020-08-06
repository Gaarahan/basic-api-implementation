package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.dto.UserDto;
import com.thoughtworks.rslist.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

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
  private ModelMapper mapper;

  @Autowired
  private UserRepository userRepository;
  private final int initUserCount = 5;

  @BeforeEach
  private void setup () {
    this.mockMvc = MockMvcBuilders.standaloneSetup(new UserController(this.userRepository)).build();
    this.curUser = new User("han0", 21, "male", "test@test.com", "13755556666");
    this.mapper = new ModelMapper();
    this.initUserTable();
  }

  private void initUserTable() {
    User user = new User("han0", 21, "male", "test@test.com", "13755556666");
    for (int i = 0; i < this.initUserCount; i ++) {
      user.setName("han" + i);
      this.userRepository.save(mapper.map(user, UserDto.class));
    }
  }

  @Test
  void should_register_new_user() throws Exception {

    ObjectMapper objectMapper = new ObjectMapper();
    String userJson = objectMapper.writeValueAsString(curUser);

    this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userJson))
        .andExpect(status().isOk());

    List<UserDto> allUser = this.userRepository.findAll();
    assertEquals(this.curUser, mapper.map(allUser.get(0), User.class));
  }

  @Test
  void should_get_all_user() throws Exception {
    this.should_register_new_user();
    this.mockMvc.perform(get("/users"))
        .andExpect(status().isOk());
    List<UserDto> allUser = this.userRepository.findAll();
    assertEquals(this.initUserCount, allUser.size());
    assertEquals(this.curUser, mapper.map(allUser.get(0), User.class));
  }

  @Test
  void should_get_user_by_id() throws Exception {
    int id = 0;
    this.mockMvc.perform(get("/users/" + id))
        .andExpect(status().isOk());
    List<UserDto> allUser = this.userRepository.findAll();
    assertEquals(this.curUser, mapper.map(allUser.get(id), User.class));
  }

  @Test
  void should_delete_user_by_id() throws Exception {
    int id = 0;
    this.mockMvc.perform(get("/users/" + id))
        .andExpect(status().isOk());
    List<UserDto> allUser = this.userRepository.findAll();
    assertEquals(this.curUser, mapper.map(allUser.get(id), User.class));
  }
}