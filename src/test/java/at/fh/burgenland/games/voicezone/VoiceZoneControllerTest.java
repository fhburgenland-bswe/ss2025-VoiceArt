package at.fh.burgenland.games.voicezone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link VoiceZoneController}. These tests verify the correctness of utility methods
 * such as range checks, mapping functionality, and training progress resets depending on the
 * selected mode.
 */
public class VoiceZoneControllerTest {

  private static boolean javafxInitialized = false;

  /**
   * Initializes the JavaFX platform before all tests are executed. This is required because some
   * methods inside {@link VoiceZoneController} rely on JavaFX.
   */
  @BeforeAll
  static void initJfx() throws Exception {
    if (!javafxInitialized) {
      javafxInitialized = true;
      try {
        Platform.startup(() -> {}); // start JavaFX thread
      } catch (IllegalStateException e) {
        // JavaFX is already running, safe to ignore
      }
    }
  }

  /** Verifies that the method correctly detects a value inside the tolerance range. */
  @Test
  void testIsInTargetRange_withinRange_shouldReturnTrue() {
    VoiceZoneController controller = new VoiceZoneController();
    assertTrue(controller.isInTargetRange(100.0, 105.0, 10.0));
  }

  /** Verifies that the method correctly detects a value outside the tolerance range. */
  @Test
  void testIsInTargetRange_outsideRange_shouldReturnFalse() {
    VoiceZoneController controller = new VoiceZoneController();
    assertFalse(controller.isInTargetRange(100.0, 120.0, 10.0));
  }

  /** Verifies that the mapping function correctly scales a value from one range to another. */
  @Test
  void testMap_shouldScaleCorrectly() {
    VoiceZoneController controller = new VoiceZoneController();
    double result = controller.map(50, 0, 100, 0, 200);
    assertEquals(100.0, result, 0.001);
  }

  /** Verifies that the initial level of a newly created controller is 1. */
  @Test
  void testInitialLevel_shouldBeOne() {
    VoiceZoneController controller = new VoiceZoneController();
    assertEquals(1, controller.getLevel());
  }

  /**
   * Verifies that calling {@code resetTrainingProgress} correctly resets the level and tolerance
   * when the training mode is set to VOLUME.
   */
  @Test
  void testResetTrainingProgress_shouldResetState() {
    VoiceZoneController controller = new VoiceZoneController();
    controller.setTrainingMode(VoiceZoneTrainingMode.VOLUME); // ändere Mode
    controller.resetTrainingProgress();

    assertEquals(1, controller.getLevel()); // Level zurückgesetzt
    assertEquals(5.0, controller.getCurrentTolerance(), 0.01); // Toleranz für Volume
  }

  /**
   * Verifies that calling {@code resetTrainingProgress} correctly resets the tolerance to 30.0 when
   * the training mode is set to FREQUENCY.
   */
  @Test
  void testResetTrainingProgress_frequency_shouldSetFreqTolerance() {
    VoiceZoneController controller = new VoiceZoneController();
    controller.setTrainingMode(VoiceZoneTrainingMode.FREQUENCY);

    assertEquals(30.0, controller.getCurrentTolerance(), 0.01);
  }
}
