package at.fh.burgenland;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/** JavaFX example using for pipeline test. */
public class TestController {

  @FXML private Label welcomeText;

  private Stage stage;
  private Scene scene;
  private Parent root;

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
  public void switchToProfile(ActionEvent event) throws IOException {
    root = FXMLLoader.load(getClass().getResource("./hello.fxml"));
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * Opens the coordinate system view defined in coordinate-system.fxml. This method is triggered by
   * a button click and loads the corresponding FXML file.
   *
   * @param event The {@link ActionEvent} that triggers the method (button click)
   */
  public void showCoordinateSystem(ActionEvent event) throws IOException {
    root = FXMLLoader.load(getClass().getResource("coordinate-system.fxml"));
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }
}
