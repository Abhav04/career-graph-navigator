package com.wexa.wexa.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wexa.wexa.service.CareerPathService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LookupController.class)
class LookupControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private CareerPathService careerPathService;

  @Test
  void testGetPeople() throws Exception {
    when(careerPathService.getAllPeopleNames()).thenReturn(List.of("Alice", "Bob"));

    mockMvc
        .perform(get("/api/people"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0]").value("Alice"))
        .andExpect(jsonPath("$[1]").value("Bob"));
  }

  @Test
  void testGetJobs() throws Exception {
    when(careerPathService.getAllJobTitles()).thenReturn(List.of("Software Engineer", "Architect"));

    mockMvc
        .perform(get("/api/jobs"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0]").value("Software Engineer"))
        .andExpect(jsonPath("$[1]").value("Architect"));
  }
}
