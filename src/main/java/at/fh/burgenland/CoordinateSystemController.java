package at.fh.burgenland;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;

/** Controller class for the coordinate system view. */
public class CoordinateSystemController {

  @FXML private Canvas canvas;

  // Frequency and Loudness ranges
  private final int minFreq = 50;
  private final int maxFreq = 2000;
  private final int minDb = -60;
  private final int maxDb = 0;

  @FXML
  public void initialize() {

    CoordinateSystemDrawer.drawAxes(canvas, minFreq, maxFreq, minDb, maxDb);
  }
}
