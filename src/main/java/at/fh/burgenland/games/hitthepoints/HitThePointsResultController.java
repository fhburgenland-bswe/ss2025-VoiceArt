package at.fh.burgenland.games.hitthepoints;

import at.fh.burgenland.profiles.ProfileManager;
import at.fh.burgenland.utils.SceneUtil;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.embed.swing.SwingFXUtils;
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
        "/at/fh/burgenland/game_selection.fxml");
  }

  @FXML
  private void exportPicture(ActionEvent actionEvent) throws IOException {

    // Create folder
    File folder = new File(ProfileManager.getCurrentProfile().getUserName());
    if (!folder.exists()) {
      folder.mkdirs();
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    String timestamp = LocalDateTime.now().format(formatter);

    // Generate filename
    String filename = "HitThePoints_" + timestamp + ".png";

    // Take snapshot
    WritableImage fxImage = resultCanvas.snapshot(null, null);
    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(fxImage, null);

    // Draw the score in the top-right corner
    Graphics2D g2d = bufferedImage.createGraphics();
    g2d.setFont(new Font("Arial", Font.BOLD, 20));
    g2d.setColor(Color.BLUE); // Change color as needed

    String scoreText = scoreLabel.getText(); // "Your Score: 123"
    FontMetrics metrics = g2d.getFontMetrics();
    int textWidth = metrics.stringWidth(scoreText);
    int textHeight = metrics.getHeight();

    int x = bufferedImage.getWidth() - textWidth - 10;
    int y = textHeight;

    g2d.drawString(scoreText, x, y);
    g2d.dispose();

    // Write image to file
    File outputFile = new File(folder, filename);
    try {
      ImageIO.write(bufferedImage, "png", outputFile);
      System.out.println("Saved snapshot to: " + outputFile.getAbsolutePath());
    } catch (IOException e) {
      e.printStackTrace();
    }

    switchToStartScene(actionEvent);
  }
}
