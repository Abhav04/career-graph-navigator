package com.wexa.wexa;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.TransactionCallback;
import org.neo4j.driver.TransactionContext;
import org.neo4j.driver.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HealthController.class)
class HealthControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private Driver driver;

  private Session session;
  private TransactionContext transactionContext;
  private Result result;
  private Record record;
  private Value value;

  @BeforeEach
  void setUp() {
    session = Mockito.mock(Session.class);
    transactionContext = Mockito.mock(TransactionContext.class);
    result = Mockito.mock(Result.class);
    record = Mockito.mock(Record.class);
    value = Mockito.mock(Value.class);
  }

  @Test
  void testHealthCheck_Connected() throws Exception {
    when(driver.session()).thenReturn(session);
    when(session.executeRead(any()))
        .thenAnswer(
            invocation -> {
              TransactionCallback<?> callback = invocation.getArgument(0);
              return callback.execute(transactionContext);
            });
    when(transactionContext.run("RETURN 1 AS status")).thenReturn(result);
    when(result.hasNext()).thenReturn(true);
    when(result.next()).thenReturn(record);
    when(record.get("status")).thenReturn(value);
    when(value.asInt()).thenReturn(1);

    mockMvc
        .perform(get("/api/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("connected"));
  }

  @Test
  void testHealthCheck_UnexpectedQueryValue() throws Exception {
    when(driver.session()).thenReturn(session);
    when(session.executeRead(any()))
        .thenAnswer(
            invocation -> {
              TransactionCallback<?> callback = invocation.getArgument(0);
              return callback.execute(transactionContext);
            });
    when(transactionContext.run("RETURN 1 AS status")).thenReturn(result);
    when(result.hasNext()).thenReturn(true);
    when(result.next()).thenReturn(record);
    when(record.get("status")).thenReturn(value);
    when(value.asInt()).thenReturn(0); // unexpected value

    mockMvc
        .perform(get("/api/health"))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.error").value("Database returned unexpected value"));
  }

  @Test
  void testHealthCheck_DatabaseException() throws Exception {
    when(driver.session()).thenReturn(session);
    when(session.executeRead(any())).thenThrow(new RuntimeException("Connection refused"));

    mockMvc
        .perform(get("/api/health"))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.error").value("Database connectivity failure: Connection refused"));
  }
}
