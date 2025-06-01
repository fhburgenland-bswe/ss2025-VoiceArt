package at.fh.burgenland;

import at.fh.burgenland.profiles.ProfileManager;
import at.fh.burgenland.utils.SceneUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;

public class GalleryController {

    @FXML
    private Label weclomeText;
    @FXML private Accordion galleryAccordion;

    public void initialize() {
        weclomeText.setText(
                "Willkommen "
                        + ProfileManager.getCurrentProfile().getUserName()
                        + ", in ihrer Galerie!");

        loadImages();
    }

    private void loadImages() {
        String userName = ProfileManager.getCurrentProfile().getUserName();
        File userDir = new File(userName); // assumes working directory is project root

        if (!userDir.exists() || !userDir.isDirectory()) return;

        File[] allImages = userDir.listFiles((dir, name) ->
                name.matches("(HitThePoints|draw)-\\d{4}-\\d{2}-\\d{2}-\\d{2}-\\d{2}\\.png"));

        if (allImages == null || allImages.length == 0) return;

        // Sort descending by filename (assuming filename contains the date)
        Arrays.sort(allImages, Comparator.comparing(File::getName, Collator.getInstance()).reversed());

        VBox drawImagesBox = new VBox(10);
        VBox hitThePointsImagesBox = new VBox(10);

        for (File imgFile : allImages) {
            Image image = new Image(imgFile.toURI().toString(), 300, 0, true, true);
            ImageView imageView = new ImageView(image);

            if (imgFile.getName().startsWith("draw")) {
                drawImagesBox.getChildren().add(imageView);
            } else if (imgFile.getName().startsWith("HitThePoints")) {
                hitThePointsImagesBox.getChildren().add(imageView);
            }
        }

        TitledPane drawPane = new TitledPane("Zeichnungen", drawImagesBox);
        TitledPane hitThePointsPane = new TitledPane("HitThePoints", hitThePointsImagesBox);

        galleryAccordion.getPanes().addAll(hitThePointsPane, drawPane);
    }

    public void handleBackButton(ActionEvent event) {
        SceneUtil.changeScene(
                (Stage) ((Node) event.getSource()).getScene().getWindow(),
                "/at/fh/burgenland/landing.fxml");
    }
}
