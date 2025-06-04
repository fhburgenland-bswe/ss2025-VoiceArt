package at.fh.burgenland.logging;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a log entry for a game session. Stores user information, profile, game name,
 * timestamp, and session statistics.
 */
@Getter
@Setter
public class SessionLog {

  private String username;
  private String profile;
  private String gameName;
  private LocalDateTime timestamp;
  private double maxDb;
  private double minDb;
  private double maxHz;
  private double minHz;
}
