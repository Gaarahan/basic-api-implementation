package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.component.RsExceptionHandler;
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
class RsControllerBasicTest {

  private MockMvc mockMvc;

  @BeforeEach
  private void setup () {
    this.mockMvc = MockMvcBuilders.standaloneSetup(new RsController())
        .setControllerAdvice(new RsExceptionHandler())
        .build();
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

        .andExpect(jsonPath("$", not(hasKey("user"))))
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

        .andExpect(jsonPath("$[0]", not(hasKey("user"))))

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
    String newRsEventStr = "{\"eventName\":\"rs-new\",\"key\":\"new\",\"user\":{\"user_name\":\"han\",\"user_age\":21,\"user_gender\":\"male\",\"user_email\":\"test@test.com\",\"user_phone\":\"13755556666\"}}";

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
}
