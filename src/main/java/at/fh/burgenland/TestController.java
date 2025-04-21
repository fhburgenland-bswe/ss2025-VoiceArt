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

  private Stage stage;private Scene scene;
  private Parent root;

  @FXML
  protected void onHelloButtonClick() {
    welcomeText.setText("Test");
  }

  public void switchToProfile(ActionEvent event) throws IOException {
    root = FXMLLoader.load(getClass().getResource("./hello.fxml"));
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

}


