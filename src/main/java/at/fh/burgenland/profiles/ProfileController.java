package at.fh.burgenland.profiles;

import java.io.IOException;
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
   * Initializes the profile controller by populating the ComboBox with user profiles and setting up
   * the action for when a profile is selected.
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
          switchPage("/at/fh/burgenland/game_selection.fxml");
        });
  }

  @FXML
  protected void showProfileCreationScreen(ActionEvent event) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("/at/fh/burgenland/create_profile.fxml"));
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  protected void switchPage(String path) {
    FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
    Parent nextPage = null;
    try {
      nextPage = loader.load();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    Stage stage = (Stage) profiles.getScene().getWindow();
    stage.setScene(new Scene(nextPage));
  }
}
