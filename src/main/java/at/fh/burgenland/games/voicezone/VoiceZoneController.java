package at.fh.burgenland.games.voicezone;

import at.fh.burgenland.audioinput.AudioInputService;
import at.fh.burgenland.coordinatesystem.CoordinateSystemDrawer;
import at.fh.burgenland.coordinatesystem.LogScaleConverter;
import at.fh.burgenland.fft.FrequenzDbOutput;
import at.fh.burgenland.profiles.IfVoiceProfile;
import at.fh.burgenland.profiles.ProfileManager;
import at.fh.burgenland.profiles.UserProfile;
import at.fh.burgenland.utils.SceneUtil;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

/**
 * This controller handles real-time training with voice data (frequency or volume). It visualizes a
 * target zone on a canvas, compares live input values with the target and checks if the target has
 * been hit within a tolerance window and for a minimum hold time.
 */
public class VoiceZoneController {

  private VoiceZoneTrainingMode trainingMode = VoiceZoneTrainingMode.FREQUENCY; // Default mode

  // All necessary UI-elements from the fxml
  @FXML private Canvas coordinateSystemCanvas;
  @FXML private Label logoLabel;
  @FXML private Label textLabel;
  @FXML private Label titleLabel;
  @FXML private Label targetLabel;
  @FXML private Label toleranceLabel;
  @FXML private Label levelLabel;
  @FXML private Button backButton;
  @FXML private Button exportButton;
  @FXML private RadioButton freqButton;
  @FXML private RadioButton volumeButton;
  @FXML private Label usernameLabel;
  @FXML private Label voiceProfileLabel;
  @FXML private Label overlayMessageLabel;

  @FXML private CheckBox recordingIndicator;

  /**
   * Sets the recording indicator state based on the specified parameter.
   *
   * @param isRecording a boolean value indicating whether recording is active. If true, the
   *     recording indicator is set to selected; otherwise, it is deselected.
   */
  public void setRecording(boolean isRecording) {
    recordingIndicator.setSelected(isRecording);
  }

  // Audio is recorded from the choosen microphone und in dB and Hz converted
  private final AudioInputService audioInputService = AudioInputService.getInstance();
  private final FrequenzDbOutput recorder =
      new FrequenzDbOutput(audioInputService.getSelectedMixer());

  // Range values based on the voice profile
  private int minFreq;
  private int maxFreq;
  private int minDb;
  private int maxDb;

  // Level organisation
  private int level = 1;
  private final int maxLevel = 5;

  // Training configurations
  private final double toleranceFreq =
      30.0; // Start-Tolerance Range for Frequenz (target = 200, tolerance = [170-230])
  private final double toleranceVol =
      5.0; // Start-Tolerance Range for Volume (target = 10, tolerance = [0-20])
  private double currentTolerance = toleranceFreq;
  private double targetValue; // Random generated target Value to be reached
  private int successfulHitsInLevel = 0; // Counter of successful hits
  private final int requiredHitsPerLevel =
      3; // 3 hits are required in one Level -> after that it will be more difficult

  // Timing and state tracking
  private final long requiredHoldFregTimeMs =
      1500; // required time to be hold in the bar -> bar is successful if we hold the value for
  // min. 1,5 sec
  private final long requiredHoldVolTimeMs = 500;
  private final long maxOutOfTargetGracePeriod =
      500; // if you leave the target area for a short time, you have 0,5 sec grace period. You can
  // still return within this time without the attempt being cancelled
  private final long minDelayBetweenSuccesses =
      2000; // after you have successfully hit the target for 1,5 sec, you must pause for 2 sec
  // before the new hit can be counted. This prevents you from accidentally getting
  // several hits in succession without changing the target.
  private long hitStartTime =
      -1; // saves the first time you enter the target area (is used to calculate if the
  // requiredHoldTime is reached)
  private long outOfTargetSince =
      -1; // time is saved when you leave the target area (is used to check if you are out of the
  // grace period)
  private long lastSuccessTime =
      -1; // saved when you have successfully held the value (is used to check if the
  // minDelayBetweenSuccesses has already ended)
  private boolean currentlyInTarget = false; // defines if you are already in the target area or not
  private boolean showGreen =
      false; // defines if the bar should be green (when you are in the target area) or not

