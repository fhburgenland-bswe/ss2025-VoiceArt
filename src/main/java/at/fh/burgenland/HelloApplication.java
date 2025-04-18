package at.fh.burgenland;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** JavaFX example using for pipeline test. */
public class HelloApplication extends Application {
  /* @Override
    public void start(Stage stage) throws IOException {
      FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
      Scene scene = new Scene(fxmlLoader.load(), 320, 240);
      stage.setTitle("Hello!");
      stage.setScene(scene);
      stage.show();
    }
  */

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("coordinate-system.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    stage.setTitle("Koordinatensystem");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}
