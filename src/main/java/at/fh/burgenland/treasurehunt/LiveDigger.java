package at.fh.burgenland.treasurehunt;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Utility class for drawing the live digging line on the coordinate system canvas in the treasure
 * hunt application. Visualizes the user's real-time pitch and loudness as a path and highlights the
 * current position.
 */
public class LiveDigger {

  /**
   * Draws a live "digging" line on the canvas based on the user's current pitch and loudness.
   * Clears a path between the previous and current points to simulate digging, and marks the
   * current position with a green dot.
   *
   * @param coordinateSystemCanvas the canvas to draw on
   * @param pitch the current smoothed pitch in Hz
   * @param db the current smoothed loudness in dB
   * @param minFreq the minimum frequency value for the coordinate system
   * @param maxFreq the maximum frequency value for the coordinate system
   * @param minDb the minimum dB value for the coordinate system
   * @param maxDb the maximum dB value for the coordinate system
   * @param lastX a mutable container holding the last x-coordinate (as a single-element array)
   * @param lastY a mutable container holding the last y-coordinate (as a single-element array)
   */
  public static void digLiveLine(
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

    double plotWidth = width - TopCanvasDrawer.PADDING_LEFT - TopCanvasDrawer.PADDING_RIGHT;
    double plotHeight = height - TopCanvasDrawer.PADDING_TOP - TopCanvasDrawer.PADDING_BOTTOM;

    // converting Hz & dB to coordinates
    double x =
        TopCanvasDrawer.PADDING_LEFT
            + ((pitch - minFreq) / (double) (maxFreq - minFreq)) * plotWidth;
    double y = TopCanvasDrawer.PADDING_TOP + ((maxDb - db) / (double) (maxDb - minDb)) * plotHeight;

    GraphicsContext g = coordinateSystemCanvas.getGraphicsContext2D();

    double eraserSize = 10.0;

    if (lastX[0] >= 0 && lastY[0] >= 0) {
      double dx = x - lastX[0];
      double dy = y - lastY[0];
      double distance = Math.sqrt(dx * dx + dy * dy);

      // If the distance is significant, interpolate points along the path
      if (distance > eraserSize / 2) {
        int steps = (int) (distance / (eraserSize / 4)) + 1;
        for (int i = 0; i <= steps; i++) {
          double t = (double) i / steps;
          double ix = lastX[0] + t * dx;
          double iy = lastY[0] + t * dy;
          g.clearRect(ix - eraserSize / 2, iy - eraserSize / 2, eraserSize, eraserSize);
        }
      } else {
        // For very close points, just draw a single eraser mark
        g.clearRect(x - eraserSize / 2, y - eraserSize / 2, eraserSize, eraserSize);
      }
    } else {
      // First point - just draw a circle
      g.clearRect(x - eraserSize / 2, y - eraserSize / 2, eraserSize, eraserSize);
    }
    // g.setGlobalBlendMode(BlendMode.SRC_OVER);

    // Grünen Punkt an aktueller Position zeichnen
    double pointRadius = 5.0;
    g.setFill(javafx.scene.paint.Color.LIMEGREEN);
    g.fillOval(x - pointRadius, y - pointRadius, pointRadius * 2, pointRadius * 2);

    lastX[0] = x;
    lastY[0] = y;
  }
}
