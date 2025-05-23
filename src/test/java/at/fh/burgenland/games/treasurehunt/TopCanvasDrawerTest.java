package at.fh.burgenland.games.treasurehunt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link TopCanvasDrawer} class. Verifies that the {@code drawAxes} method
 * interacts correctly with the {@link GraphicsContext} by performing drawing operations such as
 * {@code setStroke}, {@code strokeLine}, {@code setFill}, {@code fillText}, and {@code fillRect}.
 */
public class TopCanvasDrawerTest {
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
  void testDrawAxesDrawsCoordinateSystem() {
    TopCanvasDrawer.drawAxes(canvas, 100, 1000, 30, 100);

    verify(gc, atLeastOnce()).setStroke(any());
    verify(gc, atLeastOnce()).strokeLine(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    verify(gc, atLeastOnce()).setFill(any());
    verify(gc, atLeastOnce()).fillText(any(), anyDouble(), anyDouble());
    verify(gc, atLeastOnce()).fillRect(anyDouble(), anyDouble(), anyDouble(), anyDouble());
  }
}
