package com.wexa.wexa;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HealthController provides a REST endpoint to monitor the health of the application and its
 * connectivity to the Neo4j database.
 */
@RestController
@CrossOrigin(origins = "*")
public class HealthController {

  private final Driver driver;
  private final String databaseName;

  /**
   * Constructs the controller with the auto-configured Neo4j Driver and database name.
   *
   * @param driver the Neo4j driver bean
   * @param databaseName the name of the database configured
   */
  public HealthController(
      Driver driver, @Value("${spring.data.neo4j.database:neo4j}") String databaseName) {
    this.driver = driver;
    this.databaseName = databaseName;
  }

  /**
   * Health check endpoint that validates database connectivity.
   *
   * @return JSON response detailing connectivity status or error details.
   */
  @GetMapping("/api/health")
  public ResponseEntity<Map<String, String>> getHealth() {
    try (Session session = driver.session()) {
      int statusValue =
          session.executeRead(
              tx -> {
                Result result = tx.run("RETURN 1 AS status");
                if (result.hasNext()) {
                  return result.next().get("status").asInt();
                }
                throw new IllegalStateException("Query did not return any status");
              });

      if (statusValue == 1) {
        Map<String, String> body = new HashMap<>();
        body.put("status", "connected");
        body.put("database", databaseName);
        return ResponseEntity.ok(body);
      } else {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(Collections.singletonMap("error", "Database returned unexpected value"));
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
          .body(
              Collections.singletonMap(
                  "error", "Database connectivity failure: " + e.getMessage()));
    }
  }
}
