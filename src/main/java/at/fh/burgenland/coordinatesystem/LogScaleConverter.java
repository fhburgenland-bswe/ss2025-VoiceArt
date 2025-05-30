package at.fh.burgenland.coordinatesystem;

/**
 * The class LogScaleConverter converts frequency (Hz) and x-coordinates (pixel) on a logarithmic
 * scale. The frequency scale should be logarithmic instead of linear. The scale is based on the
 * natural logarithmic funciton. The class acts static and has to be initialized before usage.
 */
public class LogScaleConverter {

  private static double minFreq; // minimum frequency to be shown
  private static double maxFreq; // maximum frequency to be shown

  /**
   * logarithmic scale factor per pixel the frequency distribution can be mapped in the drawing
   * width
   */
  private static double lnBase;

  /**
   * Initializes the logarithmic scale with frequency are and drawing width
   *
   * @param minFreq the minimum frequency value to be displayed
   * @param maxFreq the maximum frequency value to be displayed
   * @param plotWidth the available drawing width in pixel (f.eg. the width from the x axis without
   *     padding)
   */
  public static void init(double minFreq, double maxFreq, double plotWidth) {
    LogScaleConverter.minFreq = minFreq;
    LogScaleConverter.maxFreq = maxFreq;

    // calculation from the natural logarithmic function for scaling per pixel
    lnBase = Math.log(maxFreq / minFreq) / plotWidth;
  }

  /**
   * Given frequency is converted into a x-coordinate (pixel)
   *
   * @param freq frequency value in Hz
   * @return the related x-position in the drawing area without padding
   */
  public static double freqToX(double freq) {
    return Math.log(freq / minFreq) / lnBase;
  }

  /**
   * Converts a given x-coordinate (pixels) back to the related frequency (Hz) Mostly used for the
   * axis naming
   *
   * @param x x-coordinate within the drawing area
   * @return calculated frequency in Hz on this position
   */
  public static double xToFreq(double x) {
    return minFreq * Math.exp(lnBase * x);
  }
}
