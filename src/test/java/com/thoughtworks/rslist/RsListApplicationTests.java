package com.thoughtworks.rslist;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RsListApplicationTests {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void should_return_rs_list () throws Exception {
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
        .andExpect(jsonPath("$", hasSize(2)))

        .andExpect(jsonPath("$[0].eventName", is("rs1")))
        .andExpect(jsonPath("$[1].eventName", is("rs2")))
        .andExpect(jsonPath("$[2].eventName", is("rs3")))

        .andExpect(jsonPath("$[0].key", is("key")))
        .andExpect(jsonPath("$[1].key", is("key")))
        .andExpect(jsonPath("$[2].key", is("key")))

        .andExpect(status().isOk());
  }
}
