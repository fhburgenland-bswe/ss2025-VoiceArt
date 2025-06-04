package at.fh.burgenland.logging;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link SessionLogger} utility class.
 *
 * <p>Verifies correct logging, file creation, appending, and cleanup of session logs. Each test
 * writes to a separate file and ensures test isolation by deleting files after execution.
 */
public class SessionLoggerTest {

  private SessionLogger logger;

  @BeforeEach
  void setUp() {
    logger = new SessionLogger();
  }

  @Test
  void testLogFileIsWrittenAndDeleted() {
    SessionLog log = new SessionLog();
    log.setUsername("testuser");
    log.setProfile("männlich");
    log.setGameName("VoiceZone");
    log.setTimestamp(LocalDateTime.now());
    log.setMaxDb(-10.0);
    log.setMinDb(-40.0);
    log.setMaxHz(400.0);
    log.setMinHz(20.0);

    String fileName = "test_session_logs.json";
    File file = new File(fileName);

    try {
      logger.logSession(log, fileName);

      assertTrue(file.exists());
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (file.exists()) {
        file.delete();
      }
    }
  }

  @Test
  void testLogSessionAppendsToFile() throws Exception {
    String fileName = "test_append_logs.json";
    File file = new File(fileName);
    file.delete(); // Ensure the file does not exist before the test

    try {
      SessionLog log1 = new SessionLog();
      log1.setUsername("user1");
      log1.setProfile("männlich");
      log1.setGameName("Game1");
      log1.setTimestamp(LocalDateTime.now());
      log1.setMaxDb(-10.0);
      log1.setMinDb(-40.0);
      log1.setMaxHz(400.0);
      log1.setMinHz(20.0);

      SessionLog log2 = new SessionLog();
      log2.setUsername("user2");
      log2.setProfile("weiblich");
      log2.setGameName("Game2");
      log2.setTimestamp(LocalDateTime.now());
      log2.setMaxDb(-12.0);
      log2.setMinDb(-42.0);
      log2.setMaxHz(420.0);
      log2.setMinHz(22.0);

      SessionLogger.logSession(log1, fileName);
      SessionLogger.logSession(log2, fileName);

      // read the file and verify contents
      ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
      List<SessionLog> logs = mapper.readValue(file, new TypeReference<List<SessionLog>>() {});
      assertTrue(logs.size() == 2);
      assertTrue(logs.get(0).getUsername().equals("user1"));
      assertTrue(logs.get(1).getUsername().equals("user2"));
    } finally {
      file.delete();
    }
  }

  @Test
  void testInvalidSessionIsNotLogged() throws Exception {
    String fileName = "test_invalid_log.json";
    File file = new File(fileName);
    if (file.exists()) {
      file.delete();
    }

    SessionLog invalidLog = new SessionLog();
    invalidLog.setUsername("invalid");
    invalidLog.setProfile("test");
    invalidLog.setGameName("GameX");
    invalidLog.setTimestamp(LocalDateTime.now());
    invalidLog.setMaxDb(Double.NEGATIVE_INFINITY);
    invalidLog.setMinDb(Double.POSITIVE_INFINITY);
    invalidLog.setMaxHz(Double.NaN);
    invalidLog.setMinHz(Double.NaN);

    SessionLogger.logSession(invalidLog, fileName);

    // file should not exist or should be empty
    assertTrue(!file.exists() || file.length() == 0);

    if (file.exists()) {
      file.delete();
    }
  }
}
