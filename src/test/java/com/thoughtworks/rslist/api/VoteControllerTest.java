package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.dto.RsEventDto;
import com.thoughtworks.rslist.dto.UserDto;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VoteControllerTest {
  @Autowired
  VoteRepository voteRepository;
  @Autowired
  UserRepository userRepository;
  @Autowired
  RsEventRepository rsEventRepository;

  MockMvc mockMvc;
  ModelMapper modelMapper;
  ObjectMapper objectMapper;

  User u;
  int uId;
  RsEvent rs;
  int rsId;

  @BeforeEach
  void setUp() {
    this.mockMvc = MockMvcBuilders.standaloneSetup(new VoteController(this.rsEventRepository, this.userRepository, this.voteRepository)).build();
    this.modelMapper = new ModelMapper();
    this.objectMapper = new ObjectMapper();
    this.initTestData();
  }

  private void initTestData() {
    this.u = new User("han1", 21, "male", "test@test.com", "13755556666");
    UserDto userDto = modelMapper.map(u, UserDto.class);
    this.userRepository.save(userDto);

    this.uId = this.userRepository.findAll().get(0).getId();
    this.rs = new RsEvent("rs-1", "rs", uId);
    RsEventDto rsEventDto = modelMapper.map(rs, RsEventDto.class);
    rsEventDto.setUserDto(userDto);
    this.rsEventRepository.save(rsEventDto);

    this.rsId = this.rsEventRepository.findAll().get(0).getId();
  }

  @Test
  void should_add_vote_record_and_change_rs_event_and_change_user() throws Exception {
    int userVoteBefore = this.userRepository.findById(this.uId).get().getRestVote();
    int rsVoteBefore = this.rsEventRepository.findById(this.rsId).get().getVoteNum();

    int voteCount = 3;
    Vote vote = new Vote(this.rsId, this.uId, voteCount, Timestamp.valueOf(LocalDateTime.now()));

    this.mockMvc.perform(
        post("/rs/vote/" + this.rsId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(vote))
    )
        .andExpect(status().isCreated());

    int userVoteAfter = this.userRepository.findById(this.uId).get().getRestVote();
    int rsVoteAfter = this.rsEventRepository.findById(this.rsId).get().getVoteNum();

    assertEquals(voteCount, userVoteBefore - userVoteAfter);
    assertEquals(voteCount, rsVoteAfter - rsVoteBefore);
  }
}

