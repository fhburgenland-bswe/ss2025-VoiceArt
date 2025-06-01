package at.fh.burgenland.games.hitthepoints;

import at.fh.burgenland.profiles.ProfileManager;
import at.fh.burgenland.utils.SceneUtil;

import javafx.embed.swing.SwingFXUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

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
    SceneUtil.changeScene(
        (Stage) ((Node) event.getSource()).getScene().getWindow(),
        "/at/fh/burgenland/landing.fxml");
  }

  @FXML
  private void exportPicture(ActionEvent actionEvent) throws IOException {

    // Create folder
    File folder = new File(ProfileManager.getCurrentProfile().getUserName());
    if (!folder.exists()) folder.mkdirs();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
    String timestamp = LocalDateTime.now().format(formatter);

    // Generate filename
    String filename = "HitThePoints_" + timestamp + ".png";
    // Take snapshot
    WritableImage image = resultCanvas.snapshot(null, null);
    File outputFile = new File(folder, filename);

    try {
      ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputFile);
      System.out.println("Saved snapshot to: " + outputFile.getAbsolutePath());
    } catch (IOException e) {
      e.printStackTrace();
    }
    switchToStartScene(actionEvent);
  }
}
