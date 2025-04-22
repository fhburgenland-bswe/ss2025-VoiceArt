package at.fh.burgenland;

import at.fh.burgenland.fft.FrequenzDbOutput;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javax.sound.sampled.LineUnavailableException;

/** JavaFX example using for pipeline test. */
public class HelloController {

  @FXML private Label welcomeText;

  @FXML
  protected void onHelloButtonClick() {
    welcomeText.setText("Welcome to JavaFX Application!");
  }
}
