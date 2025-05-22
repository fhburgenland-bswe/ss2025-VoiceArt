package at.fh.burgenland.games.hitthepoints;

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

import java.io.IOException;


public class HitThePointsResultController {


  @FXML
  private VBox root;
  @FXML private Label scoreLabel;
  @FXML private Canvas resultCanvas;

  public void setScore(int score) {
    scoreLabel.setText("Your Score: " + score);
  }

  public void setCanvas(Canvas canvas) {
    root.getChildren().remove(resultCanvas);
    root.getChildren().add(1, canvas);
    resultCanvas = canvas;
    resultCanvas.setVisible(true);
  }

  @FXML
  private void switchToStartScene(ActionEvent event) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("/at/fh/burgenland/landing.fxml"));
    Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    stage.setScene(new Scene(root));
    stage.show();
  }
}
