package at.fh.burgenland;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
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

  private AudioFormat audioFormat =
      new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 48000, 16, 2, 4, 48000, false);

  /** Methode to fill out Audio-Input-Dropdown. */
  @FXML
  public void initialize() {
    loadValidAudioInputs();

    audioInputComboBox.setOnAction(
        event -> {
          int selectIndex = audioInputComboBox.getSelectionModel().getSelectedIndex();
          if (selectIndex >= 0) {
            try {
              TargetDataLine selectedLine =
                  AudioLineSelector.selectLineByIndex(selectIndex, audioFormat);
              System.out.println("Selected Input: " + audioInputComboBox.getValue());

            } catch (LineUnavailableException | IllegalArgumentException e) {
              e.printStackTrace();
            }
          }
        });
  }

  private void loadValidAudioInputs() {
    Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
    List<String> validMixerNames = new ArrayList<>();
    List<Mixer.Info> validMixerInfos = new ArrayList<>();
    for (Mixer.Info mixerInfo : mixerInfos) {
      Mixer mixer = AudioSystem.getMixer(mixerInfo);
      Info[] targetLineInfos = mixer.getTargetLineInfo();
      for (Info lineInfo : targetLineInfos) {
        if (lineInfo.getLineClass().equals(TargetDataLine.class)) {
          try {
            TargetDataLine line = (TargetDataLine) mixer.getLine(lineInfo);
            line.open(audioFormat);
            line.close(); // schliessen, weil wir sie ja nur kurz testen moechten
            validMixerNames.add(mixerInfo.getName());
            validMixerInfos.add(mixerInfo);
            break; // fals ungueltiger Mixer, wird zu naechsten gegangen

          } catch (LineUnavailableException e) {
            // Line wird uebersprungen
          }
        }
      }
    }
    ObservableList<String> options = FXCollections.observableArrayList(validMixerNames);
    audioInputComboBox.setItems(options);
  }
}
