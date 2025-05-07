package at.fh.burgenland.profiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

  @FXML private RadioButton childProfile;

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

  /*
  private void updateLetsGoButtonState() {
    boolean usernameEntered = !usernameField.getText().trim().isEmpty();
    boolean profileSelected = voiceProfileGroup.getSelectedToggle() != null;
    letsGoButton.setDisable(!(usernameEntered && profileSelected));
  }

   */

  @FXML
  protected void backtoLanding(ActionEvent event) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("/at/fh/burgenland/landing.fxml"));
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
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

    //ProfileManager.addProfile(userProfile);
    ProfileManager.setCurrentProfile(userProfile);

    System.out.println(
        "Neues Profile erstellt: Benutzername= "
            + userProfile.getUserName()
            + ", Stimmprofil: "
            + userProfile.getVoiceProfile());

    Parent root = FXMLLoader.load(getClass().getResource("./game_selection.fxml"));
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  private void saveUser(UserProfile userProfile) throws IOException {
    if(ProfileManager.getUserProfiles() !=null && ProfileManager.getUserProfiles().contains(userProfile)) {
      usernameErrorLabel.setText("Benutzername bereits vergeben.");
      return;
    }
    ProfileManager.addProfile(userProfile);
    ObjectMapper mapper = new ObjectMapper();

    File file = new File("profiles.json");

    List<UserProfile> profiles;

    if (file.exists()){
      try {
        if(ProfileManager.getUserProfiles().isEmpty()){
          profiles = new ArrayList<>();
        } else {
          profiles = mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, UserProfile.class));
        }
        profiles.add(userProfile);
        mapper.writeValue(file, profiles);
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
    } else {
      file.createNewFile();
      profiles = new ArrayList<>();

      profiles.add(userProfile);
      mapper.writeValue(file, profiles);
    }
  }
}
