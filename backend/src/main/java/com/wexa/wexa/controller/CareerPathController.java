package com.wexa.wexa.controller;

import com.wexa.wexa.service.CareerPathService;
import java.util.Collections;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller exposing REST endpoints for career path analytics. Delegates the implementation
 * directly to the service layer.
 */
@RestController
@RequestMapping("/api/career-path")
@CrossOrigin(origins = "*")
public class CareerPathController {

  private final CareerPathService careerPathService;

  /**
   * Constructs the controller with the CareerPathService.
   *
   * @param careerPathService the service to delegate operations to
   */
  public CareerPathController(CareerPathService careerPathService) {
    this.careerPathService = careerPathService;
  }

  /**
   * Endpoint to execute a skill gap analysis for a given person and job.
   *
   * @param person the name of the person
   * @param job the title of the target job
   * @return ResponseEntity wrapping the list of gap analysis results or 400 Bad Request
   */
  @GetMapping("/gap-analysis")
  public ResponseEntity<?> getGapAnalysis(
      @RequestParam("person") String person, @RequestParam("job") String job) {
    if (person == null || person.trim().isEmpty()) {
      return ResponseEntity.badRequest()
          .body(Collections.singletonMap("error", "Person name parameter cannot be blank"));
    }
    if (job == null || job.trim().isEmpty()) {
      return ResponseEntity.badRequest()
          .body(Collections.singletonMap("error", "Job title parameter cannot be blank"));
    }

    return ResponseEntity.ok(careerPathService.getSkillGap(person, job));
  }

  /**
   * Endpoint to retrieve the optimal learning path of skills for a given person and job.
   *
   * @param person the name of the person
   * @param job the title of the target job
   * @return ResponseEntity wrapping the list of path results or 400 Bad Request
   */
  @GetMapping("/shortest-path")
  public ResponseEntity<?> getShortestPath(
      @RequestParam("person") String person, @RequestParam("job") String job) {
    if (person == null || person.trim().isEmpty()) {
      return ResponseEntity.badRequest()
          .body(Collections.singletonMap("error", "Person name parameter cannot be blank"));
    }
    if (job == null || job.trim().isEmpty()) {
      return ResponseEntity.badRequest()
          .body(Collections.singletonMap("error", "Job title parameter cannot be blank"));
    }

    return ResponseEntity.ok(careerPathService.getShortestLearningPath(person, job));
  }
}
