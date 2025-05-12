package at.fh.burgenland.treasurehunt;


import at.fh.burgenland.audioinput.AudioInputController;
import at.fh.burgenland.audioinput.AudioInputService;
import at.fh.burgenland.coordinatesystem.ExponentialSmoother;
import at.fh.burgenland.fft.FrequenzDbOutput;
import at.fh.burgenland.profiles.ProfileManager;
import at.fh.burgenland.profiles.UserProfile;
import at.fh.burgenland.profiles.VoiceProfile;
import java.io.IOException;
import java.util.Random;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TreasureHuntController {

  @FXML
  private Canvas gameCanvas;

  @FXML
  private Label statusLabel;

  @FXML
  private Button backButton;

  @FXML
  private Button startButton;

  @FXML
  private Button stopButton;

  //game grid parameters
  private static final int GRID_ROWS = 10;
  private static final int GRID_COLS = 12;
  private double cellWidth;
  private double cellHeight;

  private boolean[][] revealedCells;
  private int treasureRow;
  private int treasureCol;
  private boolean treasureFound = false;
  private boolean gameActive = false;

  //Voice input
  private AudioInputService audioInputService;
  private FrequenzDbOutput recorder;
  private float smoothedPitch = -1f;
  private double smoothedDb = Double.NEGATIVE_INFINITY;
  private final float smoothingFactor = 0.3f;

  //Voice profil ranges (will be overwritten with profiles)
  private int minFreq = 50;
  private int maxFreq = 1100;
  private int minDb = -60;
  private int maxDb = 0;

  private final Random random = new Random();

  //current voice position on grid fpr cursor
  private int currentVoiceCol = -1;
  private int currentVoiceRow = -1;

  @FXML
  public void initialize(){
    audioInputService = AudioInputService.getInstance();
    revealedCells = new boolean[GRID_ROWS][GRID_COLS];

    loadVoiceProfileRanges();
    placeTreasure();

    //bind canvas size for dynamic resizing and cell calculation
    // ensure drawing is responsive to window size changes
    gameCanvas.widthProperty().addListener((obs, oldVal, newVal) -> {
      calculateCellSize();
      drawGameboard();
    });
    gameCanvas.heightProperty().addListener((obs, oldVal, newVal) -> {
      calculateCellSize();
      drawGameboard();
    });

    //initial draw after layout pass, ensure canvas has dimensions
    Platform.runLater(() -> {
      calculateCellSize();
      drawGameboard();
    });

  }

  private void loadVoiceProfileRanges(){
    UserProfile userProfile = ProfileManager.getCurrentProfile();
    if(userProfile != null && userProfile.getVoiceProfile() != null){
      VoiceProfile voiceProfile = userProfile.getVoiceProfile();
      minFreq = voiceProfile.getMinFreq();
      maxFreq = voiceProfile.getMaxFreq();
      minDb = voiceProfile.getMinDb();
      maxDb = voiceProfile.getMaxDb();
      statusLabel.setText("Game for " + userProfile.getUserName() + ". Pitch: " + minFreq + "-" + maxFreq + "Hz, Loudness: " + minDb + "-" + maxDb + "dB");

    }else{
      statusLabel.setText("Find the treasure! (Using default voice range: " + minFreq + "-" + maxFreq + "Hz, " + minDb + "-" + maxDb + "dB)");
    }
  }

  private void calculateCellSize(){
    if(gameCanvas.getWidth() > 0 && gameCanvas.getHeight() >0){
      cellWidth = gameCanvas.getWidth()/GRID_COLS;
      cellHeight = gameCanvas.getHeight()/GRID_ROWS;

    }
  }

  private void placeTreasure(){
    treasureRow = random.nextInt(GRID_ROWS);
    treasureCol = random.nextInt(GRID_COLS);
    System.out.println("Debug: Treasure hidden at [" + treasureRow + ", " + treasureCol + "]");

  }

  private void drawGameboard(){
    if(cellWidth <= 0 || cellHeight <= 0) return; //ensure cell sizes are valid

    GraphicsContext gc = gameCanvas.getGraphicsContext2D();
    gc.clearRect(0,0, gameCanvas.getWidth(), gameCanvas.getHeight());

    //draw grid cells
    for (int row = 0; row < GRID_ROWS; row++){
      for (int col = 0; col < GRID_COLS; col++){
        if(revealedCells[row][col]){
          gc.setFill(Color.SANDYBROWN.deriveColor(1, 1, 1, 0.5)); // "Dug" earth look
          gc.fillRect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
          if(treasureFound && row == treasureRow && col == treasureCol){
            gc.setFill(Color.GOLD); //treasurecolor
            //simple treasure representation
            gc.fillOval(col * cellWidth + cellWidth * 0.2, row * cellHeight + cellHeight * 0.2, cellWidth * 0.6, cellHeight * 0.6);
            gc.setStroke(Color.ORANGERED);
            gc.setLineWidth(2);
            gc.strokeOval(col * cellWidth + cellWidth * 0.2, row * cellHeight + cellHeight * 0.2, cellWidth * 0.6, cellHeight * 0.6);

          }
        }
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(0.5);
        gc.strokeRect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
      }
    }

    //draw current voice cursor if game is active and treasure not found
    if (gameActive && !treasureFound && currentVoiceCol != -1 && currentVoiceRow != -1) {
      gc.setFill(Color.RED.deriveColor(1,1,1,0.7)); //semi-transparent red
      gc.fillOval(currentVoiceCol * cellWidth + cellWidth / 2 - 5, currentVoiceRow * cellHeight + cellHeight / 2 - 5, 10, 10);

    }
  }

  @FXML
  private void handleStartGame(ActionEvent event){

    if(gameActive && !treasureFound) return; //already running

    if(audioInputService.getSelectedMixer() == null){
      showErrorAlert("No Microphone Selected", "Please select a microphone using the icon at the top-right before starting the game.");
      return;
    }
    resetGameForNewStart();
    gameActive = true;
    startButton.setDisable(true);
    stopButton.setDisable(false);
    statusLabel.setText("Voice input active... Hunt!");

    //initialize or re-initialize recorder
    if(recorder != null && recorder.isRunning()){
      recorder.stop(); //stops previous instance if any
    }
    recorder = new FrequenzDbOutput(audioInputService.getSelectedMixer());

    recorder.setListener((pitch, db) ->{
      if(!gameActive || treasureFound){ // stop processing if game is over or paused
        return;
      }

      if(pitch > 0 && !Double.isInfinite(db)){
        smoothedPitch = ExponentialSmoother.smooth(smoothedPitch, pitch, smoothingFactor);
        smoothedDb = ExponentialSmoother.smooth(smoothedDb, db, smoothingFactor);

        // Map voice to grid: Pitch (X-axis/columns), Loudness (Y-axis/rows)
        // Louder sounds (higher dB, closer to maxDb) map to lower rows (digging "down").
        currentVoiceCol = (int) map(smoothedPitch, minFreq, maxFreq, 0, GRID_COLS - 1);
        currentVoiceRow = (int) map(smoothedDb, maxDb, minDb, 0 , GRID_ROWS - 1); // maxDb to minDb for louder -> lower row index


        //clamp values to be within grid boundaries
        currentVoiceCol = Math.max(0, Math.min(GRID_COLS - 1, currentVoiceRow));
        currentVoiceRow = Math.max(0, Math.min(GRID_ROWS - 1, currentVoiceRow));

        if(!revealedCells[currentVoiceRow][currentVoiceCol]){
          revealedCells[currentVoiceRow][currentVoiceCol] = true; //reveal the cell
          if (currentVoiceRow == treasureRow && currentVoiceCol == treasureCol){
            treasureFound = true;
            gameActive = false; //stop voice processing logic
            Platform.runLater(()-> {
              statusLabel.setText("!! Treasure found at (" + treasureCol + ", " + treasureRow + ") !!");
              stopButton.setDisable(true);
              startButton.setText("Play again?");
              startButton.setDisable(false);
              if (recorder != null) recorder.stop(); //stop audio dispatcher
            });
          }
        }

        //Update UI on JavaFX Application Thread
        Platform.runLater(this::drawGameboard);
      }

    });

    try{
      recorder.start();
    } catch (Exception e) {
      gameActive = false;
      startButton.setDisable(false);
      stopButton.setDisable(true);
      showErrorAlert("Audio Error", "Could not start audio recording: " + e.getMessage());
      e.printStackTrace();
    }


  }

  private void resetGameForNewStart(){
    treasureFound = false;
    gameActive = false; //will be set to true by handleStartGame()
    revealedCells = new boolean[GRID_ROWS][GRID_COLS];
    placeTreasure();
    smoothedPitch = -1f;
    smoothedDb = Double.NEGATIVE_INFINITY;
    currentVoiceCol = -1;
    currentVoiceRow = -1;

    if(recorder != null && recorder.isRunning()){
      recorder.stop();
    }

    //UI updates for reset state
    Platform.runLater(() ->{
      loadVoiceProfileRanges(); //reload ranges in case of profile change
      drawGameboard();
      startButton.setText("Start Game"); //reset button text
      startButton.setDisable(false);
      stopButton.setDisable(true);
    });
  }

  @FXML
  private void handleStopGame(ActionEvent event){
    if(recorder != null && recorder.isRunning()){
      recorder.stop();
    }
    gameActive = false; //pause the game logic part of listener
    startButton.setDisable(false);
    stopButton.setDisable(true);
    if(!treasureFound){
      statusLabel.setText("Voice input paused. Press 'Start Game' to resume");
    }
  }

  @FXML
  private void handleBackButton(ActionEvent event) throws IOException{
    shutdownRecorder(); // ensure recorder is stopped and resource released

    Parent gameSelectionRoot = FXMLLoader.load(getClass().getResource("/at/fh/burgenland/game_selection.fxml"));
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    Scene scene = new Scene(gameSelectionRoot);
    stage.setScene(scene);
    stage.show();
  }

  private double map(double value, double inMin, double inMax, double outMin, double outMax ){
    if(inMin == inMax) return outMin;  // Avoid division by zero, return lower bound of output
    return (value - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
  }

  private void showErrorAlert(String title, String content){
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
  }

  //call this method when the controllers view is being closed permanently
  public void shutdownRecorder(){
    gameActive = false; //stop any ongoin game logic
    if (recorder != null){
      if(recorder.isRunning()){
        recorder.stop();
      }
      recorder.shutdown(); //properly release audio and thread
      recorder = null;
    }

  }

}