package at.fh.burgenland.games.treasurehunt;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Utility class for drawing decorative and interactive elements on the bottom canvas of the
 * treasure hunt application.
 */
public class BottomCanvasDrawer {

  // padding - has to be done here, because it is exactly for the canvas (coordinate system) and
  // not for the general scene
  // also used for the drawLiveLine method
  public static final double PADDING_LEFT = 70;
  public static final double PADDING_TOP = 60;
  public static final double PADDING_RIGHT = 30;
  public static final double PADDING_BOTTOM = 60;

  /**
   * Draws the covering layer on the canvas, including background, hint text, decorative lines, and
   * treasure marks.
   *
   * @param canvas the canvas to draw on
   */
  public static void drawCoveringLayer(Canvas canvas) {
    GraphicsContext g = canvas.getGraphicsContext2D();
    double width = canvas.getWidth();
    double height = canvas.getHeight();

    System.out.println("Canvasgroesse: " + width + " x " + height);

    double plotWidth = width - PADDING_LEFT - PADDING_RIGHT;
    double plotHeight = height - PADDING_TOP - PADDING_BOTTOM;

    // background of the coordinate system
    g.setFill(Color.BLUE);
    g.fillRect(0, 0, width, height);

    String hintText = "Singen oder sprechen Sie, um zu graben!";
    g.setFill(Color.WHITE);
    g.setFont(Font.font(18));

    // text in center
    double textWidth = hintText.length() * 10;
    double textX = (width - textWidth) / 2;
    double textY = height / 2;

    g.fillText(hintText, textX, textY);

    // decoration
    g.setStroke(Color.GOLDENROD);
    g.setLineWidth(2);

    // Draw X mark for treasure spots
    drawTreasureMarks(g, width, height);

    // decorative dashes
    drawDecorativeLines(g, width, height);
  }

  /**
   * Draws X marks on the canvas to indicate possible treasure spots.
   *
   * @param g the graphics context to draw with
   * @param width the width of the canvas
   * @param height the height of the canvas
   */
  private static void drawTreasureMarks(GraphicsContext g, double width, double height) {

    double plotWidth = width - PADDING_LEFT - PADDING_RIGHT;
    double plotHeight = height - PADDING_TOP - PADDING_BOTTOM;

    // X mark 1 - top left area
    double x1 = PADDING_LEFT + plotWidth * 0.25;
    double y1 = PADDING_TOP + plotHeight * 0.25;
    drawXmark(g, x1, y1);

    // X mark 2 - bottom right area
    double x2 = PADDING_LEFT + plotWidth * 0.75;
    double y2 = PADDING_TOP + plotHeight * 0.75;
    drawXmark(g, x2, y2);

    // X mark 3 - center area
    double x3 = PADDING_LEFT + plotWidth * 0.5;
    double y3 = PADDING_TOP + plotHeight * 0.5;
    drawXmark(g, x3, y3);
  }

  /**
   * Draws a single X mark at the specified position.
   *
   * @param g the graphics context to draw with
   * @param x the x-coordinate of the center of the X mark
   * @param y the y-coordinate of the center of the X mark
   */
  private static void drawXmark(GraphicsContext g, double x, double y) {
    double size = 15;
    g.setStroke(Color.DARKRED);
    g.setLineWidth(3);

    g.strokeLine(x - size, y - size, x + size, y + size);
    g.strokeLine(x + size, y - size, x - size, y + size);

    g.strokeOval(x - size - 5, y - size - 5, (size + 5) * 2, (size + 5) * 2);
  }

  /** Draws decorative dashed lines on the canvas. */
  private static void drawDecorativeLines(GraphicsContext g, double width, double height) {
    double plotWidth = width - PADDING_LEFT - PADDING_RIGHT;
    double plotHeight = height - PADDING_TOP - PADDING_BOTTOM;

    g.setStroke(Color.GOLDENROD);
    g.setLineWidth(1.5);
    g.setLineDashes(10, 5);

    // Draw a curved path across the map
    g.beginPath();
    g.moveTo(PADDING_LEFT, PADDING_TOP + plotHeight * 0.7);
    g.bezierCurveTo(
        PADDING_LEFT + plotWidth * 0.3, PADDING_TOP + plotHeight * 0.3,
        PADDING_LEFT + plotWidth * 0.7, PADDING_TOP + plotHeight * 0.8,
        PADDING_LEFT + plotWidth, PADDING_TOP + plotHeight * 0.4);
    g.stroke();

    // Reset line dashes
    g.setLineDashes(null);
  }

  /**
   * Draws a treasure symbol at the specified position on the canvas. The treasure consists of two
   * concentric circles (outer and inner) and a star symbol in the center.
   *
   * @param g the graphics context to draw with
   * @param x the x-coordinate of the center of the treasure
   * @param y the y-coordinate of the center of the treasure
   * @param radiusInner the radius of the inner circle
   * @param radiusOuter the radius of the outer circle
   */
  public static void drawTreasure(
      GraphicsContext g, double x, double y, double radiusInner, double radiusOuter) {
    // outer radius
    g.setFill(Color.LIGHTGOLDENRODYELLOW);
    g.fillOval(x - radiusOuter, y - radiusOuter, radiusOuter * 2, radiusOuter * 2);
    g.setStroke(Color.GOLDENROD);
    g.setLineWidth(2);
    g.strokeOval(x - radiusOuter, y - radiusOuter, radiusOuter * 2, radiusOuter * 2);

    // Inner Radius
    g.setFill(Color.GOLD);
    g.fillOval(x - radiusInner, y - radiusInner, radiusInner * 2, radiusInner * 2);
    g.setStroke(Color.DARKGOLDENROD);
    g.setLineWidth(3);
    g.strokeOval(x - radiusInner, y - radiusInner, radiusInner * 2, radiusInner * 2);
    // Optional: Ein "€" oder "$" Symbol in die Mitte
    g.setFill(Color.BROWN);
    g.setFont(Font.font(18));
    g.fillText("★", x - 8, y + 7);
  }
}
