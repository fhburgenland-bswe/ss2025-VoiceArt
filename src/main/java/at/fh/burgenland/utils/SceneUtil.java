package at.fh.burgenland.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

public class SceneUtil {
  public static void changeScene(Stage stage, String fxmlPath) {
    Parent root = null;
    try {
      root = FXMLLoader.load(SceneUtil.class.getResource(fxmlPath));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Scene scene = new Scene(root);
    scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
    scene.getStylesheets().add(SceneUtil.class.getResource("/at/fh/burgenland/styles.css").toExternalForm());
    stage.setScene(scene);
    stage.show();
  }
}
