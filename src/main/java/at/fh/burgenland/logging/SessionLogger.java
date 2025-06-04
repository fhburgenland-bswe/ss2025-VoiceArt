package at.fh.burgenland.logging;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for logging session data to a JSON file. Handles serialization and appending of
 * {@link SessionLog} entries.
 */
public class SessionLogger {

  private static final ObjectMapper mapper =
      new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .enable(SerializationFeature.INDENT_OUTPUT)
          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

  /**
   * Appends a session log entry to the specified JSON file. If the file does not exist, it will be
   * created. If the file already contains logs, the new entry will be added to the list.
   *
   * @param log the session log entry to be saved
   * @param fileName the name of the file to write to
   * @throws Exception if an I/O or serialization error occurs
   */
  public static void logSession(SessionLog log, String fileName) throws Exception {

    if (!isValidLog(log)) {
      // invalid session does not get logged
      return;
    }

    File file = new File(fileName);
    List<SessionLog> logs = new ArrayList<>();

    if (file.exists() && file.length() > 0) {
      logs = mapper.readValue(file, new TypeReference<List<SessionLog>>() {});
    }

    logs.add(log);
    mapper.writeValue(file, logs);
  }

  private static boolean isValidLog(SessionLog log) {
    return Double.isFinite(log.getMaxDb())
        && Double.isFinite(log.getMinDb())
        && Double.isFinite(log.getMaxHz())
        && Double.isFinite(log.getMinHz());
  }
}
