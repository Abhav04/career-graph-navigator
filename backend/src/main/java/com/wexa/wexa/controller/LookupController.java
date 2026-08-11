package com.wexa.wexa.controller;

import com.wexa.wexa.service.CareerPathService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controller exposing REST endpoints for basic list lookups (e.g. people, jobs). */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LookupController {

  private final CareerPathService careerPathService;

  /**
   * Constructs the controller with the CareerPathService.
   *
   * @param careerPathService the service to delegate lookup operations to
   */
  public LookupController(CareerPathService careerPathService) {
    this.careerPathService = careerPathService;
  }

  /**
   * Endpoint to retrieve a list of all person names.
   *
   * @return ResponseEntity wrapping the list of names
   */
  @GetMapping("/people")
  public ResponseEntity<List<String>> getPeople() {
    return ResponseEntity.ok(careerPathService.getAllPeopleNames());
  }

  /**
   * Endpoint to retrieve a list of all job titles.
   *
   * @return ResponseEntity wrapping the list of titles
   */
  @GetMapping("/jobs")
  public ResponseEntity<List<String>> getJobs() {
    return ResponseEntity.ok(careerPathService.getAllJobTitles());
  }
}
