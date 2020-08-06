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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
  private final int initRsEventCount = 3;
  private List<RsEvent> initRsEvent;

  @BeforeEach
  private void setup () {
    this.mockMvc = MockMvcBuilders.standaloneSetup(new RsController(this.rsEventRepository, this.userRepository))
        .setControllerAdvice(new RsExceptionHandler())
        .build();
    this.mapper = new ObjectMapper();
    this.modelMapper = new ModelMapper();
    this.initTestTable();
  }

  private void initTestTable() {
    this.userRepository.deleteAll();

    User user = new User("han0", 21, "male", "test@test.com", "13755556666");
    this.userRepository.save(modelMapper.map(user, UserDto.class));

    this.initRsEvent = new ArrayList<>();
    RsEvent rsEvent = new RsEvent("rs", "key", 1);
    for (int i = 0; i < this.initRsEventCount; i ++) {
      rsEvent.setEventName("rs" + i);
      this.initRsEvent.add(rsEvent);
      this.rsEventRepository.save(modelMapper.map(rsEvent, RsEventDto.class));
    }
  }

  private List<RsEvent> getCurrentRsList () {
    return this.rsEventRepository.findAll().stream()
        .map(rsEventDto -> modelMapper.map(rsEventDto, RsEvent.class))
        .collect(Collectors.toList());
  }

  @Test
  void should_return_all_rs_list () throws Exception {
    this.mockMvc.perform(get("/rs/list"))
        .andExpect(jsonPath("$", hasSize(this.initRsEventCount)))

        .andExpect(jsonPath("$[0].eventName", is("rs0")))
        .andExpect(jsonPath("$[1].eventName", is("rs1")))
        .andExpect(jsonPath("$[2].eventName", is("rs2")))

        .andExpect(jsonPath("$[0].keyWord", is("key")))
        .andExpect(jsonPath("$[1].keyWord", is("key")))
        .andExpect(jsonPath("$[2].keyWord", is("key")))

        .andExpect(status().isOk());
  }

  @Test
  void should_add_new_rs_event() throws Exception {
    RsEvent newRsEvent = new RsEvent("rs-new", "new", 1);
    String newRsEventStr = mapper.writeValueAsString(newRsEvent);
    this.mockMvc.perform(
        post("/rs/add").content(newRsEventStr)
            .contentType(MediaType.APPLICATION_JSON)
    )
        .andExpect(status().isCreated());

    List<RsEvent> rsEventList = this.getCurrentRsList();
    assertEquals(newRsEvent, rsEventList.get(this.initRsEventCount));
  }

  @Test
  void should_update_specific_rs_event() throws Exception {

    this.mockMvc.perform(patch("/rs/update?index=1&eventName=rs1-modified&key=key1-modified"))
        .andExpect(status().isOk());
    this.mockMvc.perform(patch("/rs/update?index=2&eventName=rs2-modified"))
        .andExpect(status().isOk());
    this.mockMvc.perform(patch("/rs/update?index=3&key=key3-modified"))
        .andExpect(status().isOk());

    this.mockMvc.perform(get("/rs/list"))
        .andExpect(jsonPath("$[0].eventName", is("rs1-modified")))
        .andExpect(jsonPath("$[1].eventName", is("rs2-modified")))
        .andExpect(jsonPath("$[2].eventName", is("rs3")))

        .andExpect(jsonPath("$[0].key", is("key1-modified")))
        .andExpect(jsonPath("$[1].key", is("key")))
        .andExpect(jsonPath("$[2].key", is("key3-modified")))

        .andExpect(status().isOk());
  }

  @Test
  void should_delete_specific_rs_event_by_id() throws Exception {
    List<RsEvent> expectList = this.getCurrentRsList();
    expectList.remove(0);

    this.mockMvc.perform(delete("/rs/1"))
        .andExpect(status().isOk());

    List<RsEvent> actualList = this.getCurrentRsList();
    assertEquals(expectList, actualList);
  }
}
