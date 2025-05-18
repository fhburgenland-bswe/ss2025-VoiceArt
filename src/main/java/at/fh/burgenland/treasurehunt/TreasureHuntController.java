package at.fh.burgenland.treasurehunt;

import at.fh.burgenland.audioinput.AudioInputService;

import at.fh.burgenland.coordinatesystem.ExponentialSmoother;
import at.fh.burgenland.coordinatesystem.LiveDrawer;
import at.fh.burgenland.fft.FrequenzDbOutput;
import at.fh.burgenland.profiles.ProfileManager;
import at.fh.burgenland.profiles.UserProfile;
import at.fh.burgenland.profiles.VoiceProfile;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class TreasureHuntController {

  @FXML
  private Canvas baseCanvas;
  @FXML
  private Canvas overlapCanvas;

  @FXML
  private Label logoLabel;
  @FXML
  private Label textLabel;
  @FXML
  private Button backButton;
  @FXML
  private Button exportButton;
  @FXML
  private Label levelLabel;
  @FXML
  private Label usernameLabel;
  @FXML
  private Label profileLabel;

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
  private final int MAX_LEVEL = 10;

  // Getter für Schatz-Koordinaten
  public double getTreasureX() {
    return treasureX;
  }

  public double getTreasureY() {
    return treasureY;
  }

  public double getTreasureRadius() {
    return treasureRadiusInner;
  }

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
  private  FrequenzDbOutput recorder;

  // Last X coordinate used in drawing, stored in a single element array for mutable access.
  private final double[] lastX = {-1};
  // Last Y coordinate used in drawing, stored in a single element array for mutable access.
  private final double[] lastY = {-1};

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

  private void updateLevelLabel() {
    if (levelLabel != null) {
      levelLabel.setText("Level: " + level);
    }
  }

  public void setUserInfo(String username, String profileName) {
    if (usernameLabel != null) {
      usernameLabel.setText("  Benutzer: " + username);
    }
    if (profileLabel != null) {
      profileLabel.setText("  Stimmprofil: " + profileName);
    }
  }


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
          baseCanvas
              .widthProperty()
              .bind(baseCanvas.getScene().widthProperty().subtract(60));
          baseCanvas
              .heightProperty()
              .bind(baseCanvas.getScene().heightProperty().subtract(250));

          overlapCanvas
              .widthProperty()
              .bind(baseCanvas.getScene().widthProperty().subtract(60));
          overlapCanvas
              .heightProperty()
              .bind(baseCanvas.getScene().heightProperty().subtract(250));

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
   * Redraws the background structure of the coordinate system (axes, labels). This method is called
   * whenever the window is resized.
   */
  private void drawCoordinateSystemStructure() {
    updateTreasureRadii();

    TopCanvasDrawer.drawAxes(overlapCanvas, minFreq, maxFreq, minDb, maxDb);
    //OverlapCanvasDrawer.drawAxes(baseCanvas, minFreq, maxFreq, minDb, maxDb);

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
        treasureX, treasureY, treasureRadiusInner, treasureRadiusOuter);

    // reset last coordinates
    lastX[0] = -1;
    lastY[0] = -1;

    // draw all existing points new
    for (TreasureHuntController.VoicePoint point : recordedPoints) {
      LiveDigger.drawLiveLine(
          overlapCanvas,  /// ?????
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
                      LiveDigger.drawLiveLine(
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
              double width = overlapCanvas.getWidth();
              double height = overlapCanvas.getHeight();
              double plotWidth =
                  width - TopCanvasDrawer.PADDING_LEFT - TopCanvasDrawer.PADDING_RIGHT;
              double plotHeight =
                  height - TopCanvasDrawer.PADDING_TOP - TopCanvasDrawer.PADDING_BOTTOM;

              double x = TopCanvasDrawer.PADDING_LEFT
                  + ((smoothedPitch - minFreq) / (double) (maxFreq - minFreq)) * plotWidth;
              double y = TopCanvasDrawer.PADDING_TOP
                  + ((maxDb - smoothedDb) / (double) (maxDb - minDb)) * plotHeight;

              double dx = x - treasureX;
              double dy = y - treasureY;
              double distance = Math.sqrt(dx * dx + dy * dy);

              if (!treasureFound && distance < treasureRadiusInner + 5) { // 5 = halbe Radiergröße
                treasureFound = true;
                Platform.runLater(() -> {
                  javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                      javafx.scene.control.Alert.AlertType.INFORMATION, "Schatz gefunden!");
                  alert.showAndWait();
                  if (level < MAX_LEVEL) {
                    level++;
                    updateTreasureRadii();
                    updateLevelLabel();
                  }
                  overlapCanvas.setVisible(false);
                  updateLevelLabel();
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
   */
  @FXML
  public void resetTreasureHunt() {
    recordedPoints.clear();
    lastX[0] = -1;
    lastY[0] = -1;
    treasureFound = false;
    overlapCanvas.setVisible(true);

    updateTreasureRadii();
    updateLevelLabel();

    // Bereich berechnen, in dem der Schatz platziert werden darf
    double width = baseCanvas.getWidth();
    double height = baseCanvas.getHeight();
    double minX = TopCanvasDrawer.PADDING_LEFT + treasureRadiusOuter;
    double maxX = width - TopCanvasDrawer.PADDING_RIGHT - treasureRadiusOuter;
    double minY = TopCanvasDrawer.PADDING_TOP + treasureRadiusOuter;
    double maxY = height - TopCanvasDrawer.PADDING_BOTTOM - treasureRadiusOuter;

    // Neue zufällige Koordinaten setzen
    treasureX = ThreadLocalRandom.current().nextDouble(minX, maxX);
    treasureY = ThreadLocalRandom.current().nextDouble(minY, maxY);

    // Relative Position speichern
    treasureRelX = (treasureX - minX) / (maxX - minX);
    treasureRelY = (treasureY - minY) / (maxY - minY);

    drawCoordinateSystemStructure();
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
    Parent root = FXMLLoader.load(getClass().getResource("/at/fh/burgenland/landing.fxml"));
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  @FXML
  public void switchToGameSelection(ActionEvent event) throws IOException {
    stopRecording();
    Parent root = FXMLLoader.load(getClass().getResource("/at/fh/burgenland/game_selection.fxml"));
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }


}
