package at.fh.burgenland.coordinatesystem;

/**
 * Utility class for applying exponential smoothing to numeric values. Used to smooth real-time data
 * like pitch and dB levels to create visually continuous and less jittery output in applications
 * such as audio visualization.
 */
public class ExponentialSmoother {

  /**
   * Applies exponential smoothing to a float value.
   *
   * @param previousValue the last smoothed value
   * @param newValue the new incoming raw value
   * @param factor the smoothing factor (0 < factor < 1)
   * @return the new smoothed value
   */
  public static float smooth(float previousValue, float newValue, float factor) {
    if (previousValue < 0) {
      return newValue;
    }
    ;
    return factor * newValue + (1 - factor) * previousValue;
  }

  /**
   * Applies exponential smoothing to a double value.
   *
   * @param previousValue the last smoothed value
   * @param newValue the new incoming raw value
   * @param factor the smoothing factor (0 < factor < 1)
   * @return the new smoothed value
   */
  public static double smooth(double previousValue, double newValue, float factor) {
    if (Double.isInfinite(previousValue)) {
      return newValue;
    }
    return factor * newValue + (1 - factor) * previousValue;
  }
}
