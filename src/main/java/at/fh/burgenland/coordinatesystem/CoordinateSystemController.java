package at.fh.burgenland.coordinatesystem;

import at.fh.burgenland.audioinput.AudioInputService;
import at.fh.burgenland.fft.FrequenzDbOutput;
import at.fh.burgenland.logging.SessionLog;
import at.fh.burgenland.logging.SessionLogger;
import at.fh.burgenland.profiles.IfVoiceProfile;
import at.fh.burgenland.profiles.ProfileManager;
import at.fh.burgenland.profiles.UserProfile;
import at.fh.burgenland.utils.SceneUtil;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

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
  @FXML private ColorPicker colorPicker;

  // Frequency and Loudness ranges - later on enums for voice profiles (male, female, children)
  private int minFreq;
  private int maxFreq;
  private int minDb;
  private int maxDb;

  // Session statistics for the current game session
  private double sessionMaxDb = Double.NEGATIVE_INFINITY;
  private double sessionMinDb = Double.POSITIVE_INFINITY;
  private double sessionMaxHz = Double.NEGATIVE_INFINITY;
  private double sessionMinHz = Double.POSITIVE_INFINITY;
  private static final String LOG_FILE = "session_logs.json";

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

  @FXML private CheckBox recordingIndicator;

  /**
   * Sets the recording state of the application. This method updates the recording indicator to
   * visually reflect whether recording is active or not.
   *
   * @param isRecording true if recording is active, false otherwise
   */
  public void setRecording(boolean isRecording) {
    recordingIndicator.setSelected(isRecording);
  }

  /**
   * Represents a single data point of voice input containing pitch (Hz) and loudness (dB). Used to
   * store and redraw smoothed voice data for responsive canvas updates.
   */
  private static class VoicePoint {

    float pitch;
    double db;
    Color color;

    VoicePoint(float pitch, double db, Color color) {
      this.pitch = pitch;
      this.db = db;
      this.color = color;
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
      IfVoiceProfile voiceProfile = userProfile.getVoiceProfile();
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

    if (userProfile == null) {
      exportButton.setDisable(true);
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

    colorPicker.setValue(Color.BLACK);
  }

  /**
   * Redraws the background structure of the coordinate system (axes, labels). This method is called
   * whenever the window is resized.
   */
  private void drawCoordinateSystemStructure() {

    // NEU FÜR LOG SKALA
    double plotWidth =
        coordinateSystemCanvas.getWidth()
            - CoordinateSystemDrawer.PADDING_LEFT
            - CoordinateSystemDrawer.PADDING_RIGHT;

    LogScaleConverter.init(minFreq, maxFreq, plotWidth);

    // BIS HIER

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
          lastY,
          point.color);
    }
  }

  @FXML
  private void exportPicture(ActionEvent actionEvent) throws IOException {

    // Create folder
    File folder = new File(ProfileManager.getCurrentProfile().getUserName());
    if (!folder.exists()) {
      folder.mkdirs();
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    String timestamp = LocalDateTime.now().format(formatter);

    // Generate filename
    String filename = "FreeDraw_" + timestamp + ".png";
    // Take snapshot
    WritableImage image = coordinateSystemCanvas.snapshot(null, null);
    File outputFile = new File(folder, filename);

    try {
      ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputFile);
      System.out.println("Saved snapshot to: " + outputFile.getAbsolutePath());
    } catch (IOException e) {
      e.printStackTrace();
    }
    switchToStartScene(actionEvent);
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
    setRecording(true);
    // Set up the listener to receive live frequency (Hz) and loudness (dB) data
    recorder.setListener(
        (pitch, db) -> {
          if (pitch > 0 && !Double.isInfinite(db)) {

            // updates session statistics
            if (db > sessionMaxDb && db > -100) {
              sessionMaxDb = db;
            }
            if (db < sessionMinDb && db > -100) {
              sessionMinDb = db;
            }
            if (pitch > sessionMaxHz) {
              sessionMaxHz = pitch;
            }
            if (pitch < sessionMinHz) {
              sessionMinHz = pitch;
            }

            // Use the utility class for smoothing
            smoothedPitch = ExponentialSmoother.smooth(smoothedPitch, pitch, smoothingFactor);
            smoothedDb = ExponentialSmoother.smooth(smoothedDb, db, smoothingFactor);

            recordedPoints.add(new VoicePoint(smoothedPitch, smoothedDb, colorPicker.getValue()));

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
                        lastY,
                        colorPicker.getValue()));

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
    setRecording(false);
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
    this.stopRecording();
    // Log the session statistics if in FreeDraw mode
    if (ProfileManager.getCurrentProfile() != null) {
      SessionLog log = new SessionLog();
      log.setUsername(ProfileManager.getCurrentProfile().getUserName());
      log.setProfile(ProfileManager.getCurrentProfile().getVoiceProfile().toString());
      log.setGameName("FreeDraw");
      log.setTimestamp(LocalDateTime.now());
      log.setMaxDb(sessionMaxDb);
      log.setMinDb(sessionMinDb);
      log.setMaxHz(sessionMaxHz);
      log.setMinHz(sessionMinHz);

      try {
        SessionLogger.logSession(log, LOG_FILE);
      } catch (Exception e) {
        e.printStackTrace();
      }

      SceneUtil.changeScene(
          (Stage) ((Node) event.getSource()).getScene().getWindow(),
          "/at/fh/burgenland/game_selection.fxml");
    } else {
      // If no profile is selected, switch to the landing page (Debug Menu)
      SceneUtil.changeScene(
          (Stage) ((Node) event.getSource()).getScene().getWindow(),
          "/at/fh/burgenland/landing.fxml");
    }
  }
}
