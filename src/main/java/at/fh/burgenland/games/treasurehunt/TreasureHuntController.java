package at.fh.burgenland.games.treasurehunt;

import at.fh.burgenland.audioinput.AudioInputService;
import at.fh.burgenland.coordinatesystem.ExponentialSmoother;
import at.fh.burgenland.fft.FrequenzDbOutput;
import at.fh.burgenland.profiles.ProfileManager;
import at.fh.burgenland.profiles.UserProfile;
import at.fh.burgenland.profiles.VoiceProfile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import at.fh.burgenland.utils.SceneUtil;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller for the Treasure Hunt game scene. Handles user interaction, audio input, drawing on
 * canvases, level progression, and navigation between scenes.
 */
public class TreasureHuntController {

  @FXML private Canvas baseCanvas;
  @FXML private Canvas overlapCanvas;

  @FXML private Label logoLabel;
  @FXML private Label textLabel;
  @FXML private Button backButton;
  @FXML private Button exportButton;
  @FXML private Label levelLabel;
  @FXML private Label usernameLabel;
  @FXML private Label profileLabel;

  // Frequency and Loudness ranges - later on enums for voice profiles (male, female, children)
  private int minFreq;
  private int maxFreq;
  private int minDb;
  private int maxDb;

  private double treasureX = 300;
  private double treasureY = 300;
  private double treasureRelX = 0.5; // 0.0 bis 1.0
  private double treasureRelY = 0.5;
  private double treasureRadiusInner = 50;
  private double treasureRadiusOuter = 100;
  private boolean treasureFound = false;
  private int level = 1;
  private final int maxLevel = 10;

  /**
   * Returns the X coordinate of the treasure.
   *
   * @return the X coordinate
   */
  public double getTreasureX() {
    return treasureX;
  }

  /**
   * Returns the Y coordinate of the treasure.
   *
   * @return the Y coordinate
   */
  public double getTreasureY() {
    return treasureY;
  }

  /**
   * Returns the inner radius of the treasure.
   *
   * @return the inner radius
   */
  public double getTreasureRadius() {
    return treasureRadiusInner;
  }

  /**
   * Returns the outer radius of the treasure.
   *
   * @return the outer radius
   */
  public double getTreasureRadiusOuter() {
    return treasureRadiusOuter;
  }

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

  private final List<TreasureHuntController.VoicePoint> recordedPoints = new ArrayList<>();

  private final AudioInputService audioInputService = AudioInputService.getInstance();
  private FrequenzDbOutput recorder;

  // Last X coordinate used in drawing, stored in a single element array for mutable access.
  private final double[] lastX = {-1};
  // Last Y coordinate used in drawing, stored in a single element array for mutable access.
  private final double[] lastY = {-1};

  /** Updates the treasure radii based on the current canvas size and level. */
  private void updateTreasureRadii() {
    double width = baseCanvas.getWidth();
    double height = baseCanvas.getHeight();
    double minSide = Math.min(width, height);

    // Beispiel: Start bei 20%/10%, pro Level -2%/-1%
    double outerPercent = Math.max(0.05, 0.20 - (level - 1) * 0.02);
    double innerPercent = Math.max(0.02, 0.10 - (level - 1) * 0.01);

    treasureRadiusOuter = minSide * outerPercent;
    treasureRadiusInner = minSide * innerPercent;
  }

  /** Updates the level label in the UI. */
  private void updateLevelLabel() {
    if (levelLabel != null) {
      levelLabel.setText("Level: " + level);
    }
  }

  /**
   * Sets the user and profile information in the UI.
   *
   * @param username the user's name
   * @param profileName the name of the selected voice profile
   */
  public void setUserInfo(String username, String profileName) {
    if (usernameLabel != null) {
      usernameLabel.setText("  Benutzer: " + username);
    }
    if (profileLabel != null) {
      profileLabel.setText("  Stimmprofil: " + profileName);
    }
  }

