package at.fh.burgenland.coordinatesystem;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * CoordinateSystemDrawer is a utility class for drawing a labeled coordinate system on a canvas.
 */
public class CoordinateSystemDrawer {

  // padding - has to be done here, because it is exactly for the canvas (coordinate system) and
  // not for the general scene
  // also used for the drawLiveLine method
  public static final double PADDING_LEFT = 70;
  public static final double PADDING_TOP = 60;
  public static final double PADDING_RIGHT = 30;
  public static final double PADDING_BOTTOM = 60;

  /**
   * The method drawAxes() draws a coordinate system with labled axes for frequency (Hz) and volume
   * (dB). The canvas is padded to allow spacing for lables and axis titles.
   */
  public static void drawAxes(Canvas canvas, int minFreq, int maxFreq, int minDb, int maxDb) {
    GraphicsContext g = canvas.getGraphicsContext2D();
    double width = canvas.getWidth();
    double height = canvas.getHeight();

    System.out.println("Canvasgroesse: " + width + " x " + height);

    double plotWidth = width - PADDING_LEFT - PADDING_RIGHT;
    double plotHeight = height - PADDING_TOP - PADDING_BOTTOM;

    // background of the coordinate system
    g.setFill(Color.WHITE);
    g.fillRect(0, 0, width, height);

    // axis style
    g.setStroke(Color.LIGHTGRAY);
    g.setLineWidth(1);
    g.setFont(Font.font(10));
    g.setFill(Color.GRAY);

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

    // vertical lines (Hz)
    /*for (int hz = 100; hz <= maxFreq; hz += 300) {
      double x = PADDING_LEFT + ((hz - minFreq) / (double) (maxFreq - minFreq)) * plotWidth;
      g.strokeLine(x, PADDING_TOP, x, height - PADDING_BOTTOM);
      g.fillText(hz + " Hz", x - 20, height - PADDING_BOTTOM + 20);
    }*/

    // vertical lines (Hz)
    // dynamically rendering based on a given frequency and volume range
    int stepHz = (maxFreq - minFreq) / 6; // 6 Lables
    if (stepHz < 1) {
      stepHz = 1;
    }

    // URSPRÜNGLICHER CODE VOR LOG SKALA
    /*for (int hz = minFreq; hz <= maxFreq; hz += stepHz) {
      double x = PADDING_LEFT + ((hz - minFreq) / (double) (maxFreq - minFreq)) * plotWidth;
      g.strokeLine(x, PADDING_TOP, x, height - PADDING_BOTTOM);
      g.fillText(hz + " Hz", x - 20, height - PADDING_BOTTOM + 20);
    }*/
    // Anzahl der Achsenmarkierungen
    int numLabels = 6;
    LogScaleConverter.init(minFreq, maxFreq, plotWidth);

    for (int i = 0; i <= numLabels; i++) {
      double x = i * plotWidth / numLabels;
      double freq = LogScaleConverter.xToFreq(x);
      double absX = PADDING_LEFT + x;

      g.strokeLine(absX, PADDING_TOP, absX, height - PADDING_BOTTOM);
      g.fillText(String.format("%.0f Hz", freq), absX - 20, height - PADDING_BOTTOM + 20);
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
