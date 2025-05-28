package at.fh.burgenland.games.hitthepoints;

import java.io.IOException;

import at.fh.burgenland.utils.SceneUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller class for the Hit The Points result screen. This class handles the display of the
 * score and the canvas in the result screen.
 */
public class HitThePointsResultController {

  @FXML private VBox root;
  @FXML private Label scoreLabel;
  @FXML private Canvas resultCanvas;

  /**
   * sets the score of the user.
   *
   * @param score the score to be displayed
   */
  public void setScore(int score) {
    scoreLabel.setText("Your Score: " + score);
  }

  /**
   * sets the canvas of the user, which is passed by the HitThePointsController.
   *
   * @param canvas the canvas to be displayed
   */
  public void setCanvas(Canvas canvas) {
    root.getChildren().remove(resultCanvas);
    root.getChildren().add(1, canvas);
    resultCanvas = canvas;
    resultCanvas.setVisible(true);
  }

  /**
   * Switches to the start scene when the back button is pressed.
   *
   * @param event the mouse click event
   * @throws IOException if the FXML file cannot be loaded
   */
  @FXML
  private void switchToStartScene(ActionEvent event) throws IOException {
    SceneUtil.changeScene((Stage) ((Node) event.getSource()).getScene().getWindow(), "/at/fh/burgenland/landing.fxml");
  }
}
