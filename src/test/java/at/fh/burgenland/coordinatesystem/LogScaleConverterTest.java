package at.fh.burgenland.coordinatesystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the class LogScaleConverter. These tests should verify the correct baheviour of
 * logarithmic frequency-to-pixel conversions and back-conversion using natural logarithmic scaling.
 */
public class LogScaleConverterTest {

  /**
   * Initializes the LogScaleConverter with a standard frequency range and plot width before each
   * test. This ensures consistent test conditions.
   */
  @BeforeEach
  void setup() {
    LogScaleConverter.init(50, 1000, 800); // minFreq, maxFreq, pixelWidth
  }

  /**
   * Tests whether converting a frequency to X and then back to frequency returns the original value
   * (within tolerance). This verifies the correctness of the round-trip transformation.
   */
  @Test
  void testConversion() {
    double freq = 440;
    double x = LogScaleConverter.frequencyToX(freq);
    double convertedBack = LogScaleConverter.xcoordinateToFrequency(x);
    assertEquals(freq, convertedBack, 1.0);
  }

  /**
   * Tests the frequency-to-X conversion at the boundaries. Ensures that: - The minimum frequency
   * maps to pixel 0 - The maximum frequency maps to the full plot width
   */
  @Test
  void testConversionBounds() {
    assertEquals(0.0, LogScaleConverter.frequencyToX(50), 0.001, "Min frequency should map to x=0");
    assertEquals(
        800, LogScaleConverter.frequencyToX(1000), 0.001, "Max frequency should map to full width");
  }

  /**
   * Verifies that higher frequency intervals are mapped closer together than lower ones on a
   * logarithmic scale.
   */
  @Test
  void testLogScaleSpacingBehavior() {
    double x100 = LogScaleConverter.frequencyToX(100);
    double x200 = LogScaleConverter.frequencyToX(200);
    double x400 = LogScaleConverter.frequencyToX(400);
    double x800 = LogScaleConverter.frequencyToX(800);

    double deltaLow = x200 - x100; // Abstand im unteren Bereich
    double deltaHigh = x800 - x400; // Abstand im höheren Bereich

    assertTrue(deltaHigh < deltaLow, "High-frequency steps should be narrower on a log scale");
  }
}
