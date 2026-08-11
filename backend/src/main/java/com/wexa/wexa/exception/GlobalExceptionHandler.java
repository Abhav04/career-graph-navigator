package com.wexa.wexa.exception;

import java.util.HashMap;
import java.util.Map;
import org.neo4j.driver.exceptions.Neo4jException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler to intercept application-wide exceptions and return standard structured
 * HTTP error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Intercepts all Neo4jException and its subtypes, returning an HTTP 503 response.
   *
   * @param ex the database exception
   * @return ResponseEntity wrapping the structured error response
   */
  @ExceptionHandler(Neo4jException.class)
  public ResponseEntity<Map<String, String>> handleNeo4jException(Neo4jException ex) {
    Map<String, String> body = new HashMap<>();
    body.put("error", "Database unavailable");
    body.put("detail", ex.getMessage());
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
  }
}
