package at.fh.burgenland.profiles;

import java.io.IOException;

import at.fh.burgenland.utils.SceneUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

/** Controller for everything profile related. */
public class ProfileController {

  @FXML private Button newProfileCreateButton;

  @FXML private ComboBox profiles;

  /**
   * Initializes the profile controller by populating the ComboBox with user profiles
   * and setting up the action for when a profile is selected.
   */
  public void initialize() {
    for (UserProfile profile : ProfileManager.getUserProfiles()) {
      profiles.getItems().add(profile);
    }
    profiles.setOnAction(
        event -> {
          System.out.println("Selected item: " + profiles.getSelectionModel().getSelectedItem());
          ProfileManager.setCurrentProfile(
              (UserProfile) profiles.getSelectionModel().getSelectedItem());
          SceneUtil.changeScene((Stage) profiles.getScene().getWindow(), "/at/fh/burgenland/game_selection.fxml");
        });
  }

  @FXML
  protected void showProfileCreationScreen(ActionEvent event) {
    SceneUtil.changeScene((Stage) ((Node) event.getSource()).getScene().getWindow(), "/at/fh/burgenland/create_profile.fxml");
  }


}
