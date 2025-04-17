package at.fh.burgenland;

import at.fh.burgenland.audioinput.AudioLineSelector;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;



/** JavaFX example using for pipeline test. */
public class HelloController {

  @FXML private Label welcomeText;

  @FXML
  protected void onHelloButtonClick() {
    welcomeText.setText("Welcome to JavaFX Application!");
  }

  @FXML ComboBox<String> audioInputComboBox;

  @FXML private Button recordButton;

  private AudioFormat audioFormat =
      new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 48000, 16, 2, 4, 48000, false);

  private TargetDataLine targetDataLine;
  private boolean isRecording = false;
  private ObservableList<Mixer.Info> mixerInfos;


  /** Methode to fill out Audio-Input-Dropdown. */
  @FXML
  public void initialize() {
    loadValidAudioInputs();


    audioInputComboBox.setOnAction(event -> {
      int selectedIndex = audioInputComboBox.getSelectionModel().getSelectedIndex();
      if (selectedIndex >= 0) {
        Mixer.Info selectedMixerInfo = mixerInfos.get(selectedIndex);
        try {
          if (targetDataLine != null) {
            targetDataLine.close();
          }
          Mixer mixer = AudioSystem.getMixer(selectedMixerInfo);
          Line.Info[] targetLineInfos = mixer.getTargetLineInfo();
          for (Line.Info lineInfo : targetLineInfos) {
            if (lineInfo.getLineClass().equals(TargetDataLine.class)) {
              targetDataLine = (TargetDataLine) mixer.getLine(lineInfo);
              targetDataLine.open(audioFormat);
              break;
            }
          }
          System.out.println("Selected Input: " + audioInputComboBox.getValue());
        } catch (LineUnavailableException | IllegalArgumentException e) {
          e.printStackTrace();
          showErrorAlert("Audio Input Error", "No suitable audio input found for the selected device.");
        }
      }
    });

    recordButton.setOnAction(event -> {
      if(isRecording){
        stopRecording();
      }else{
        startRecording();
      }
    });
  }



  private void loadValidAudioInputs() {
    Mixer.Info[] allMixerInfos = AudioSystem.getMixerInfo();
    List<String> validMixerNames = new ArrayList<>();
    mixerInfos = FXCollections.observableArrayList();

    for (Mixer.Info mixerInfo : allMixerInfos) {
      Mixer mixer = AudioSystem.getMixer(mixerInfo);
      Line.Info[] targetLineInfos = mixer.getTargetLineInfo();
      for (Line.Info lineInfo : targetLineInfos) {
        if (lineInfo.getLineClass().equals(TargetDataLine.class)) {
          try {
            TargetDataLine line = (TargetDataLine) mixer.getLine(lineInfo);
            line.open(audioFormat);
            line.close();
            validMixerNames.add(mixerInfo.getName());
            mixerInfos.add(mixerInfo);
            break;
          } catch (LineUnavailableException e) {
            // Line nicht verfügbar, versuche die nächste Line
          }
        }
      }
    }
    audioInputComboBox.setItems(FXCollections.observableArrayList(validMixerNames));
  }

  private void startRecording(){
    if(targetDataLine != null){
      try{
        targetDataLine.open(audioFormat);
        targetDataLine.start();
        isRecording = true;
        recordButton.setText("Stop");
        System.out.println("Recording started");



        //TODO: hier AudioaufnahmeLogik
      } catch (Exception e) {
        e.printStackTrace();
        showErrorAlert("Recording Error", "Failed to start recording.");
      }
    }else {
      showErrorAlert("Recording Error", "No audio input selected.");
    }
  }

  private void stopRecording(){
    if(targetDataLine != null){
      targetDataLine.stop();
      targetDataLine.close();
      isRecording = false;
      recordButton.setText("Aufnehmen");
      System.out.println("Recording stopped");
      //TODO: hier die Audioverarbeitung
    }
  }

  private void showErrorAlert(String title, String content){
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
  }

}
