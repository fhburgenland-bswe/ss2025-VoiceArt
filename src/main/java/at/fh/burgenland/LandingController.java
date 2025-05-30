package at.fh.burgenland;

import at.fh.burgenland.utils.SceneUtil;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/** JavaFX example using for pipeline test. */
public class LandingController {

  @FXML private Label welcomeText;

  private Stage stage;
  private Scene scene;
  private Parent root;

  @FXML private HBox debugContainer; // Referenz zum Debug-Container

  /**
   * Initializes the controller by making the debug container visible and managed if it is not null.
   * This ensures that debug-related UI elements are consistently displayed in the application.
   */
  public void initialize() {
    // Debug-Buttons immer sichtbar machen
    if (debugContainer != null) {
      debugContainer.setVisible(true);
      debugContainer.setManaged(true);
    }
  }

  @FXML
  protected void onHelloButtonClick() {
    welcomeText.setText("Test");
  }

  /**
   * Switches the current scene to the "profile" view loaded from the "hello.fxml" file.
   *
   * @param event The {@link ActionEvent} that triggered the switch (e.g., a button click).
   * @throws IOException If the "hello.fxml" file cannot be loaded.
   */
  public void switchToDebug(ActionEvent event) {
    SceneUtil.changeScene(
        (Stage) ((Node) event.getSource()).getScene().getWindow(), "/at/fh/burgenland/hello.fxml");
  }

  /**
   * Opens the coordinate system view defined in coordinate-system.fxml. This method is triggered by
   * a button click and loads the corresponding FXML file.
   *
   * @param event The {@link ActionEvent} that triggers the method (button click)
   */
  public void showCoordinateSystem(ActionEvent event) {
    SceneUtil.changeScene(
        (Stage) ((Node) event.getSource()).getScene().getWindow(),
        "/at/fh/burgenland/coordinate-system.fxml");
  }
}
