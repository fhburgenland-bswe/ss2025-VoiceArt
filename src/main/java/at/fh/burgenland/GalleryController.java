package at.fh.burgenland;

import at.fh.burgenland.profiles.ProfileManager;
import at.fh.burgenland.utils.SceneUtil;
import java.io.File;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller class for managing the gallery view in the application. This class handles the
 * initialization of the gallery, loading images from the user's directory, and navigating back to
 * the game selection screen.
 */
public class GalleryController {

  @FXML private Label weclomeText;
  @FXML private Accordion galleryAccordion;

  /**
   * Initializes the gallery view by setting the welcome text and loading images into the accordion.
   */
  public void initialize() {
    weclomeText.setText(
        "Willkommen " + ProfileManager.getCurrentProfile().getUserName() + ", in Ihrer Galerie!");

    loadImages();
  }

  private void loadImages() {
    String userName = ProfileManager.getCurrentProfile().getUserName();
    File userDir = new File(userName); // assumes working directory is project root

    if (!userDir.exists() || !userDir.isDirectory()) {
      return;
    }

    File[] allImages =
        userDir.listFiles(
            (dir, name) ->
                name.matches(
                    "(HitThePoints|FreeDraw)_\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}-\\d{2}\\.png"));

    if (allImages == null || allImages.length == 0) {
      return;
    }

    // Sort descending by filename (assuming filename contains the date)
    Arrays.sort(allImages, Comparator.comparing(File::getName, Collator.getInstance()).reversed());

    VBox drawImagesBox = new VBox(10);
    drawImagesBox.setAlignment(Pos.CENTER);
    VBox hitThePointsImagesBox = new VBox(10);
    hitThePointsImagesBox.setAlignment(Pos.CENTER);

    for (File imgFile : allImages) {
      Image image = new Image(imgFile.toURI().toString()); // No scaling on load
      ImageView imageView = new ImageView(image);
      imageView.setFitWidth(600); // Resize visually
      imageView.setPreserveRatio(true); // Keep aspect ratio
      imageView.setSmooth(true); // Enable smoothing for better downscale
      // Extract timestamp from filename (removing prefix and .png)
      String fileName = imgFile.getName();
      String dateTimeRaw =
          fileName.replaceFirst("^(HitThePoints|FreeDraw)_", "").replace(".png", "");
      String labelText = dateTimeRaw.replace('_', ' ').replace('-', ':');
      // Result: "2025:06:02 16:26"

      Label dateLabel = new Label(labelText);
      VBox imageWithLabel = new VBox(5, imageView, dateLabel);

      if (fileName.startsWith("FreeDraw")) {
        drawImagesBox.getChildren().add(imageWithLabel);
      } else if (fileName.startsWith("HitThePoints")) {
        hitThePointsImagesBox.getChildren().add(imageWithLabel);
      }
    }

    TitledPane drawPane = new TitledPane("FreeDraw", drawImagesBox);
    TitledPane hitThePointsPane = new TitledPane("HitThePoints", hitThePointsImagesBox);

    galleryAccordion.getPanes().addAll(hitThePointsPane, drawPane);
  }

  /**
   * Handles the action event when the back button is clicked. Navigates the user back to the game
   * selection screen by changing the current scene.
   *
   * @param event The ActionEvent triggered by clicking the back button.
   */
  public void handleBackButton(ActionEvent event) {
    SceneUtil.changeScene(
        (Stage) ((Node) event.getSource()).getScene().getWindow(),
        "/at/fh/burgenland/game_selection.fxml");
  }
}
