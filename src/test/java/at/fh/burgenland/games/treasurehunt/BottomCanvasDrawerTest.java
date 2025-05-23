package at.fh.burgenland.games.treasurehunt;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link BottomCanvasDrawer} utility class. Verifies that the drawing methods
 * interact correctly with the {@link GraphicsContext} by checking calls to drawing operations such
 * as fillRect, fillText, strokeLine, and strokeOval.
 */
public class BottomCanvasDrawerTest {

  private Canvas canvas;
  private GraphicsContext gc;

  @BeforeEach
  void setUp() {
    canvas = mock(Canvas.class);
    gc = mock(GraphicsContext.class);
    when(canvas.getGraphicsContext2D()).thenReturn(gc);
    when(canvas.getWidth()).thenReturn(400.0);
    when(canvas.getHeight()).thenReturn(300.0);
  }

  @Test
  void testDrawCoveringLayerCallsFillRectAndFillText() {
    BottomCanvasDrawer.drawCoveringLayer(canvas);

    verify(gc, atLeastOnce()).fillRect(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    verify(gc, atLeastOnce())
        .fillText(contains("Singen oder sprechen Sie"), anyDouble(), anyDouble());
    verify(gc, atLeastOnce()).strokeLine(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    verify(gc, atLeastOnce()).strokeOval(anyDouble(), anyDouble(), anyDouble(), anyDouble());
  }

  @Test
  void testDrawTreasure() {
    BottomCanvasDrawer.drawTreasure(gc, 100, 100, 10, 20);

    verify(gc, atLeastOnce()).fillOval(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    verify(gc, atLeastOnce()).strokeOval(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    verify(gc, atLeastOnce()).fillText(contains("★"), anyDouble(), anyDouble());
  }
}
