package at.fh.burgenland;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/** Controller class for the coordinate system view. */
public class CoordinateSystemController {

  @FXML private Canvas coordinateSystemCanvas;
  @FXML private Label logoLabel;
  @FXML private Label textLabel;
  @FXML private Button backButton;
  @FXML private Button exportButton;

  // Frequency and Loudness ranges
  private final int minFreq = 50;
  private final int maxFreq = 2000;
  private final int minDb = -60;
  private final int maxDb = 0;

  /**
   * Initializes the UI and draws the coordinate system. This method binds the canvas size to the
   * window size, ensuring a responsive layout. It also triggers the initial drawing and sets up
   * listeners to redraw the coordinate system when the window is resized.
   */
  @FXML
  public void initialize() {

    Platform.runLater(
        () -> {
          // dynamic bounding, canvas grows with the full window
          coordinateSystemCanvas
              .widthProperty()
              .bind(coordinateSystemCanvas.getScene().widthProperty().subtract(60));
          coordinateSystemCanvas
              .heightProperty()
              .bind(coordinateSystemCanvas.getScene().heightProperty().subtract(250));

          draw();

          // draw at every size change
          coordinateSystemCanvas.widthProperty().addListener((obs, oldVal, newVal) -> draw());
          coordinateSystemCanvas.heightProperty().addListener((obs, oldVal, newVal) -> draw());
        });
  }

  private void draw() {
    CoordinateSystemDrawer.drawAxes(coordinateSystemCanvas, minFreq, maxFreq, minDb, maxDb);
  }
}
