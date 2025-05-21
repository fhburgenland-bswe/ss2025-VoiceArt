package at.fh.burgenland.treasurehunt;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Utility class for drawing the coordinate system with labeled axes for frequency (Hz) and volume
 * (dB) on the top canvas of the treasure hunt application.
 */
public class TopCanvasDrawer {

  // padding - has to be done here, because it is exactly for the canvas (coordinate system) and
  // not for the general scene
  public static final double PADDING_LEFT = 70;
  public static final double PADDING_TOP = 60;
  public static final double PADDING_RIGHT = 30;
  public static final double PADDING_BOTTOM = 60;

  /**
   * Draws a coordinate system with labeled axes for frequency (Hz) and volume (dB). The canvas is
   * padded to allow spacing for labels and axis titles.
   *
   * @param canvas the canvas to draw on
   * @param minFreq the minimum frequency value for the x-axis (Hz)
   * @param maxFreq the maximum frequency value for the x-axis (Hz)
   * @param minDb the minimum dB value for the y-axis
   * @param maxDb the maximum dB value for the y-axis
   */
  public static void drawAxes(Canvas canvas, int minFreq, int maxFreq, int minDb, int maxDb) {
    GraphicsContext g = canvas.getGraphicsContext2D();
    double width = canvas.getWidth();
    double height = canvas.getHeight();

    System.out.println("Canvasgroesse: " + width + " x " + height);

    double plotWidth = width - PADDING_LEFT - PADDING_RIGHT;
    double plotHeight = height - PADDING_TOP - PADDING_BOTTOM;

    // background of the coordinate system
    g.setFill(Color.GRAY);
    g.fillRect(0, 0, width, height);

    // axis style
    g.setStroke(Color.WHITE);
    g.setLineWidth(1);
    g.setFont(Font.font(10));
    g.setFill(Color.WHITE);

    // title from the canvas
    String title = "Stimmfrequenz (Hz) vs. Lautstärke (dB)";
    Font titleFont = Font.font(16);
    g.setFont(titleFont);
    g.fillText(title, (width - title.length() * 7) / 2, PADDING_TOP - 20);

    g.setFont(Font.font(10));

    // horizontal lines (dB)
    for (int db = minDb; db <= maxDb; db += 10) {
      double y = PADDING_TOP + ((maxDb - db) / (double) (maxDb - minDb)) * plotHeight;
      g.strokeLine(PADDING_LEFT, y, width - PADDING_RIGHT, y);
      g.fillText(db + " dB", PADDING_LEFT - 50, y + 4);
    }

    int stepHz = (maxFreq - minFreq) / 6; // 6 Lables
    if (stepHz < 1) {
      stepHz = 1;
    }

    for (int hz = minFreq; hz <= maxFreq; hz += stepHz) {
      double x = PADDING_LEFT + ((hz - minFreq) / (double) (maxFreq - minFreq)) * plotWidth;
      g.strokeLine(x, PADDING_TOP, x, height - PADDING_BOTTOM);
      g.fillText(hz + " Hz", x - 20, height - PADDING_BOTTOM + 20);
    }

    // x-axis title
    g.setFont(Font.font(12));
    g.fillText("Frequenz (Hz)", PADDING_LEFT + plotWidth / 2 - 40, height - PADDING_BOTTOM + 40);

    // y-axis title
    g.save();
    g.translate(PADDING_LEFT - 60, PADDING_TOP + plotHeight / 2);
    g.rotate(-90);
    g.fillText("Lautstärke (dB)", 0, 0);
    g.restore();
  }
}
