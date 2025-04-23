package at.fh.burgenland;

import at.fh.burgenland.audioinput.AudioInputService;
import at.fh.burgenland.fft.FrequenzDbOutput;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/** JavaFX example using for pipeline test. */
public class HelloController {

  @FXML private Label welcomeText;

  private final AudioInputService audioInputService = AudioInputService.getInstance();

  private FrequenzDbOutput recorder = new FrequenzDbOutput(audioInputService.getSelectedMixer());

  @FXML private Label pitchLabel;

  @FXML private Label dbLabel;

  /**
   * Initializes the controller by setting up a FrequencyDbListener for data updates. The listener
   * receives pitch and dB values and updates the corresponding JavaFX labels with the updated data.
   * Additionally, this method retrieves the selected Mixer from the AudioInputService and prints
   * its string representation to the console.
   */
  @FXML
  public void initialize() {
    recorder.setListener(
        (pitch, db) -> {
          Platform.runLater(
              () -> {
                pitchLabel.setText(String.format("%.2f Hz", pitch));
                dbLabel.setText(String.format("%.2f dB", db));
              });
        });
  }

  /**
   * Starts recording audio input by initiating the audio processing and analysis. The method calls
   * the start method of the Recorder instance, which sets up an audio dispatcher depending on the
   * input source type (microphone or file) and starts the audio processing on a new thread. Throws
   * exceptions LineUnavailableException, IOException, and UnsupportedAudioFileException if
   * initialization fails.
   */
  @FXML
  public void onStartRecording() {
    recorder.start();
  }

  /**
   * Stops the recording process by calling the {@link FrequenzDbOutput#stop()} method, which in
   * turn stops the audio processing and analysis if it is currently running. If the process is not
   * running, this method will return without taking any action.
   */
  @FXML
  public void onStopRecording() {
    recorder.stop();
  }

  @FXML
  protected void onHelloButtonClick() {
    welcomeText.setText("Welcome to JavaFX Application!");
  }
}
