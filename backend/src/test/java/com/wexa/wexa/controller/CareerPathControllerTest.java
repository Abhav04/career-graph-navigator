package com.wexa.wexa.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wexa.wexa.exception.GlobalExceptionHandler;
import com.wexa.wexa.service.CareerPathService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.exceptions.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {CareerPathController.class, GlobalExceptionHandler.class})
class CareerPathControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private CareerPathService careerPathService;

  @Test
  void testGetGapAnalysis_Success() throws Exception {
    when(careerPathService.getSkillGap("Alice", "Senior Developer"))
        .thenReturn(List.of(Collections.singletonMap("skillName", "Python")));

    mockMvc
        .perform(get("/api/career-path/gap-analysis?person=Alice&job=Senior Developer"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].skillName").value("Python"));
  }

  @Test
  void testGetGapAnalysis_BadRequest_BlankPerson() throws Exception {
    mockMvc
        .perform(get("/api/career-path/gap-analysis?person= &job=Senior Developer"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Person name parameter cannot be blank"));
  }

  @Test
  void testGetGapAnalysis_BadRequest_BlankJob() throws Exception {
    mockMvc
        .perform(get("/api/career-path/gap-analysis?person=Alice&job="))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Job title parameter cannot be blank"));
  }

  @Test
  void testGetShortestPath_Success() throws Exception {
    when(careerPathService.getShortestLearningPath("Alice", "Senior Developer"))
        .thenReturn(List.of(Collections.singletonMap("targetSkillName", "Python")));

    mockMvc
        .perform(get("/api/career-path/shortest-path?person=Alice&job=Senior Developer"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].targetSkillName").value("Python"));
  }

  @Test
  void testGetShortestPath_BadRequest_BlankPerson() throws Exception {
    mockMvc
        .perform(get("/api/career-path/shortest-path?person= &job=Senior Developer"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Person name parameter cannot be blank"));
  }

  @Test
  void testGetShortestPath_BadRequest_BlankJob() throws Exception {
    mockMvc
        .perform(get("/api/career-path/shortest-path?person=Alice&job= "))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Job title parameter cannot be blank"));
  }

  @Test
  void testGlobalExceptionHandler_Neo4jException() throws Exception {
    when(careerPathService.getSkillGap("Alice", "Senior Developer"))
        .thenThrow(new ServiceUnavailableException("Neo4j database connection refused"));

    mockMvc
        .perform(get("/api/career-path/gap-analysis?person=Alice&job=Senior Developer"))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.error").value("Database unavailable"))
        .andExpect(jsonPath("$.detail").value("Neo4j database connection refused"));
  }
}
