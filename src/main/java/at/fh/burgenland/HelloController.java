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

  private FrequenzDbOutput recorder = new FrequenzDbOutput();

  @FXML
  private Label pitchLabel;

  @FXML
  private Label dbLabel;

  @FXML
  public void initialize() {
    recorder.setListener((pitch, db) -> {
      // This must run on the JavaFX Application Thread
      Platform.runLater(() -> {
        pitchLabel.setText(String.format("%.2f Hz", pitch));
        dbLabel.setText(String.format("%.2f dB", db));
      });
    });
  }

  @FXML
  public void onStartRecording() throws LineUnavailableException {
    recorder.start();
  }

  @FXML
  public void onStopRecording() {
    recorder.stop();
  }
}
