package at.fh.burgenland;

import at.fh.burgenland.card.CardController;
import at.fh.burgenland.profiles.ProfileManager;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

/** Controller class for handling game selection functionality. */
public class GameSelectionController {

  private Stage stage;
  private Scene scene;
  private Parent root;

  @FXML private Label weclomeText;

  @FXML private FlowPane cardContainer;

  /**
   * Initializes the game selection menu by setting the welcome text and adding card items for each
   * available game. Each card item includes the game title, description, and the target FXML file
   * for navigation.
   */
  public void initialize() {
    weclomeText.setText(
        "Willkommen "
            + ProfileManager.getCurrentProfile().getUserName()
            + ", wählen Sie ein Spiel aus:");

    final String[] games = {"Draw Game", "Redraw Game", "VoiceZone", "Treasure Hunt"};
    final String[] descriptions = {
      "Draw a line on the canvas using your voice pitch and volume.",
      "Redraw the line you just drew using your voice pitch and volume.",
      "Hold your Voice in the given box.",
      "Find the Treasure using your voice."
    };

    final String[] fxmlTargets = {
      "/at/fh/burgenland/landing.fxml",
      "/at/fh/burgenland/coordinate-system.fxml",
      "/at/fh/burgenland/voicezone.fxml",
      "/at/fh/burgenland/treasurehunt.fxml"
    };

    for (int i = 0; i < games.length; i++) {
      try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("card.fxml"));
        final Parent card = loader.load();

        CardController controller = loader.getController();
        controller.setTitle(games[i]);
        controller.setDescription(descriptions[i]);
        controller.setNextPageFxml(fxmlTargets[i]);

        cardContainer.getChildren().add(card);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Handles the action event when the back button is clicked. Loads the landing page FXML file,
   * sets it as the scene and displays the stage.
   *
   * @param event The ActionEvent triggered by clicking the back button
   */
  public void handleBackButton(ActionEvent event) {
    try {
      root = FXMLLoader.load(getClass().getResource("landing.fxml"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }
}