  /**
   * Initializes the controller, sets up canvas bindings, and draws the initial coordinate system.
   * Loads user profile and voice profile settings if available.
   */
  public void initialize() {

    UserProfile userProfile = ProfileManager.getCurrentProfile();
    if (userProfile != null) {
      VoiceProfile voiceProfile = userProfile.getVoiceProfile();
      setUserInfo(userProfile.getUserName(), userProfile.getVoiceProfile().toString());
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

          baseCanvas.widthProperty().bind(baseCanvas.getScene().widthProperty().subtract(60));
          baseCanvas.heightProperty().bind(baseCanvas.getScene().heightProperty().subtract(250));

          overlapCanvas.widthProperty().bind(baseCanvas.getScene().widthProperty().subtract(60));
          overlapCanvas.heightProperty().bind(baseCanvas.getScene().heightProperty().subtract(250));

          drawCoordinateSystemStructure();

          // draw at every size change
          baseCanvas
              .widthProperty()
              .addListener((obs, oldVal, newVal) -> drawCoordinateSystemStructure());
          baseCanvas
              .heightProperty()
              .addListener((obs, oldVal, newVal) -> drawCoordinateSystemStructure());
        });
  }

  /**
   * Redraws the background structure of the coordinate system (axes, labels, treasure). Called
   * whenever the window is resized or the treasure position changes.
   */
  private void drawCoordinateSystemStructure() {
    updateTreasureRadii();

    TopCanvasDrawer.drawAxes(overlapCanvas, minFreq, maxFreq, minDb, maxDb);
    // OverlapCanvasDrawer.drawAxes(baseCanvas, minFreq, maxFreq, minDb, maxDb);

    BottomCanvasDrawer.drawCoveringLayer(baseCanvas);

    double width = baseCanvas.getWidth();
    double height = baseCanvas.getHeight();
    double minX = TopCanvasDrawer.PADDING_LEFT + treasureRadiusOuter;
    double maxX = width - TopCanvasDrawer.PADDING_RIGHT - treasureRadiusOuter;
    double minY = TopCanvasDrawer.PADDING_TOP + treasureRadiusOuter;
    double maxY = height - TopCanvasDrawer.PADDING_BOTTOM - treasureRadiusOuter;

    treasureX = minX + treasureRelX * (maxX - minX);
    treasureY = minY + treasureRelY * (maxY - minY);

    BottomCanvasDrawer.drawTreasure(
        baseCanvas.getGraphicsContext2D(),
        treasureX,
        treasureY,
        treasureRadiusInner,
        treasureRadiusOuter);

    // reset last coordinates
    lastX[0] = -1;
    lastY[0] = -1;

    // draw all existing points new
    for (TreasureHuntController.VoicePoint point : recordedPoints) {
      LiveDigger.digLiveLine(
          overlapCanvas, /// ?????
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
   * Starts the audio recording and analysis process. Sets a listener for incoming pitch and dB
   * values, applies exponential smoothing, and draws the resulting smoothed line in real-time.
   */
  @FXML
  public void startRecording() {

    if (recorder == null) {
      recorder = new FrequenzDbOutput(audioInputService.getSelectedMixer());

      // Set up the listener to receive live frequency (Hz) and loudness (dB) data
      recorder.setListener(
          (pitch, db) -> {
            if (pitch > 0 && !Double.isInfinite(db)) {

              // Use the utility class for smoothing
              smoothedPitch = ExponentialSmoother.smooth(smoothedPitch, pitch, smoothingFactor);
              smoothedDb = ExponentialSmoother.smooth(smoothedDb, db, smoothingFactor);

              recordedPoints.add(new TreasureHuntController.VoicePoint(smoothedPitch, smoothedDb));

              // Draw on canvas with smoothed values
              Platform.runLater(
                  () ->
                      LiveDigger.digLiveLine(
                          overlapCanvas,
                          smoothedPitch,
                          smoothedDb,
                          minFreq,
                          maxFreq,
                          minDb,
                          maxDb,
                          lastX,
                          lastY));

              System.out.println("Pitch: " + pitch + " | dB: " + db);
              double width1 = overlapCanvas.getWidth();
              double height1 = overlapCanvas.getHeight();
              double plotWidth =
                  width1 - TopCanvasDrawer.PADDING_LEFT - TopCanvasDrawer.PADDING_RIGHT;
              double plotHeight =
                  height1 - TopCanvasDrawer.PADDING_TOP - TopCanvasDrawer.PADDING_BOTTOM;

              double x =
                  TopCanvasDrawer.PADDING_LEFT
                      + ((smoothedPitch - minFreq) / (double) (maxFreq - minFreq)) * plotWidth;
              double y =
                  TopCanvasDrawer.PADDING_TOP
                      + ((maxDb - smoothedDb) / (double) (maxDb - minDb)) * plotHeight;

              double dx = x - treasureX;
              double dy = y - treasureY;
              double distance = Math.sqrt(dx * dx + dy * dy);

              if (!treasureFound && distance < treasureRadiusInner + 5) {
                treasureFound = true;
                Platform.runLater(
                    () -> {
                      Alert alert = new Alert(AlertType.INFORMATION);
                      alert.setTitle("Schatz gefunden!");
                      alert.setHeaderText(null);
                      alert.setContentText(
                          "Du hast den Schatz gefunden! Klicke auf 'Nächstes Level',"
                              + " um fortzufahren.");

                      ButtonType nextLevelButton = new ButtonType("Nächstes Level");
                      alert.getButtonTypes().setAll(nextLevelButton);

                      Optional<ButtonType> result = alert.showAndWait();
                      if (result.isPresent() && result.get() == nextLevelButton) {
                        overlapCanvas.setVisible(false);
                        PauseTransition pause = new PauseTransition(Duration.seconds(3));
                        pause.setOnFinished(e -> resetLevelAndTreasure(true));
                        pause.play();
                      }
                    });
              }
            }
          });
      recorder.start();
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
    if (recorder != null) {
      recorder.setListener(null);
      recorder.stop();
      recorder = null;
    }
  }

  /**
   * Resets the treasure hunt by clearing all recorded points and redrawing the covering layer.
   * Hides the overlap canvas for a short pause before resetting.
   */
  @FXML
  public void resetTreasureHunt() {
    overlapCanvas.setVisible(false);
    PauseTransition pause = new PauseTransition(Duration.seconds(3));
    pause.setOnFinished(e -> resetLevelAndTreasure(false));
    pause.play();
  }

  /**
   * Handles switching from the coordinatesystem scene to the 'start-scene' - landing.fxml. This
   * event is triggered by an UI-event, typically a button click.
   *
   * @param event The {@link javafx.event.ActionEvent} that triggers the scene switch
   * @throws IOException If the FXML file for the start scene cannot be loaded.
   */
  @FXML
  public void switchToStartScene(ActionEvent event) throws IOException {
    stopRecording();

    // Pop-Up mit Userdaten und Level
    String username = usernameLabel != null ? usernameLabel.getText() : "";
    String profile = profileLabel != null ? profileLabel.getText() : "";
    String message = username + profile + "  -> erreichtes Level: " + level;

    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle("Spiel beendet");
    alert.setHeaderText("Hier dein Ergebnis:");
    alert.setContentText(message);
    alert.showAndWait();

    SceneUtil.changeScene((Stage) ((Node) event.getSource()).getScene().getWindow(), "/at/fh/burgenland/landing.fxml");
  }

  /**
   * Switches to the game selection scene.
   *
   * @param event the action event that triggers the scene switch
   * @throws IOException if the FXML file cannot be loaded
   */
  @FXML
  public void switchToGameSelection(ActionEvent event) throws IOException {
    stopRecording();
    SceneUtil.changeScene((Stage) ((Node) event.getSource()).getScene().getWindow(), "/at/fh/burgenland/game_selection.fxml");
  }

  /**
   * Resets the level and treasure position, optionally increasing the level. Clears all recorded
   * points and redraws the coordinate system.
   *
   * @param increaseLevel true to increase the level, false to keep the current level
   */
  private void resetLevelAndTreasure(boolean increaseLevel) {

    if (increaseLevel && level < maxLevel) {
      level++;
      updateTreasureRadii();
      updateLevelLabel();
    }

    recordedPoints.clear();
    lastX[0] = -1;
    lastY[0] = -1;
    treasureFound = false;
    overlapCanvas.setVisible(true);

    double width = baseCanvas.getWidth();
    double height = baseCanvas.getHeight();
    double minX = TopCanvasDrawer.PADDING_LEFT + treasureRadiusOuter;
    double maxX = width - TopCanvasDrawer.PADDING_RIGHT - treasureRadiusOuter;
    double minY = TopCanvasDrawer.PADDING_TOP + treasureRadiusOuter;
    double maxY = height - TopCanvasDrawer.PADDING_BOTTOM - treasureRadiusOuter;

    treasureX = ThreadLocalRandom.current().nextDouble(minX, maxX);
    treasureY = ThreadLocalRandom.current().nextDouble(minY, maxY);

    treasureRelX = (treasureX - minX) / (maxX - minX);
    treasureRelY = (treasureY - minY) / (maxY - minY);

    drawCoordinateSystemStructure();
  }
}
