package at.fh.burgenland.coordinatesystem;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * CoordinateSystemDrawer is a utility class for drawing a labeled coordinate system on a canvas.
 */
public class CoordinateSystemDrawer {

  /**
   * The method drawAxes() draws a coordinate system with labled axes for frequency (Hz) and volume
   * (dB). The canvas is padded to allow spacing for lables and axis titles.
   */
  public static void drawAxes(Canvas canvas, int minFreq, int maxFreq, int minDb, int maxDb) {
    GraphicsContext g = canvas.getGraphicsContext2D();
    double width = canvas.getWidth();
    double height = canvas.getHeight();

    System.out.println("Canvasgroesse: " + width + " x " + height);

    // padding - has to be done here, because it is exactly for the canvas (coordinate system) and
    // not for the general scene
    double paddingLeft = 70;
    double paddingTop = 60;
    double paddingRight = 30;
    double paddingBottom = 60;

    double plotWidth = width - paddingLeft - paddingRight;
    double plotHeight = height - paddingTop - paddingBottom;

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
    g.fillText(title, (width - title.length() * 7) / 2, paddingTop - 20);

    g.setFont(Font.font(10));

    // horizontal lines (dB)
    for (int db = minDb; db <= maxDb; db += 10) {
      double y = paddingTop + ((maxDb - db) / (double) (maxDb - minDb)) * plotHeight;
      g.strokeLine(paddingLeft, y, width - paddingRight, y);
      g.fillText(db + " dB", paddingLeft - 50, y + 4);
    }

    // vertical lines (Hz)
    for (int hz = 100; hz <= maxFreq; hz += 300) {
      double x = paddingLeft + ((hz - minFreq) / (double) (maxFreq - minFreq)) * plotWidth;
      g.strokeLine(x, paddingTop, x, height - paddingBottom);
      g.fillText(hz + " Hz", x - 20, height - paddingBottom + 20);
    }

    // x-axis title
    g.setFont(Font.font(12));
    g.fillText("Frequenz (Hz)", paddingLeft + plotWidth / 2 - 40, height - paddingBottom + 40);

    // y-axis title
    g.save();
    g.translate(paddingLeft - 60, paddingTop + plotHeight / 2);
    g.rotate(-90);
    g.fillText("Lautstärke (dB)", 0, 0);
    g.restore();
  }
}
