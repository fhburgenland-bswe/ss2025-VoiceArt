package at.fh.burgenland.profiles;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class CreateProfileController {

  @FXML
  private TextField usernameField;

  @FXML
  private RadioButton femaleProfile;

  @FXML
  private RadioButton maleProfile;

  @FXML
  private RadioButton childProfile;

  @FXML
  private ToggleGroup voiceProfileGroup;

  @FXML
  private Button backButton;

  @FXML
  private Button letsGoButton;

  @FXML
  protected void backtoLanding(ActionEvent event) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("/at/fh/burgenland/landing.fxml"));
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  @FXML
  protected void createNewProfile(ActionEvent event) {
    String username = usernameField.getText();
    String voiceprofile = ((RadioButton) voiceProfileGroup.getSelectedToggle()).getText();
    System.out.println(
        "Neues Profile erstellt: Benutzername= " + username + ", Stimmprofil: " + voiceprofile);

    //TODO: Hier Logik zum Speichern des Profils eventuell(?)
  }


}
