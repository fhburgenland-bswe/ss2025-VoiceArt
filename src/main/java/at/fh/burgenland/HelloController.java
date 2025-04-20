package at.fh.burgenland;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/** JavaFX example using for pipeline test. */
public class HelloController {

  @FXML private Label welcomeText;

  @FXML
  protected void onHelloButtonClick() {
    welcomeText.setText("Welcome to JavaFX Application!");
  }
}
