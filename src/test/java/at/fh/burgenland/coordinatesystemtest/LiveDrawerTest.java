package at.fh.burgenland.coordinatesystemtest;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import at.fh.burgenland.coordinatesystem.LiveDrawer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This class uses mocked JavaFX components (Canvas and GraphicsContext) to test the behavior of the
 * drawLiveLine method under different input conditions. It ensures that - no drawing happens if the
 * pitch is out of bounds - drawing occurs correctly when valid input is provided
 */
public class LiveDrawerTest {

  private Canvas canvas;
  private GraphicsContext graphics;

  private final int minFreq = 50;
  private final int maxFreq = 2000;
  private final int minDb = -60;
  private final int maxDb = 0;

  private final double[] lastX = {-1};
  private final double[] lastY = {-1};

  /**
   * Sets up a mocked Canvas and GraphicsContext before each test.
   *
   * <p>The canvas is mocked to have a fixed width and height of 800x600, and returns the mocked
   * GraphicsContext when requested.
   */
  @BeforeEach
  void setup() {
    canvas = mock(Canvas.class);
    graphics = mock(GraphicsContext.class);

    when(canvas.getWidth()).thenReturn(800.0);
    when(canvas.getHeight()).thenReturn(600.0);
    when(canvas.getGraphicsContext2D()).thenReturn(graphics);
  }

  /**
   * Verifies that no drawing is performed if the pitch is outside the valid range.
   *
   * <p>This test ensures that the method exits early and does not attempt to draw if the pitch
   * value is below the minimum frequency.
   */
  @Test
  void testOutOfRangePitch_NoDrawingOccurs() {
    LiveDrawer.drawLiveLine(canvas, 30f, -30.0, minFreq, maxFreq, minDb, maxDb, lastX, lastY);

    // Verifiziere, dass keine Zeichnung erfolgt
    verify(graphics, never()).strokeLine(anyDouble(), anyDouble(), anyDouble(), anyDouble());
  }

  /**
   * Verifies that drawing is performed when valid pitch and dB values are provided.
   *
   * <p>This test checks that the strokeLine method is called exactly once if the input data is
   * within the specified frequency and dB ranges.
   */
  @Test
  void testValidInput_DrawLineCalled() {
    lastX[0] = 100;
    lastY[0] = 100;

    LiveDrawer.drawLiveLine(canvas, 500f, -20.0, minFreq, maxFreq, minDb, maxDb, lastX, lastY);

    // Verifiziere, dass strokeLine genau einmal aufgerufen wird
    verify(graphics, times(1)).strokeLine(anyDouble(), anyDouble(), anyDouble(), anyDouble());
  }
}
