package at.fh.burgenland.audioinput;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

/** Klasse zum Auswaehlen des Inputs. */
public class AudioInputController {

  @FXML private ImageView microphoneIcon;

  @FXML private ComboBox<String> audioInputComboBox;

  private AudioFormat audioFormat =
      new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 48000, 16, 2, 4, 48000, false);

  // Speichert den ausgewählten Mixer
  private ObservableList<Mixer.Info> mixerInfos;
  private final AudioInputService audioInputService = AudioInputService.getInstance();

  /** Methode to fill out Audio-Input-Dropdown. */
  @FXML
  public void initialize() {
    loadValidAudioInputs();

    // Stelle zuvor ausgewählten Mixer wieder her (falls vorhanden)
    Mixer storedMixer = audioInputService.getSelectedMixer();

    if (storedMixer != null) {
      String storedMixerName = storedMixer.getMixerInfo().getName();
      for (int i = 0; i < mixerInfos.size(); i++) {
        if (mixerInfos.get(i).getName().equals(storedMixerName)) {
          audioInputComboBox.getSelectionModel().select(i);
          break;
        }
      }
    } else if (audioInputService.getSelectedMixerIndex() != -1
        && audioInputService.getSelectedMixerIndex() < mixerInfos.size()) {
      audioInputComboBox.getSelectionModel().select(audioInputService.getSelectedMixerIndex());
    }

    microphoneIcon.setOnMouseClicked(this::handleMicrophoneIconClick);

    audioInputComboBox.setOnAction(
        event -> {
          int selectedIndex = audioInputComboBox.getSelectionModel().getSelectedIndex();
          if (selectedIndex >= 0) {
            Mixer selectedMixer = AudioSystem.getMixer(mixerInfos.get(selectedIndex));
            audioInputService.setSelectedMixer(selectedMixer);
            audioInputService.setSelectedMixerIndex(selectedIndex);
            System.out.println(
                "Selected Input: "
                    + audioInputComboBox.getValue()
                    + " - Mixer: "
                    + selectedMixer.getMixerInfo().getName());
          } else {
            audioInputService.setSelectedMixer(null);
            audioInputService.setSelectedMixerIndex(-1);
            System.out.println("No Input Selected");
          }

          audioInputComboBox.setVisible(false);
          audioInputComboBox.setManaged(false);
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

  @FXML
  private void handleMicrophoneIconClick(MouseEvent event) {

    if (!audioInputComboBox.isVisible()) {
      audioInputComboBox.setVisible(true);
      audioInputComboBox.setManaged(true);
      audioInputComboBox.show();
    } else {
      audioInputComboBox.hide();
      audioInputComboBox.setVisible(false);
      audioInputComboBox.setManaged(false);
    }
  }

  private void showErrorAlert(String title, String content) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
  }

  // Getter für selectedMixer, falls du ihn in den Tests benötigst
  public Mixer getSelectedMixer() {
    return audioInputService.getSelectedMixer();
  }

  // Getter für audioFormat, falls du ihn in den Tests benötigst
  public AudioFormat getAudioFormat() {
    return audioFormat;
  }
}
