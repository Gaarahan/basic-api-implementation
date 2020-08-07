package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.dto.RsEventDto;
import com.thoughtworks.rslist.dto.UserDto;
import com.thoughtworks.rslist.dto.VoteDto;
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
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

  @Test
  void should_get_vote_between_time() throws Exception {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime timeBase = LocalDateTime.now();

    Vote voteBefore = new Vote(this.rsId, this.uId, 1, Timestamp.valueOf(timeBase));
    String timeStart = timeBase.plusSeconds(1).format(formatter);
    Vote voteBetween1 = new Vote(this.rsId, this.uId, 1, Timestamp.valueOf(timeBase.plusSeconds(2)));
    Vote voteBetween2 = new Vote(this.rsId, this.uId, 1, Timestamp.valueOf(timeBase.plusSeconds(3)));
    Vote voteBetween3 = new Vote(this.rsId, this.uId, 1, Timestamp.valueOf(timeBase.plusSeconds(4)));
    Vote voteBetween4 = new Vote(this.rsId, this.uId, 1, Timestamp.valueOf(timeBase.plusSeconds(5)));
    String timeEnd = timeBase.plusSeconds(6).format(formatter);
    Vote voteAfter = new Vote(this.rsId, this.uId, 1, Timestamp.valueOf(timeBase.plusSeconds(7)));


    List<VoteDto> voteDtoBetweenList = Stream.of(voteBefore, voteBetween1, voteBetween2, voteBetween3, voteBetween4, voteAfter)
        .map(vote -> modelMapper.map(vote, VoteDto.class))
        .peek(voteDto -> {
          voteDto.setId(null);
          voteDto.setRsEventDto(this.rsEventRepository.findById(this.rsId).get());
          voteDto.setUserDto(this.userRepository.findById(this.uId).get());
        })
        .collect(Collectors.toList());
    this.voteRepository.saveAll(voteDtoBetweenList);

    this.mockMvc.perform(
        get("/rs/vote")
            .param("startTime", timeStart)
            .param("endTime", timeEnd)
    )
        .andExpect(jsonPath("$", hasSize(4)))
        .andExpect(status().isOk());
  }
}

