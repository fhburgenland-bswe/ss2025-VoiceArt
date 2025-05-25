package at.fh.burgenland.coordinatesystem;

import at.fh.burgenland.audioinput.AudioInputService;
import at.fh.burgenland.fft.FrequenzDbOutput;
import at.fh.burgenland.profiles.ProfileManager;
import at.fh.burgenland.profiles.UserProfile;
import at.fh.burgenland.profiles.VoiceProfile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.fh.burgenland.utils.SceneUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller class for the coordinate system view. This controller is responsible for:
 * -Initializing and dynamically resizing the canvas for drawing. -Handling the real-time drawing of
 * smoothed voice data (frequency and loudness). -Responding to user actions like starting/stopping
 * recording and switching scenes.
 *
 * <p>Reads the current user's voice profile and configures the canvas drawing range accordingly.
 *
 * <p>It uses the {@link FrequenzDbOutput} class to receive audio data and the {@link LiveDrawer}
 * utility to draw smooth lines on the canvas.
 */
public class CoordinateSystemController {

  @FXML private Canvas coordinateSystemCanvas;
  @FXML private Label logoLabel;
  @FXML private Label textLabel;
  @FXML private Button backButton;
  @FXML private Button exportButton;

  // Frequency and Loudness ranges - later on enums for voice profiles (male, female, children)
  private int minFreq;
  private int maxFreq;
  private int minDb;
  private int maxDb;

  // Holds the exponentially smoothed pitch value (Hz).
  // Initialized with -1 to indicate "no valid pitch received yet".
  private float smoothedPitch = -1;

  // Holds the exponentially smoothed decibel value (dB).
  // Initialized with negative infinity to represent "no valid dB value yet".
  private double smoothedDb = Double.NEGATIVE_INFINITY;

  // Smoothing factor for exponential smoothing of pitch and dB values.
  // A value closer to 1 gives more weight to new values (less smoothing),
  // while a value closer to 0 gives more weight to previous values (more smoothing).
  private final float smoothingFactor = 0.3f;

  /**
   * Represents a single data point of voice input containing pitch (Hz) and loudness (dB). Used to
   * store and redraw smoothed voice data for responsive canvas updates.
   */
  private static class VoicePoint {

    float pitch;
    double db;

    VoicePoint(float pitch, double db) {
      this.pitch = pitch;
      this.db = db;
    }
  }

  private final List<VoicePoint> recordedPoints = new ArrayList<>();

  private final AudioInputService audioInputService = AudioInputService.getInstance();
  private final FrequenzDbOutput recorder =
      new FrequenzDbOutput(audioInputService.getSelectedMixer());

  // Last X coordinate used in drawing, stored in a single element array for mutable access.
  private final double[] lastX = {-1};
  // Last Y coordinate used in drawing, stored in a single element array for mutable access.
  private final double[] lastY = {-1};

  /**
   * Initializes the UI and draws the coordinate system. This method binds the canvas size to the
   * window size, ensuring a responsive layout. It also triggers the initial drawing and sets up
   * listeners to redraw the coordinate system when the window is resized.
   */
  @FXML
  public void initialize() {

    UserProfile userProfile = ProfileManager.getCurrentProfile();
    if (userProfile != null) {
      VoiceProfile voiceProfile = userProfile.getVoiceProfile();
      minFreq = voiceProfile.getMinFreq();
      maxFreq = voiceProfile.getMaxFreq();
      minDb = voiceProfile.getMinDb();
      maxDb = voiceProfile.getMaxDb();
    } else {
      minFreq = 50;
      maxFreq = 1100;
      minDb = -60;
      maxDb = 0;
    }

    Platform.runLater(
        () -> {
          // dynamic bounding, canvas grows with the full window
          coordinateSystemCanvas
              .widthProperty()
              .bind(coordinateSystemCanvas.getScene().widthProperty().subtract(60));
          coordinateSystemCanvas
              .heightProperty()
              .bind(coordinateSystemCanvas.getScene().heightProperty().subtract(250));

          drawCoordinateSystemStructure();

          // draw at every size change
          coordinateSystemCanvas
              .widthProperty()
              .addListener((obs, oldVal, newVal) -> drawCoordinateSystemStructure());
          coordinateSystemCanvas
              .heightProperty()
              .addListener((obs, oldVal, newVal) -> drawCoordinateSystemStructure());
        });
  }

  /**
   * Redraws the background structure of the coordinate system (axes, labels). This method is called
   * whenever the window is resized.
   */
  private void drawCoordinateSystemStructure() {
    CoordinateSystemDrawer.drawAxes(coordinateSystemCanvas, minFreq, maxFreq, minDb, maxDb);

    // reset last coordinates
    lastX[0] = -1;
    lastY[0] = -1;

    // draw all existing points new
    for (VoicePoint point : recordedPoints) {
      LiveDrawer.drawLiveLine(
          coordinateSystemCanvas,
          point.pitch,
          point.db,
          minFreq,
          maxFreq,
          minDb,
          maxDb,
          lastX,
          lastY);
    }
  }

  /**
   * Starts the audio recording and analysis process.
   *
   * <p>This method sets a listener for incoming pitch and dB values, applies exponential smoothing
   * using the {@link ExponentialSmoother}, and draws the resulting smoothed line in real-time using
   * the {@link LiveDrawer}.
   */
  @FXML
  public void startRecording() {

    // Set up the listener to receive live frequency (Hz) and loudness (dB) data
    recorder.setListener(
        (pitch, db) -> {
          if (pitch > 0 && !Double.isInfinite(db)) {

            // Use the utility class for smoothing
            smoothedPitch = ExponentialSmoother.smooth(smoothedPitch, pitch, smoothingFactor);
            smoothedDb = ExponentialSmoother.smooth(smoothedDb, db, smoothingFactor);

            recordedPoints.add(new VoicePoint(smoothedPitch, smoothedDb));

            // Draw on canvas with smoothed values
            Platform.runLater(
                () ->
                    LiveDrawer.drawLiveLine(
                        coordinateSystemCanvas,
                        smoothedPitch,
                        smoothedDb,
                        minFreq,
                        maxFreq,
                        minDb,
                        maxDb,
                        lastX,
                        lastY));

            System.out.println("Pitch: " + pitch + " | dB: " + db);
          }
        });

    recorder.start();
  }

  /**
   * Stops the audio recording and drawing process.
   *
   * <p>Unregisters the audio listener and halts the processing thread from {@link
   * FrequenzDbOutput}.
   */
  @FXML
  public void stopRecording() {
    recorder.setListener(null); // stop drawing
    recorder.stop();
  }

  /**
   * Handles switching from the coordinatesystem scene to the 'start-scene' - landing.fxml. This
   * event is triggered by an UI-event, typically a button click.
   *
   * @param event The {@link javafx.event.ActionEvent} that triggers the scene switch
   * @throws IOException If the FXML file for the start scene cannot be loaded.
   */
  @FXML
  public void switchToStartScene(ActionEvent event) {
    SceneUtil.changeScene((Stage) ((Node) event.getSource()).getScene().getWindow(), "/at/fh/burgenland/landing.fxml");
  }
}