  private final double minDeltaFreq = 30.0; // Minimum required frequency difference between targets
  private final double minDeltaVol = 13.0; // Minimum required volume difference between targets
  private Double lastTargetValue = null; // Stores the last generated target value

  private final int smoothingWindowSize =
      3; // Number of recent dB measurements used to smooth the volume input via moving average
  private final Deque<Double> recentDbValues =
      new ArrayDeque<>(); // sliding window of recent dB values for smoothing volume input to reduce

  // noise fluctuations during live voice analysis

  /** Initialies UI elements and bind canvas dimensions to window size. */
  @FXML
  public void initialize() {
    ToggleGroup group = new ToggleGroup(); // only one play mode can be chosen (frequncy or volume)
    freqButton.setToggleGroup(group);
    volumeButton.setToggleGroup(group);

    loadVoiceProfile(); // values from the active voice profile are chosen

    // draw canvas and bind it to the window size
    Platform.runLater(
        () -> {
          coordinateSystemCanvas
              .widthProperty()
              .bind(coordinateSystemCanvas.getScene().widthProperty().subtract(60));
          coordinateSystemCanvas
              .heightProperty()
              .bind(coordinateSystemCanvas.getScene().heightProperty().subtract(200));
          coordinateSystemCanvas
              .widthProperty()
              .addListener((obs, oldVal, newVal) -> drawCoordinateSystemAndTargetBar());
          coordinateSystemCanvas
              .heightProperty()
              .addListener((obs, oldVal, newVal) -> drawCoordinateSystemAndTargetBar());
          drawCoordinateSystemAndTargetBar();
          generateNewTarget(); // first target value is generated and shown
          updateLevelInfo();
        });

    UserProfile userProfile = ProfileManager.getCurrentProfile();
    if (userProfile != null) {
      usernameLabel.setText(userProfile.getUserName());
      voiceProfileLabel.setText(userProfile.getVoiceProfile().toString());
    } else {
      usernameLabel.setText("Benutzer: (nicht eingeloggt)");
      voiceProfileLabel.setText("Profil: (kein Profil)");
    }
  }

  /**
   * The method loadVoiceProfile uses values from the active userprofile. If there is no active
   * profile, default values are used.
   */
  private void loadVoiceProfile() {
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
  }

  /** Draws the background axes and labels. */
  private void drawCoordinateSystemAndTargetBar() {
    CoordinateSystemDrawer.drawAxes(coordinateSystemCanvas, minFreq, maxFreq, minDb, maxDb);
    drawTargetBar(targetValue);
  }

  /**
   * Draws the target bar on the canvas depending on training mode. If the training mode equals
   * frequency, the bar is horizontal. If the training mode equals volume, the bar is vertical.
   *
   * @param targetValue the current target frequency or volume
   */
  private void drawTargetBar(double targetValue) {
    var gc = coordinateSystemCanvas.getGraphicsContext2D();
    double width = coordinateSystemCanvas.getWidth();
    double height = coordinateSystemCanvas.getHeight();

    // gc.setFill(
    // showGreen ? javafx.scene.paint.Color.LIGHTGREEN : javafx.scene.paint.Color.LIGHTGRAY);

    gc.setFill(showGreen ? javafx.scene.paint.Color.LIGHTGREEN : getBarColor());

    double thickness = 40.0;
    if (trainingMode == VoiceZoneTrainingMode.FREQUENCY) {
      double plotWidth =
          width - CoordinateSystemDrawer.PADDING_LEFT - CoordinateSystemDrawer.PADDING_RIGHT;
      System.out.println("Plotwidth" + plotWidth);
      /*double x1 =
          map(
              targetValue - currentTolerance,
              minFreq,
              maxFreq,
              CoordinateSystemDrawer.PADDING_LEFT,
              CoordinateSystemDrawer.PADDING_LEFT + plotWidth);
      double x2 =
          map(
              targetValue + currentTolerance,
              minFreq,
              maxFreq,
              CoordinateSystemDrawer.PADDING_LEFT,
              CoordinateSystemDrawer.PADDING_LEFT + plotWidth);*/

      LogScaleConverter.init(minFreq, maxFreq, plotWidth);
      double x1 =
          CoordinateSystemDrawer.PADDING_LEFT
              + LogScaleConverter.frequencyToX(targetValue - currentTolerance);
      double x2 =
          CoordinateSystemDrawer.PADDING_LEFT
              + LogScaleConverter.frequencyToX(targetValue + currentTolerance);
      gc.fillRect(Math.min(x1, x2), (height - thickness) / 2, Math.abs(x2 - x1), thickness);
    } else {
      double plotHeight =
          height - CoordinateSystemDrawer.PADDING_TOP - CoordinateSystemDrawer.PADDING_BOTTOM;
      double top =
          map(
              Math.min(targetValue + currentTolerance, maxDb),
              maxDb,
              minDb,
              CoordinateSystemDrawer.PADDING_TOP,
              CoordinateSystemDrawer.PADDING_TOP + plotHeight);
      double bottom =
          map(
              Math.max(targetValue - currentTolerance, minDb),
              maxDb,
              minDb,
              CoordinateSystemDrawer.PADDING_TOP,
              CoordinateSystemDrawer.PADDING_TOP + plotHeight);

      gc.fillRect(
          (width - thickness) / 2, Math.min(top, bottom), thickness, Math.abs(bottom - top));
    }
  }

