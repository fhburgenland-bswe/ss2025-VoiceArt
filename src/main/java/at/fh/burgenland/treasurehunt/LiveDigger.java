package at.fh.burgenland.treasurehunt;

import at.fh.burgenland.coordinatesystem.CoordinateSystemDrawer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;

public class LiveDigger {

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
        width - BaseCanvasDrawer.PADDING_LEFT - BaseCanvasDrawer.PADDING_RIGHT;
    double plotHeight =
        height - BaseCanvasDrawer.PADDING_TOP - BaseCanvasDrawer.PADDING_BOTTOM;

    // converting Hz & dB to coordinates
    double x =
        BaseCanvasDrawer.PADDING_LEFT
            + ((pitch - minFreq) / (double) (maxFreq - minFreq)) * plotWidth;
    double y =
        BaseCanvasDrawer.PADDING_TOP + ((maxDb - db) / (double) (maxDb - minDb)) * plotHeight;

    GraphicsContext g = coordinateSystemCanvas.getGraphicsContext2D();

    double eraserSize = 10.0;


    if (lastX[0] >= 0 && lastY[0] >= 0) {
      double dx = x - lastX[0];
      double dy = y - lastY[0];
      double distance = Math.sqrt(dx * dx + dy * dy);

      // If the distance is significant, interpolate points along the path
      if (distance > eraserSize/2){
        int steps = (int) (distance / (eraserSize/4)) + 1;
        for (int i = 0; i <= steps; i++) {
          double t = (double) i / steps;
          double ix = lastX[0] + t * dx;
          double iy = lastY[0] + t * dy;
          g.clearRect(ix - eraserSize / 2, iy - eraserSize / 2, eraserSize, eraserSize);
        }
      }else{
        // For very close points, just draw a single eraser mark
        g.clearRect(x - eraserSize/2, y - eraserSize/2, eraserSize, eraserSize);
      }
    }else {
      // First point - just draw a circle
      g.clearRect(x - eraserSize/2, y - eraserSize/2, eraserSize, eraserSize);
    }
     //g.setGlobalBlendMode(BlendMode.SRC_OVER);

    lastX[0] = x;
    lastY[0] = y;
  }

}
