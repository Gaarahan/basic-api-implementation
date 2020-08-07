package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.component.RsExceptionHandler;
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
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RsControllerBasicTest {

  private MockMvc mockMvc;
  private ObjectMapper mapper;
  private ModelMapper modelMapper;
  @Autowired
  RsEventRepository rsEventRepository;
  @Autowired
  UserRepository userRepository;

  @BeforeEach
  private void setup () {
    this.mockMvc = MockMvcBuilders.standaloneSetup(new RsController(this.rsEventRepository, this.userRepository))
        .setControllerAdvice(new RsExceptionHandler())
        .build();
    this.mapper = new ObjectMapper();
    this.modelMapper = new ModelMapper();
    this.userRepository.deleteAll();
    this.initTestTable();
  }

  private void initTestTable() {
    User curUser = new User("han1", 21, "male", "test@test.com", "13755556666");
    UserDto newUser = modelMapper.map(curUser, UserDto.class);
    this.userRepository.save(newUser);
    UserDto u = this.userRepository.findAll().get(0);
    RsEventDto rs = modelMapper.map(new RsEvent("rs-new", "new", u.getId()), RsEventDto.class);
    rs.setUserDto(u);
    this.rsEventRepository.save(rs);
  }

  private List<RsEvent> getCurrentRsList () {
    return this.rsEventRepository.findAll().stream()
        .map(rsEventDto -> modelMapper.map(rsEventDto, RsEvent.class))
        .collect(Collectors.toList());
  }

  @Test
  void should_return_all_rs_list () throws Exception {
    this.mockMvc.perform(get("/rs/list"))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(status().isOk());

  }

  @Test
  void should_add_new_rs_event() throws Exception {
    this.rsEventRepository.deleteAll();

    UserDto u = this.userRepository.findAll().get(0);
    RsEvent newRsEvent = new RsEvent("rs-new", "new", u.getId());
    String newRsEventStr = mapper.writeValueAsString(newRsEvent);
    this.mockMvc.perform(
        post("/rs/add").content(newRsEventStr)
            .contentType(MediaType.APPLICATION_JSON)
    )
        .andExpect(status().isCreated());

    List<RsEvent> rsEventList = this.getCurrentRsList();
    assertEquals(newRsEvent, rsEventList.get(rsEventList.size() - 1));
  }

  @Test
  void should_get_bad_request_when_add_new_rs_event_for_not_exist_user() throws Exception {
    RsEvent newRsEvent = new RsEvent("rs-new", "new", 100);
    String newRsEventStr = mapper.writeValueAsString(newRsEvent);
    this.mockMvc.perform(
        post("/rs/add").content(newRsEventStr)
            .contentType(MediaType.APPLICATION_JSON)
    )
        .andExpect(status().isBadRequest());
  }

  @Test
  void should_update_specific_rs_event() throws Exception {
    RsEventDto rs = this.rsEventRepository.findAll().get(0);
    RsEvent newRsEvent = modelMapper.map(rs, RsEvent.class);

    newRsEvent.setEventName("rs-modified");
    newRsEvent.setKeyWord("key-modified");

    this.mockMvc.perform(
        patch("/rs/update")
            .param("id", String.valueOf(rs.getId()))
            .param("eventName", newRsEvent.getEventName())
            .param("keyWord", newRsEvent.getKeyWord())
    )
        .andExpect(status().isOk());
    RsEventDto updatedRsEvent = this.rsEventRepository.findById(rs.getId()).get();
    assertEquals(newRsEvent, modelMapper.map(updatedRsEvent, RsEvent.class));

    newRsEvent.setEventName("rs-modified");
    newRsEvent.setKeyWord("key-modified-again");

    this.mockMvc.perform(
        patch("/rs/update")
            .param("id", String.valueOf(rs.getId()))
            .param("keyWord", newRsEvent.getKeyWord())
    )
        .andExpect(status().isOk());
    updatedRsEvent = this.rsEventRepository.findById(rs.getId()).get();
    assertEquals(newRsEvent, modelMapper.map(updatedRsEvent, RsEvent.class));

    newRsEvent.setEventName("rs-modified-again");
    newRsEvent.setKeyWord("key-modified-again");

    this.mockMvc.perform(
        patch("/rs/update")
            .param("id", String.valueOf(rs.getId()))
            .param("eventName", newRsEvent.getEventName())
    )
        .andExpect(status().isOk());
    updatedRsEvent = this.rsEventRepository.findById(rs.getId()).get();
    assertEquals(newRsEvent, modelMapper.map(updatedRsEvent, RsEvent.class));
  }

  @Test
  void should_delete_specific_rs_event_by_id() throws Exception {
    int firstRsEventId = this.rsEventRepository.findAll().get(0).getId();
    this.mockMvc.perform(delete("/rs/" + firstRsEventId))
        .andExpect(status().isOk());

    assertFalse(this.rsEventRepository.findById(firstRsEventId).isPresent());
  }
}
