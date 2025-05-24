package at.fh.burgenland.coordinatesystem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests the logic for exponential smoothing. */
public class ExponentialSmootherTest {

  /**
   * Tests the smoothing of a float value when the previous value is -1, which is treated as an
   * "uninitialized" state.
   */
  @Test
  void testSmoothFloatInitialCase() {
    float result = ExponentialSmoother.smooth(-1, 100, 0.2f);
    assertEquals(100, result);
  }

  /**
   * Tests the smoothing of a float value under normal conditions with a previous value. Verifies
   * the correct weighted average is computed.
   */
  @Test
  void testSmoothFloatRegularCase() {
    float result = ExponentialSmoother.smooth(80, 100, 0.2f);
    assertEquals(84, result); // 0.2 * 100 + 0.8 * 80 = 84
  }

  /**
   * Tests the smoothing of a double value when the previous value is {@link
   * Double#NEGATIVE_INFINITY}, which is treated as an "uninitialized" state.
   */
  @Test
  void testSmoothDoubleInitialCase() {
    double result = ExponentialSmoother.smooth(Double.NEGATIVE_INFINITY, -30.0, 0.2f);
    assertEquals(-30.0, result);
  }

  /**
   * Tests the smoothing of a double value under normal conditions with a previous value. A delta is
   * used for precision in floating point comparison.
   */
  @Test
  void testSmoothDoubleRegularCase() {
    double result = ExponentialSmoother.smooth(-40.0, -30.0, 0.2f);
    assertEquals(-38.0, result, 0.0001); // 0.2 * -30 + 0.8 * -40 = -38 inkl. delta
  }
}
