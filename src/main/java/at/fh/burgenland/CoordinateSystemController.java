package at.fh.burgenland;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/** Controller class for the coordinate system view. */
public class CoordinateSystemController {

  @FXML private Canvas coordinateSystemCanvas;
  @FXML private Label logoLabel;
  @FXML private Label textLabel;
  @FXML private Button startButton;

  // Frequency and Loudness ranges
  private final int minFreq = 50;
  private final int maxFreq = 2000;
  private final int minDb = -60;
  private final int maxDb = 0;

  /** Initializes the UI and draws the coordinate system. */
  @FXML
  public void initialize() {
    logoLabel.getText();
    textLabel.getText();
    CoordinateSystemDrawer.drawAxes(coordinateSystemCanvas, minFreq, maxFreq, minDb, maxDb);
    startButton.getText();
  }
}
