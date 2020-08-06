package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.component.RsExceptionHandler;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RsControllerValidateTest {

  private MockMvc mockMvc;
  private User curUser;
  private RsEvent rsEvent;
  private ObjectMapper ignoreAnnotationsMapper;

  @BeforeEach
  private void setup () {
    this.mockMvc = MockMvcBuilders.standaloneSetup(new RsController())
        .setControllerAdvice(new RsExceptionHandler())
        .build();
    this.curUser = new User("han", 21, "male", "test@test.com", "13755556666");
    this.rsEvent = new RsEvent("rs-new", "key", curUser);

    this.ignoreAnnotationsMapper = new ObjectMapper();
    ignoreAnnotationsMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
  }

  @Test
  void should_got_bad_request_message_when_event_name_is_null() throws Exception {
    rsEvent.setEventName(null);
    this.mockMvc.perform(post("/rs/add").content(ignoreAnnotationsMapper.writeValueAsString(rsEvent)).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void should_got_bad_request_message_when_key_is_null() throws Exception {
    rsEvent.setKey(null);
    this.mockMvc.perform(
        post("/rs/add").content(ignoreAnnotationsMapper.writeValueAsString(rsEvent)).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
    )
        .andExpect(status().isBadRequest());
  }

  @Test
  void should_got_bad_request_message_when_user_is_null() throws Exception {
    RsEvent newRsEvent = new RsEvent("rs-new", "key", null);
    this.mockMvc.perform(
        post("/rs/add")
            .content(ignoreAnnotationsMapper.writeValueAsString(newRsEvent))
            .contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
    )
        .andExpect(status().isBadRequest());
  }

  @Test
  void should_got_bad_request_message_when_user_name_has_error() throws Exception {
    curUser.setName("123456789");
    RsEvent newRsEvent = new RsEvent("rs-new", "key", curUser);
    this.mockMvc.perform(post("/rs/add").content(ignoreAnnotationsMapper.writeValueAsString(newRsEvent)).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void should_got_bad_request_message_when_user_age_has_error() throws Exception {
    curUser.setAge(10);
    RsEvent newRsEvent = new RsEvent("rs-new", "key", curUser);
    this.mockMvc.perform(
        post("/rs/add")
            .content(ignoreAnnotationsMapper.writeValueAsString(newRsEvent))
            .contentType(MediaType.APPLICATION_JSON)
    )
        .andExpect(status().isBadRequest());
  }

  @Test
  void should_got_bad_request_message_when_user_gender_has_error() throws Exception {
    curUser.setGender(null);
    RsEvent newRsEvent = new RsEvent("rs-new", "key", curUser);
    this.mockMvc.perform(
        post("/rs/add")
            .content(ignoreAnnotationsMapper.writeValueAsString(newRsEvent))
            .contentType(MediaType.APPLICATION_JSON)
    )
        .andExpect(status().isBadRequest());
  }

  @Test
  void should_got_bad_request_message_when_user_email_has_error() throws Exception {
    curUser.setEmail("aaaaaaaaa");
    RsEvent newRsEvent = new RsEvent("rs-new", "key", curUser);
    this.mockMvc.perform(
        post("/rs/add")
            .content(ignoreAnnotationsMapper.writeValueAsString(newRsEvent))
            .contentType(MediaType.APPLICATION_JSON)
    )
        .andExpect(status().isBadRequest());
  }

  @Test
  void should_got_bad_request_message_when_user_phone_has_error() throws Exception {
    curUser.setPhone("11111");
    RsEvent newRsEvent = new RsEvent("rs-new", "key", curUser);
    this.mockMvc.perform(
        post("/rs/add")
            .content(ignoreAnnotationsMapper.writeValueAsString(newRsEvent))
            .contentType(MediaType.APPLICATION_JSON)
    )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error", is("invalid param")));
  }

  @Test
  void should_got_invalid_params_error_when_pass_wrong_path_params() throws Exception {
    this.mockMvc.perform(get("/rs/list?start=0&end=4"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error", is("invalid request param")));

    this.mockMvc.perform(get("/rs/list?start=1&end=4"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error", is("invalid request param")));

    this.mockMvc.perform(get("/rs/list?start=-1&end=2"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error", is("invalid request param")));
  }

  @Test
  void should_got_invalid_index_error_when_pass_wrong_index() throws Exception {
    this.mockMvc.perform(get("/rs/-1"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error", is("invalid index")));

    this.mockMvc.perform(get("/rs/7"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error", is("invalid index")));
  }
}