  /** Generates a new random target and updates canvas and labels. */
  private void generateNewTarget() {
    showGreen = false;
    recentDbValues.clear();

    double newTarget;
    double minDelta =
        (trainingMode == VoiceZoneTrainingMode.FREQUENCY) ? minDeltaFreq : minDeltaVol;

    do {
      if (trainingMode == VoiceZoneTrainingMode.FREQUENCY) {
        newTarget = minFreq + Math.random() * (maxFreq - minFreq);
      } else {
        double maxTol = (maxDb - minDb) / 2.0;
        double effTol = Math.min(currentTolerance, maxTol - 1);
        newTarget = minDb + effTol + Math.random() * ((maxDb - minDb) - 2 * effTol);
      }
    } while (lastTargetValue != null && Math.abs(newTarget - lastTargetValue) < minDelta);

    targetValue = newTarget;
    lastTargetValue = newTarget;

    drawCoordinateSystemAndTargetBar();
    updateTargetInfo();
  }

  /**
   * Updates the label values for current target and tolerance. Shows the target value and tolerance
   * on the UI.
   */
  private void updateTargetInfo() {
    Platform.runLater(
        () -> {
          double lowerBound = targetValue - currentTolerance;
          double upperBound = targetValue + currentTolerance;

          targetLabel.setText(String.format("%.0f", targetValue));
          toleranceLabel.setText(String.format("[%.0f – %.0f]", lowerBound, upperBound));
        });
  }

