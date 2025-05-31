package at.fh.burgenland.games.hitthepoints;

import at.fh.burgenland.audioinput.AudioInputService;
import at.fh.burgenland.coordinatesystem.CoordinateSystemDrawer;
import at.fh.burgenland.coordinatesystem.ExponentialSmoother;
import at.fh.burgenland.coordinatesystem.LiveDrawer;
import at.fh.burgenland.coordinatesystem.LogScaleConverter;
import at.fh.burgenland.fft.FrequenzDbOutput;
import at.fh.burgenland.profiles.IfVoiceProfile;
import at.fh.burgenland.profiles.ProfileManager;
import at.fh.burgenland.profiles.UserProfile;
import at.fh.burgenland.utils.SceneUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

/** Controller class for the HitThePoints game. This class handles the game logic */
public class HitThePointsController {

  @FXML public Canvas gameCanvas;

  @FXML Canvas resultCanvas;

  @FXML private Label scoreLabel;

  @FXML private Label usernameLabel;
  @FXML private Label profileLabel;

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

  private int score = 0;
  private double circleX;
  private double circleY;
  private double circleRadius = 60;

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

  private final List<HitThePointsController.VoicePoint> recordedPoints = new ArrayList<>();

  private final AudioInputService audioInputService = AudioInputService.getInstance();
  private FrequenzDbOutput recorder;

  // Last X coordinate used in drawing, stored in a single element array for mutable access.
  private final double[] lastX = {-1};
  // Last Y coordinate used in drawing, stored in a single element array for mutable access.
  private final double[] lastY = {-1};

  /** Initializes the controller. This method is called after the FXML file has been loaded. */
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

