package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.dto.RsEventDto;
import com.thoughtworks.rslist.dto.UserDto;
import com.thoughtworks.rslist.repository.RsEventRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

  private MockMvc mockMvc;
  private User curUser;
  private ModelMapper modelMapper;

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RsEventRepository rsEventRepository;
  private final int initUserCount = 3;

  @BeforeEach
  private void setup () {
    this.mockMvc = MockMvcBuilders.standaloneSetup(new UserController(this.userRepository)).build();
    this.curUser = new User("han1", 21, "male", "test@test.com", "13755556666");
    this.modelMapper = new ModelMapper();
    this.userRepository.deleteAll();
  }

  private void initUserTable() {
    User user = new User("han", 21, "male", "test@test.com", "13755556666");
    for (int i = 0; i < this.initUserCount; i ++) {
      user.setName("han" + (i + 1));
      UserDto newUserDto = modelMapper.map(user, UserDto.class);
      this.userRepository.save(newUserDto);
    }
  }

  @Test
  void should_register_new_user() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    String userJson = objectMapper.writeValueAsString(curUser);

    this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userJson))
        .andExpect(status().isOk());

    List<UserDto> allUser = this.userRepository.findAll();
    assertEquals(this.curUser, modelMapper.map(allUser.get(0), User.class));
  }

  @Test
  void should_get_all_user() throws Exception {
    this.initUserTable();

    this.mockMvc.perform(get("/users"))
        .andDo(print())
        .andExpect(status().isOk());

    List<UserDto> allUser = this.userRepository.findAll();

    assertEquals(this.initUserCount, allUser.size());
  }

  @Test
  void should_get_user_by_id() throws Exception {
    UserDto newUser = modelMapper.map(curUser, UserDto.class);
    this.userRepository.save(newUser);

    UserDto u = this.userRepository.findAll().get(0);

    String user = this.mockMvc.perform(get("/users/" + u.getId()))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    assertEquals(this.curUser, new ObjectMapper().readValue(user, User.class));
  }

  @Test
  void should_delete_user_by_id_and_remove_all_sub_rs_event() throws Exception {
    UserDto newUser = modelMapper.map(curUser, UserDto.class);
    this.userRepository.save(newUser);
    UserDto u = this.userRepository.findAll().get(0);
    RsEventDto rs = modelMapper.map(new RsEvent("rs-new", "new", u.getId()), RsEventDto.class);
    rs.setUserDto(u);
    this.rsEventRepository.save(rs);

    int rsCountBeforeDelete = this.rsEventRepository.findAll().size();
    this.mockMvc.perform(delete("/users/" + u.getId()))
        .andExpect(status().isOk());
    int rsCountAfterDelete = this.rsEventRepository.findAll().size();

    assertEquals(rsCountBeforeDelete - 1, rsCountAfterDelete);
  }
}