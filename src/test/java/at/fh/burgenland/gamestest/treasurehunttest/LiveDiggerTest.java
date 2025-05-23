package at.fh.burgenland.gamestest.treasurehunttest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import at.fh.burgenland.games.treasurehunt.LiveDigger;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link LiveDigger} class. Verifies that the {@code digLiveLine} method
 * interacts correctly with the {@link GraphicsContext} by performing drawing operations such as
 * {@code clearRect}, {@code setFill}, and {@code fillOval} for valid input, and does nothing for
 * out-of-range input.
 */
public class LiveDiggerTest {

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
  void testDrawLiveLineDrawsEraserAndPoint() {
    double[] lastX = {-1};
    double[] lastY = {-1};

    LiveDigger.digLiveLine(canvas, 201, 50, 100, 300, 30, 100, lastX, lastY);

    verify(gc, atLeastOnce()).clearRect(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    verify(gc, atLeastOnce()).setFill(any());
    verify(gc, atLeastOnce()).fillOval(anyDouble(), anyDouble(), anyDouble(), anyDouble());
  }

  @Test
  void testDrawLiveLineOutOfRangeDoesNothing() {
    double[] lastX = {-1};
    double[] lastY = {-1};

    LiveDigger.digLiveLine(canvas, 50, 20, 100, 300, 30, 100, lastX, lastY);

    verify(gc, never()).clearRect(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    verify(gc, never()).fillOval(anyDouble(), anyDouble(), anyDouble(), anyDouble());
  }
}
