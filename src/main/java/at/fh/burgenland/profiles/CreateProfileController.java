package at.fh.burgenland.profiles;

import at.fh.burgenland.utils.SceneUtil;
import java.io.IOException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

/** Controller for Profile Creation Page. */
public class CreateProfileController {

  @FXML private TextField usernameField;

  @FXML private Label usernameErrorLabel;

  @FXML private RadioButton femaleProfile;

  @FXML private RadioButton maleProfile;

  @FXML private Label voiceProfileErrorLabel;

  @FXML private ToggleGroup voiceProfileGroup;

  @FXML private Button backButton;

  @FXML private Button letsGoButton;

  /** Method to initialize the Buttonstate (not clickable, if form not filled out). */
  public void initialize() {
    letsGoButton.setDisable(true);

    // initialize ErrorLabels
    usernameErrorLabel.setVisible(false);
    usernameErrorLabel.setManaged(false);
    voiceProfileErrorLabel.setVisible(false);
    voiceProfileErrorLabel.setManaged(false);

    // Listener for Textfield
    usernameField
        .textProperty()
        .addListener(
            new ChangeListener<String>() {
              @Override
              public void changed(
                  ObservableValue<? extends String> observable, String oldValue, String newValue) {

                updateValidationState();
              }
            });

    // Listener for ToggleGroup
    voiceProfileGroup
        .selectedToggleProperty()
        .addListener(
            new ChangeListener<Toggle>() {
              @Override
              public void changed(
                  ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                updateValidationState();
              }
            });

    updateValidationState();
  }

  private void updateValidationState() {
    boolean usernameEntered = !usernameField.getText().trim().isEmpty();
    boolean profileSelected = voiceProfileGroup.getSelectedToggle() != null;
    letsGoButton.setDisable(!(usernameEntered && profileSelected));

    // Show Error if missing username
    if (!usernameEntered) {
      usernameErrorLabel.setText("Bitte geben Sie einen Benutzernamen ein.");
      usernameErrorLabel.setVisible(true);
      usernameErrorLabel.setManaged(true);
    } else {
      usernameErrorLabel.setVisible(false);
      usernameErrorLabel.setManaged(false);
    }

    // show error if missing voice profile
    if (!profileSelected) {
      voiceProfileErrorLabel.setText("Bitte wählen Sie ein Stimmprofil.");
      voiceProfileErrorLabel.setVisible(true);
      voiceProfileErrorLabel.setManaged(true);
    } else {
      voiceProfileErrorLabel.setVisible(false);
      voiceProfileErrorLabel.setManaged(false);
    }
  }

  @FXML
  protected void backtoLanding(ActionEvent event) throws IOException {
    SceneUtil.changeScene(
        (Stage) ((Node) event.getSource()).getScene().getWindow(),
        "/at/fh/burgenland/landing.fxml");
  }

  @FXML
  protected void createNewProfile(ActionEvent event) throws IOException {
    String username = usernameField.getText();
    String selectedText = ((RadioButton) voiceProfileGroup.getSelectedToggle()).getText();

    VoiceProfile voiceProfile = null;
    switch (selectedText.toLowerCase()) {
      case "männlich":
        voiceProfile = VoiceProfile.MAENNLICH;
        break;
      case "weiblich":
        voiceProfile = VoiceProfile.WEIBLICH;
        break;
      default:
        break;
    }

    UserProfile userProfile = new UserProfile(username, voiceProfile);

    saveUser(userProfile);
    ProfileManager.setCurrentProfile(userProfile);

    System.out.println(
        "Neues Profile erstellt: Benutzername= "
            + userProfile.getUserName()
            + ", Stimmprofil: "
            + userProfile.getVoiceProfile());

    SceneUtil.changeScene(
        (Stage) ((Node) event.getSource()).getScene().getWindow(),
        "/at/fh/burgenland/game_selection.fxml");
  }

  private void saveUser(UserProfile userProfile) {
    if (ProfileManager.getUserProfiles().contains(userProfile)) {
      usernameErrorLabel.setText("Benutzername bereits vergeben.");
      return;
    }
    ProfileManager.addProfile(userProfile);
  }
}
