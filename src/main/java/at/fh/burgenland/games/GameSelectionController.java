package at.fh.burgenland.games;

import at.fh.burgenland.card.CardController;
import at.fh.burgenland.profiles.ProfileManager;
import at.fh.burgenland.utils.SceneUtil;
import java.io.IOException;
import java.net.URL;
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

    final String[] games = {"FreeDraw", "Hit the Points!", "VoiceZone", "TreasureHunt"};
    final String[] descriptions = {
      "Lassen Sie Ihre Stimme zeichnen!",
      "Fangen Sie mit Ihrer Stimme die angezeigten Punkte!",
      "Halten Sie Ihre Stimme im Zielbereich!",
      "Finden Sie mit Ihrer Stimme den Schatz!"
    };
    final String[] fxmlTargets = {
      "/at/fh/burgenland/coordinate-system.fxml",
      "/at/fh/burgenland/hitpoints.fxml",
      "/at/fh/burgenland/voicezone.fxml",
      "/at/fh/burgenland/treasurehunt.fxml"
    };

    for (int i = 0; i < games.length; i++) {
      try {
        // FXMLLoader loader = new FXMLLoader(getClass().getResource("at/fh/burgenland/card.fxml"));
        // final Parent card = loader.load();

        // Korrekter Ansatz
        URL location = getClass().getResource("/at/fh/burgenland/card.fxml");
        FXMLLoader loader = new FXMLLoader(location);
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
    SceneUtil.changeScene(
        (Stage) ((Node) event.getSource()).getScene().getWindow(),
        "/at/fh/burgenland/landing.fxml");
  }
}