  /**
   * Displays a temporary message over the coordinate system canvas for a specified duration. The
   * message is shown using the {@code overlayMessageLabel} and automatically hidden after the given
   * number of milliseconds.
   *
   * @param message the text to display
   * @param durationMillis the duration in milliseconds the message should remain visible
   */
  private void showOverlayMessage(String message, int durationMillis) {
    Platform.runLater(
        () -> {
          overlayMessageLabel.setText(message);
          overlayMessageLabel.setVisible(true);
        });

    new Thread(
            () -> {
              try {
                Thread.sleep(durationMillis);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              Platform.runLater(() -> overlayMessageLabel.setVisible(false));
            })
        .start();
  }

  /**
   * Starts real-time audio analysis and evaluation. Checks if the voice is in the target area, how
   * long is the voice in the target area, if the voice is min. 1,5 sec in the target area and if
   * the level should be increased. the bar gets green, if the target area is reached.
   */
  @FXML
  public void startRecording() {
    this.setRecording(true);
    recorder.setListener(
        (pitch, db) -> {
          boolean isSilent =
              (trainingMode == VoiceZoneTrainingMode.FREQUENCY
                      && (pitch <= 0 || Double.isNaN(pitch)))
                  || (trainingMode == VoiceZoneTrainingMode.VOLUME
                      && (Double.isInfinite(db) || Double.isNaN(db)));

          Platform.runLater(
              () -> {
                long requiredHoldTime =
                    (trainingMode == VoiceZoneTrainingMode.FREQUENCY)
                        ? requiredHoldFregTimeMs
                        : requiredHoldVolTimeMs;

                if (isSilent) {

                  showGreen = false;
                  currentlyInTarget = false;
                  hitStartTime = -1;
                  outOfTargetSince = -1;

                  if (trainingMode == VoiceZoneTrainingMode.VOLUME) {
                    recentDbValues.clear(); // << HIER löschen wir die Glättung bei Stille
                  }

                  drawCoordinateSystemAndTargetBar();
                  return;
                }

                double value;

                if (trainingMode == VoiceZoneTrainingMode.FREQUENCY) {
                  value = pitch;
                } else {
                  // Wert zur Liste hinzufügen
                  recentDbValues.addLast(db);
                  if (recentDbValues.size() > smoothingWindowSize) {
                    recentDbValues.removeFirst();
                  }
                  // Mittelwert berechnen
                  value =
                      recentDbValues.stream().mapToDouble(Double::doubleValue).average().orElse(db);

                  if (recentDbValues.size() < smoothingWindowSize) {
                    return; // noch nicht bewerten, weil Datenbasis zu gering
                  }
                }

                boolean isInTarget = isInTargetRange(value, targetValue, currentTolerance);
                long now = System.currentTimeMillis();

                System.out.printf(
                    "|Modus: %s | Wert: %.2f | Ziel: %.2f | Bereich: [%.2f - %.2f] | Status: %s%n",
                    trainingMode,
                    value,
                    targetValue,
                    targetValue - currentTolerance,
                    targetValue + currentTolerance,
                    isInTarget ? "IM ZIELBEREICH" : "daneben");

                if (isInTarget) {
                  if (!showGreen || !currentlyInTarget) {
                    showGreen = true;
                    drawCoordinateSystemAndTargetBar();
                  }
                  outOfTargetSince = -1;
                  if (!currentlyInTarget) {
                    if (now - lastSuccessTime < minDelayBetweenSuccesses) {
                      return;
                    }
                    hitStartTime = now;
                    currentlyInTarget = true;

                  } else if (now - hitStartTime >= requiredHoldTime) {
                    successfulHitsInLevel++;
                    lastSuccessTime = now;
                    hitStartTime = -1;
                    currentlyInTarget = false;
                    showGreen = false;

                    if (level == maxLevel && successfulHitsInLevel >= requiredHitsPerLevel) {
                      recorder.setListener(null);

                      Alert alert = new Alert(AlertType.INFORMATION);
                      alert.setTitle("Training abgeschlossen");
                      alert.setHeaderText("Super gemacht!");
                      alert.setContentText("Du hast alle 5 Level erfolgreich abgeschlossen!");

                      alert.showAndWait();

                      resetTrainingProgress();
                      generateNewTarget();
                      updateTargetInfo();
                      drawCoordinateSystemAndTargetBar();

                      recorder.start();
                      return;
                    }

                    // Test
                    /*if (successfulHitsInLevel >= requiredHitsPerLevel && level < maxLevel) {
                      currentTolerance *= 0.8;
                      level++;
                      successfulHitsInLevel = 0;
                      updateLevelInfo();
                    }
                    generateNewTarget();
                    updateTargetInfo();
                    drawCoordinateSystemAndTargetBar();*/

                    // Erfolg anzeigen, danach pausieren und dann neues Target anzeigen
                    showOverlayMessage("Toll gemacht!", 2000);

                    new Thread(
                            () -> {
                              try {
                                Thread.sleep(2000); // 2 Sekunden warten
                              } catch (InterruptedException e) {
                                e.printStackTrace();
                              }
                              Platform.runLater(
                                  () -> {
                                    if (successfulHitsInLevel >= requiredHitsPerLevel
                                        && level < maxLevel) {
                                      currentTolerance *= 0.8;
                                      level++;
                                      successfulHitsInLevel = 0;
                                      updateLevelInfo();
                                    }

                                    generateNewTarget();
                                    updateTargetInfo();
                                    drawCoordinateSystemAndTargetBar();
                                  });
                            })
                        .start();
                  }
                } else if (currentlyInTarget) {
                  if (outOfTargetSince == -1) {
                    outOfTargetSince = now;
                  } else if (now - outOfTargetSince > maxOutOfTargetGracePeriod) {
                    currentlyInTarget = false;
                    hitStartTime = -1;
                    if (showGreen) {
                      showGreen = false;
                      drawCoordinateSystemAndTargetBar();
                    }
                  }
                }
              });
        });

    recorder.start();
  }

  /** Stops the ongoing recording and analysis. */
  @FXML
  public void stopRecording() {
    this.setRecording(false);
    recorder.setListener(null);
    recorder.stop();
    recentDbValues.clear();
  }

  private void updateLevelInfo() {
    Platform.runLater(
        () -> {
          if (levelLabel != null) {
            levelLabel.setText("Nr. " + level);
          }
        });
  }

  /**
   * Returns to landing page.
   *
   * @param event the action triggering the scene switch
   */
  @FXML
  public void switchToGameSelectionScene(ActionEvent event) throws IOException {
    this.stopRecording();
    SceneUtil.changeScene(
        (Stage) ((Node) event.getSource()).getScene().getWindow(),
        "/at/fh/burgenland/game_selection.fxml");
  }

  /** Switches training mode to FREQUENCY. */
  @FXML
  public void setFrequencyMode() {
    if (freqButton.isSelected()) {
      trainingMode = VoiceZoneTrainingMode.FREQUENCY;
      resetTrainingProgress();
      generateNewTarget();
    }
  }

  /** Switches training mode to VOLUME. */
  @FXML
  public void setVolumeMode() {
    if (volumeButton.isSelected()) {
      trainingMode = VoiceZoneTrainingMode.VOLUME;
      resetTrainingProgress();
      generateNewTarget();
    }
  }

  /**
   * Resets the current training progress to its initial state. Sets the level back to 1, resets hit
   * counters and applies the starting tolerance based on the selected training mode (frequency or
   * volume).
   */
  public void resetTrainingProgress() {
    level = 1;
    successfulHitsInLevel = 0;
    currentTolerance =
        (trainingMode == VoiceZoneTrainingMode.FREQUENCY) ? toleranceFreq : toleranceVol;
    recentDbValues.clear();
    updateLevelInfo();
  }

  /**
   * Maps a value from one range to another.
   *
   * @param value input value
   * @param inMin source min
   * @param inMax source max
   * @param outMin target min
   * @param outMax target max
   * @return mapped value
   */
  public double map(double value, double inMin, double inMax, double outMin, double outMax) {
    return (value - inMin) / (inMax - inMin) * (outMax - outMin) + outMin;
  }

  /**
   * Determines the color of the target bar based on the current level. Darker shades are used as
   * the level increases.
   *
   * @return a grayscale color representing the current training level
   */
  private javafx.scene.paint.Color getBarColor() {
    int cappedLevel = Math.min(level, maxLevel);
    double brightness = 0.8 - (cappedLevel - 1) * 0.2;
    brightness = Math.max(0, brightness);
    return javafx.scene.paint.Color.gray(brightness);
  }

  /**
   * Checks whether a value is within a specified tolerance range of a target value.
   *
   * @param value the actual value to check
   * @param target the target value
   * @param tolerance the allowed deviation from the target
   * @return true if value is within the target ± tolerance, false otherwise
   */
  public boolean isInTargetRange(double value, double target, double tolerance) {
    return Math.abs(value - target) <= tolerance;
  }

  /**
   * Returns the current level of the training session.
   *
   * @return the training level, starting from 1
   */
  public int getLevel() {
    return level;
  }

  /**
   * Sets the training mode (FREQUENCY or VOLUME) for the current session.
   *
   * @param mode the selected VoiceZoneTrainingMode
   */
  public void setTrainingMode(VoiceZoneTrainingMode mode) {
    this.trainingMode = mode;
  }

  /**
   * Returns the current tolerance used for evaluating target hits.
   *
   * @return the active tolerance in Hz or dB, depending on the mode
   */
  public double getCurrentTolerance() {
    return currentTolerance;
  }
}
