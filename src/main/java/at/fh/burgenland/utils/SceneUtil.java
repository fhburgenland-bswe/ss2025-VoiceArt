package at.fh.burgenland.utils;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

/**
 * Utility class for handling scene changes in a JavaFX application. This class provides a method
 * for switching between scenes, including loading FXML files and applying stylesheets.
 */
public class SceneUtil {

  /**
   * Changes the current scene displayed on a JavaFX stage by loading the specified FXML file and
   * applying default stylesheets.
   *
   * @param stage the JavaFX Stage where the scene change is to take place
   * @param fxmlPath the path to the FXML file that defines the new scene
   * @throws RuntimeException if the FXML file cannot be loaded
   */
  public static void changeScene(Stage stage, String fxmlPath) {
    Parent root = null;
    try {
      root = FXMLLoader.load(SceneUtil.class.getResource(fxmlPath));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Scene scene = new Scene(root);
    scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
    scene
        .getStylesheets()
        .add(SceneUtil.class.getResource("/at/fh/burgenland/styles.css").toExternalForm());
    stage.setScene(scene);
    stage.show();
  }
}
