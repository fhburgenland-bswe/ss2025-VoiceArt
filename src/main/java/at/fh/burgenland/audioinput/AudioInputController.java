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

  private final AudioInputService audioInputService = AudioInputService.getInstance();

  private ObservableList<Mixer.Info> mixers = FXCollections.observableArrayList();

  /** Methode to fill out Audio-Input-Dropdown. */
  @FXML
  public void initialize() {
    if (this.audioInputService.getMixers() == null) {
      loadValidAudioInputs();
    } else {
      loadMixer();
      audioInputComboBox.setItems(
          FXCollections.observableArrayList(mixers.stream().map(Mixer.Info::getName).toList()));
    }

    Mixer storedMixer;
    if (audioInputService.getSelectedMixer() == null) {
      storedMixer = this.audioInputService.getMixers().get(0);
      this.audioInputService.setSelectedMixer(storedMixer);
    } else {
      // Stelle zuvor ausgewählten Mixer wieder her (falls vorhanden)
      storedMixer = audioInputService.getSelectedMixer();
    }
    if (storedMixer != null) {
      String storedMixerName = storedMixer.getMixerInfo().getName();
      for (int i = 0; i < this.audioInputService.getMixers().size(); i++) {
        if (mixers.get(i).getName().equals(storedMixerName)) {
          audioInputComboBox.getSelectionModel().select(i);
          break;
        }
      }
    } else if (audioInputService.getSelectedMixerIndex() != -1
        && audioInputService.getSelectedMixerIndex() < this.audioInputService.getMixers().size()) {
      audioInputComboBox.getSelectionModel().select(audioInputService.getSelectedMixerIndex());
    }

    microphoneIcon.setOnMouseClicked(this::handleMicrophoneIconClick);

    audioInputComboBox.setOnAction(
        event -> {
          int selectedIndex = audioInputComboBox.getSelectionModel().getSelectedIndex();
          if (selectedIndex >= 0) {
            Mixer selectedMixer = this.audioInputService.getMixers().get(selectedIndex);
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
    ObservableList<Mixer> mixerInfos = FXCollections.observableArrayList();

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
            mixerInfos.add(AudioSystem.getMixer(mixerInfo));
            break;
          } catch (LineUnavailableException e) {
            // Line nicht verfügbar, versuche die nächste Line
          }
        }
      }
    }
    this.audioInputService.setMixers(mixerInfos);
    loadMixer();
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

  private void loadMixer() {
    for (Mixer mixer : this.audioInputService.getMixers()) {
      mixers.add(mixer.getMixerInfo());
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
