package at.fh.burgenland.profiles;

import java.io.IOException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Controller for Profile Creation Page.
 */
public class CreateProfileController {

  @FXML
  private TextField usernameField;

  @FXML
  private Label usernameErrorLabel;

  @FXML
  private RadioButton femaleProfile;

  @FXML
  private RadioButton maleProfile;

  @FXML
  private RadioButton customProfile;

  @FXML
  private HBox customFields;

  @FXML
  private TextField minDbField, maxDbField, minHzField, maxHzField;

  @FXML
  private Label voiceProfileErrorLabel;

  @FXML
  private ToggleGroup voiceProfileGroup;

  @FXML
  private Button backButton;

  @FXML
  private Button letsGoButton;

  /**
   * Method to initialize the Buttonstate (not clickable, if form not filled out).
   */
  public void initialize() {
    letsGoButton.setDisable(true);

    // initialize ErrorLabels
    usernameErrorLabel.setVisible(false);
    usernameErrorLabel.setManaged(false);
    voiceProfileErrorLabel.setVisible(false);
    voiceProfileErrorLabel.setManaged(false);

    customFields.setVisible(customProfile.isSelected());
    voiceProfileGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
      customFields.setVisible(customProfile.isSelected());
    });

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

    IVoiceProfile voiceProfile = null;
    switch (selectedText.toLowerCase()) {
      case "männlich":
        voiceProfile = VoiceProfile.MAENNLICH;
        break;
      case "weiblich":
        voiceProfile = VoiceProfile.WEIBLICH;
        break;
      case "benutzerdefiniert":
        if (!validateCustomProfileInput()) {
          return;
        }
        int minDb = Integer.parseInt(minDbField.getText());
        int maxDb = Integer.parseInt(maxDbField.getText());
        int minHz = Integer.parseInt(minHzField.getText());
        int maxHz = Integer.parseInt(maxHzField.getText());
        voiceProfile = new CustomVoiceProfile(minDb, maxDb, minHz, maxHz);
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

    Parent root = FXMLLoader.load(getClass().getResource("/at/fh/burgenland/game_selection.fxml"));
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  @FXML
  private void onCustomSelected() {
    customFields.setVisible(customProfile.isSelected());
  }


  private void saveUser(UserProfile userProfile) {
    if (ProfileManager.getUserProfiles().contains(userProfile)) {
      usernameErrorLabel.setText("Benutzername bereits vergeben.");
      return;
    }
    ProfileManager.addProfile(userProfile);
  }

  private boolean validateCustomProfileInput(){
    try {
      int minDb = Integer.parseInt(minDbField.getText());
      int maxDb = Integer.parseInt(maxDbField.getText());
      int minHz = Integer.parseInt(minHzField.getText());
      int maxHz = Integer.parseInt(maxHzField.getText());

      if (minDb < -60 || minDb > 0 || maxDb < -60 || maxDb > 0) {
        showError("dB-Werte müssen zwischen -60 und 0 liegen.");
        return false;
      }
      if (minHz < 0 || maxHz < 0) {
        showError("Frequenzwerte dürfen nicht negativ sein.");
        return false;
      }
      if (minDb >= maxDb) {
        showError("Minimale dB muss kleiner als maximale dB sein.");
        return false;
      }
      if (minHz >= maxHz) {
        showError("min Frequenz muss kleiner als max Frequenz sein.");
        return false;
      }
      return true;
    } catch (NumberFormatException e) {
      showError("Bitte geben Sie gültige Zahlen ein.");
      return false;
    }
  }

  private void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
    alert.showAndWait();
  }



}
