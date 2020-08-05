package com.thoughtworks.rslist;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.api.RsController;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RsListApplicationTests {

  private MockMvc mockMvc;
  private User curUser;
  private RsEvent rsEvent;
  private ObjectMapper ignoreAnnotationsMapper;

  @BeforeEach
  private void setup () {
    this.mockMvc = MockMvcBuilders.standaloneSetup(new RsController()).build();
    this.curUser = new User("han", 21, "male", "test@test.com", "13755556666");
    this.rsEvent = new RsEvent("rs-new", "key", curUser);

    this.ignoreAnnotationsMapper = new ObjectMapper();
    ignoreAnnotationsMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
  }

  private ArrayList<RsEvent> getCurrentRsList () throws Exception {
    String res = this.mockMvc.perform(get("/rs/list"))
        .andReturn().getResponse().getContentAsString();
    @SuppressWarnings("unchecked")
    ArrayList<RsEvent> curRsList = (ArrayList<RsEvent>) new ObjectMapper().readValue(res, ArrayList.class);

    return curRsList;
  }

  @Test
  void should_return_one_rs_event () throws Exception {
    this.mockMvc.perform(get("/rs/1"))
        .andExpect(jsonPath("$.eventName", is("rs1")))
        .andExpect(jsonPath("$.key", is("key")))
        .andExpect(status().isOk());
  }

  @Test
  void should_return_all_rs_list () throws Exception {
    this.mockMvc.perform(get("/rs/list"))
        .andExpect(jsonPath("$", hasSize(3)))

        .andExpect(jsonPath("$[0].eventName", is("rs1")))
        .andExpect(jsonPath("$[1].eventName", is("rs2")))
        .andExpect(jsonPath("$[2].eventName", is("rs3")))

        .andExpect(jsonPath("$[0].key", is("key")))
        .andExpect(jsonPath("$[1].key", is("key")))
        .andExpect(jsonPath("$[2].key", is("key")))

        .andExpect(status().isOk());
  }

  @Test
  void should_return_sub_rs_list () throws Exception {
    this.mockMvc.perform(get("/rs/list?start=1&end=2"))
        .andExpect(jsonPath("$", hasSize(2)))

        .andExpect(jsonPath("$[0].eventName", is("rs1")))
        .andExpect(jsonPath("$[1].eventName", is("rs2")))

        .andExpect(jsonPath("$[0].key", is("key")))
        .andExpect(jsonPath("$[1].key", is("key")))

        .andExpect(status().isOk());
    this.mockMvc.perform(get("/rs/list?start=2&end=3"))
        .andExpect(jsonPath("$", hasSize(2)))

        .andExpect(jsonPath("$[0].eventName", is("rs2")))
        .andExpect(jsonPath("$[1].eventName", is("rs3")))

        .andExpect(jsonPath("$[0].key", is("key")))
        .andExpect(jsonPath("$[1].key", is("key")))

        .andExpect(status().isOk());
    this.mockMvc.perform(get("/rs/list?start=1&end=3"))
        .andExpect(jsonPath("$", hasSize(3)))

        .andExpect(jsonPath("$[0].eventName", is("rs1")))
        .andExpect(jsonPath("$[1].eventName", is("rs2")))
        .andExpect(jsonPath("$[2].eventName", is("rs3")))

        .andExpect(jsonPath("$[0].key", is("key")))
        .andExpect(jsonPath("$[1].key", is("key")))
        .andExpect(jsonPath("$[2].key", is("key")))

        .andExpect(status().isOk());
  }

  @Test
  void should_add_new_rs_event() throws Exception {
    RsEvent rsEvent = new RsEvent("rs-new", "new", curUser);
    String newRsEventStr = ignoreAnnotationsMapper.writeValueAsString(rsEvent);

    this.mockMvc.perform(
        post("/rs/add").content(newRsEventStr)
            .contentType(MediaType.APPLICATION_JSON)
    )
        .andExpect(status().isCreated());

    this.mockMvc.perform(get("/rs/list"))
        .andExpect(jsonPath("$", hasSize(4)))

        .andExpect(jsonPath("$[3].eventName", is("rs-new")))
        .andExpect(jsonPath("$[3].key", is("new")))
        .andExpect(jsonPath("$[3]", not(hasKey("user"))))

        .andExpect(status().isOk());
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
  void should_delete_specific_rs_event() throws Exception {
    ArrayList<RsEvent> expectList = this.getCurrentRsList();
    expectList.remove(0);

    this.mockMvc.perform(delete("/rs/1"))
        .andExpect(status().isOk());

    ArrayList<RsEvent> actualList = this.getCurrentRsList();
    Assertions.assertEquals(expectList, actualList);
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
    this.mockMvc.perform(post("/rs/add").content(ignoreAnnotationsMapper.writeValueAsString(newRsEvent)).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void should_got_bad_request_message_when_user_gender_has_error() throws Exception {
    curUser.setGender(null);
    RsEvent newRsEvent = new RsEvent("rs-new", "key", curUser);
    this.mockMvc.perform(post("/rs/add").content(ignoreAnnotationsMapper.writeValueAsString(newRsEvent)).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void should_got_bad_request_message_when_user_email_has_error() throws Exception {
    curUser.setEmail("aaaaaaaaa");
    RsEvent newRsEvent = new RsEvent("rs-new", "key", curUser);
    this.mockMvc.perform(post("/rs/add").content(ignoreAnnotationsMapper.writeValueAsString(newRsEvent)).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void should_got_bad_request_message_when_user_phone_has_error() throws Exception {
    curUser.setPhone("11111");
    RsEvent newRsEvent = new RsEvent("rs-new", "key", curUser);
    this.mockMvc.perform(post("/rs/add").content(ignoreAnnotationsMapper.writeValueAsString(newRsEvent)).contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }
}