    drawCoordinateSystemStructure(resultCanvas);
    spawnNewCircle();
    updateScoreLabel();
    setUserInfo(
        ProfileManager.getCurrentProfile().getUserName(),
        ProfileManager.getCurrentProfile().getVoiceProfile().getClass().getSimpleName());
  }

  /**
   * Starts the audio recording and drawing process. This method sets up the audio input service,
   * initializes the recorder, and starts listening for audio data.
   *
   * <p>It also sets up a listener to receive live frequency (Hz) and loudness (dB) data, which is
   * used to update the canvas in real-time.
   */
  @FXML
  public void startRecording() {
    this.setRecording(true);
    if (recorder == null) {
      recorder = new FrequenzDbOutput(audioInputService.getSelectedMixer());

      // Set up the listener to receive live frequency (Hz) and loudness (dB) data
      recorder.setListener(
          (pitch, db) -> {
            if (pitch > 0 && !Double.isInfinite(db)) {

              // Use the utility class for smoothing
              smoothedPitch = ExponentialSmoother.smooth(smoothedPitch, pitch, smoothingFactor);
              smoothedDb = ExponentialSmoother.smooth(smoothedDb, db, smoothingFactor);

              recordedPoints.add(new HitThePointsController.VoicePoint(smoothedPitch, smoothedDb));

              // Draw on canvas with smoothed values
              Platform.runLater(
                  () ->
                      LiveDrawer.drawLiveLine(
                          gameCanvas,
                          smoothedPitch,
                          smoothedDb,
                          minFreq,
                          maxFreq,
                          minDb,
                          maxDb,
                          lastX,
                          lastY));
              System.out.println("Pitch: " + pitch + " | dB: " + db);

              double width1 = gameCanvas.getWidth();
              double height1 = gameCanvas.getHeight();
              double plotWidth =
                  width1
                      - CoordinateSystemDrawer.PADDING_LEFT
                      - CoordinateSystemDrawer.PADDING_RIGHT;
              double plotHeight =
                  height1
                      - CoordinateSystemDrawer.PADDING_TOP
                      - CoordinateSystemDrawer.PADDING_BOTTOM;

              // NEU MIT LOG SKALA
              LogScaleConverter.init(minFreq, maxFreq, plotWidth);
              double x =
                  CoordinateSystemDrawer.PADDING_LEFT
                      + LogScaleConverter.frequencyToX(smoothedPitch);
              /*double x =
              CoordinateSystemDrawer.PADDING_LEFT
                  + ((smoothedPitch - minFreq) / (double) (maxFreq - minFreq)) * plotWidth;*/
              double y =
                  CoordinateSystemDrawer.PADDING_TOP
                      + ((maxDb - smoothedDb) / (double) (maxDb - minDb)) * plotHeight;

              double dx = x - circleX;
              double dy = y - circleY;
              double distance = Math.sqrt(dx * dx + dy * dy);

              if (distance <= circleRadius + 5) {
                recordedPoints.clear();
                score++;
                updateScoreLabel();
                drawResultCircle(true);
                spawnNewCircle();
              }
            }
          });
      recorder.start();
    }
  }

  /**
   * Redraws the background structure of the coordinate system (axes, labels). This method is called
   * whenever the window is resized.
   */
  private void drawCoordinateSystemStructure(Canvas canvas) {
    CoordinateSystemDrawer.drawAxes(canvas, minFreq, maxFreq, minDb, maxDb);

    // reset last coordinates
    lastX[0] = -1;
    lastY[0] = -1;

    // draw all existing points new
    for (HitThePointsController.VoicePoint point : recordedPoints) {
      LiveDrawer.drawLiveLine(
          canvas, point.pitch, point.db, minFreq, maxFreq, minDb, maxDb, lastX, lastY);
    }
  }

  /**
   * Stops the audio recording and drawing process.
   *
   * <p>Unregisters the audio listener and halts the processing thread from {@link
   * FrequenzDbOutput}.
   */
  @FXML
  public void stopRecording() {
    this.setRecording(false);
    if (recorder != null) {
      recorder.setListener(null);
      recorder.stop();
      recorder = null;
    }
  }

  /**
   * Sets the user and profile information in the UI.
   *
   * @param username the user's name
   * @param profileName the name of the selected voice profile
   */
  public void setUserInfo(String username, String profileName) {
    UserProfile userProfile = ProfileManager.getCurrentProfile();
    if (usernameLabel != null) {
      usernameLabel.setText("Benutzer: " + username);
    }
    if (profileLabel != null) {
      profileLabel.setText("Stimmprofil: " + userProfile.getVoiceProfile().toString());
    }
  }

  /**
   * leads the user the to result screen after the game is finished.
   *
   * @param event from the mouse click
   */
  @FXML
  private void switchToResultScreen(ActionEvent event) throws IOException {
    stopRecording();

    FXMLLoader loader =
        new FXMLLoader(getClass().getResource("/at/fh/burgenland/hitpoints_result.fxml"));
    Parent resultRoot = loader.load();

    HitThePointsResultController resultController = loader.getController();
    resultController.setScore(score); // your current score
    resultController.setCanvas(resultCanvas); // pass the actual canvas

    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    Scene scene = new Scene(resultRoot);
    scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
    scene
        .getStylesheets()
        .add(SceneUtil.class.getResource("/at/fh/burgenland/styles.css").toExternalForm());
    stage.setScene(scene);
    stage.show();
  }

  private void spawnNewCircle() {
    double canvasWidth = gameCanvas.getWidth();
    double canvasHeight = gameCanvas.getHeight();

    // Adjust circle radius based on score
    circleRadius = Math.max(circleRadius - 4, 18);
    double adjustedRadius = circleRadius;

    circleX = ThreadLocalRandom.current().nextDouble(adjustedRadius, canvasWidth - adjustedRadius);
    circleY = ThreadLocalRandom.current().nextDouble(adjustedRadius, canvasHeight - adjustedRadius);

    drawCircle(adjustedRadius);
  }

  private void drawCircle(double radius) {
    var gc = gameCanvas.getGraphicsContext2D();
    Platform.runLater(
        () -> {
          gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
          drawCoordinateSystemStructure(gameCanvas);
          gc.fillOval(circleX - radius, circleY - radius, radius * 2, radius * 2);
        });
  }

  private void drawResultCircle(boolean passed) {
    double adjustedRadius = circleRadius;

    var resultGc = resultCanvas.getGraphicsContext2D();
    resultGc.setFill(passed ? Color.GREEN : Color.RED);
    resultGc.fillOval(
        circleX - adjustedRadius, circleY - adjustedRadius, adjustedRadius * 2, adjustedRadius * 2);
  }

  private void updateScoreLabel() {
    Platform.runLater(() -> scoreLabel.setText("Score: " + score));
  }

  /**
   * Handles the action when the user clicks on the "Skip Point" button. This method clears the
   * result circle and spawns a new circle for the user to hit.
   *
   * @param event the action event triggered by the user
   */
  public void skipPoint(ActionEvent event) {
    drawResultCircle(false);
    recordedPoints.clear();
    spawnNewCircle();
  }

  /**
   * Switches to the game selection screen after stopping the recording. This method is called when
   * the user wants to return to the game selection menu.
   *
   * @param event the action event triggered by the user
   * @throws IOException if an error occurs while loading the FXML file
   */
  public void switchToGameSelection(ActionEvent event) throws IOException {
    stopRecording();
    SceneUtil.changeScene(
        (Stage) ((Node) event.getSource()).getScene().getWindow(),
        "/at/fh/burgenland/game_selection.fxml");
  }
}
