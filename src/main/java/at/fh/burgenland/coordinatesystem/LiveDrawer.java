package at.fh.burgenland.coordinatesystem;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Utility class for drawing live audio data onto a canvas.
 *
 * <p>Converts smoothed pitch and dB values into visual coordinates and draws lines between them to
 * create a real-time voice visualization.
 */
public class LiveDrawer {

  /**
   * Draws a line between the previous and current smoothed values on the canvas.
   *
   * @param coordinateSystemCanvas the canvas to draw on
   * @param pitch the current smoothed pitch (Hz)
   * @param db the current smoothed loudness (dB)
   * @param minFreq the minimum frequency range
   * @param maxFreq the maximum frequency range
   * @param minDb the minimum dB range
   * @param maxDb the maximum dB range
   * @param lastX mutable container holding the last x-coordinate
   * @param lastY mutable container holding the last y-coordinate
   */
  public static void drawLiveLine(
      Canvas coordinateSystemCanvas,
      float pitch,
      double db,
      int minFreq,
      int maxFreq,
      int minDb,
      int maxDb,
      double[] lastX,
      double[] lastY) {

    // If values are outside the ranges return
    if (pitch < minFreq || pitch > maxFreq || db < minDb || db > maxDb) {
      return;
    }

    double width = coordinateSystemCanvas.getWidth();
    double height = coordinateSystemCanvas.getHeight();

    double plotWidth =
        width - CoordinateSystemDrawer.PADDING_LEFT - CoordinateSystemDrawer.PADDING_RIGHT;
    double plotHeight =
        height - CoordinateSystemDrawer.PADDING_TOP - CoordinateSystemDrawer.PADDING_BOTTOM;

    // converting Hz & dB to coordinates

    // VOR LOG SKALA
    /*double x =
    CoordinateSystemDrawer.PADDING_LEFT
        + ((pitch - minFreq) / (double) (maxFreq - minFreq)) * plotWidth;*/

    double x = CoordinateSystemDrawer.PADDING_LEFT + LogScaleConverter.freqToX(pitch);

    double y =
        CoordinateSystemDrawer.PADDING_TOP + ((maxDb - db) / (double) (maxDb - minDb)) * plotHeight;

    GraphicsContext g = coordinateSystemCanvas.getGraphicsContext2D();
    g.setStroke(javafx.scene.paint.Color.BLUE);
    g.setLineWidth(2);

    if (lastX[0] >= 0 && lastY[0] >= 0) {
      g.strokeLine(lastX[0], lastY[0], x, y);
    }

    lastX[0] = x;
    lastY[0] = y;
  }
}
