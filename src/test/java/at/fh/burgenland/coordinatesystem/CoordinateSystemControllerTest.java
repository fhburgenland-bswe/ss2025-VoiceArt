package at.fh.burgenland.coordinatesystem;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import at.fh.burgenland.fft.FrequenzDbOutput;
import java.lang.reflect.Field;
import javafx.scene.canvas.Canvas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link CoordinateSystemController}. These tests verify that the controller
 * correctly starts and stops audio recording by interacting with the {@link FrequenzDbOutput}
 * class.
 */
public class CoordinateSystemControllerTest {

  private CoordinateSystemController controller;
  private FrequenzDbOutput mockRecorder;

  @BeforeEach
  void setUp() throws Exception {
    controller = new CoordinateSystemController();
    mockRecorder = mock(FrequenzDbOutput.class);

    // Inject mock via reflection
    Field recorderField = CoordinateSystemController.class.getDeclaredField("recorder");
    recorderField.setAccessible(true);
    recorderField.set(controller, mockRecorder);

    // Dummy setup for required FXML fields to avoid NullPointerException
    Field canvasField = CoordinateSystemController.class.getDeclaredField("coordinateSystemCanvas");
    canvasField.setAccessible(true);
    canvasField.set(controller, mock(Canvas.class));
  }

  /**
   * Verifies that when {@code stopRecording()} is called, the recorder's {@code stop()} method is
   * invoked and the listener is removed.
   */
  @Test
  void testStopRecording_callsStopAndRemovesListener() {
    controller.stopRecording();

    verify(mockRecorder, times(1)).stop();
    verify(mockRecorder, times(1)).setListener(null);
  }
}
