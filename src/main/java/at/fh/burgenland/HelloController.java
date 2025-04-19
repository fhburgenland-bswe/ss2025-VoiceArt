package at.fh.burgenland;

import at.fh.burgenland.audioinput.AudioInputController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;


/** JavaFX example using for pipeline test. */
public class HelloController {

  @FXML private Label welcomeText;

  @FXML
  protected void onHelloButtonClick() {
    welcomeText.setText("Welcome to JavaFX Application!");
  }






}
