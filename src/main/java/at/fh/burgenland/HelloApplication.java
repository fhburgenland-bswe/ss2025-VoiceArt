package at.fh.burgenland;

import at.fh.burgenland.profiles.ProfileManager;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

/** JavaFX example using for pipeline test. */
public class HelloApplication extends Application {

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("landing.fxml"));
    Scene scene = new Scene(fxmlLoader.load());

    // CSS einbinden
    scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
    scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());


    // Fenstergröße festlegen
    stage.setWidth(800);  // Breite in Pixeln
    stage.setHeight(600); // Höhe in Pixeln


    // Stage konfigurieren
    stage.setTitle("VoiceArt");
    stage.setScene(scene);
    stage.show();
  }

  /**
   * Loads user profiles from a JSON file and prints the number of profiles loaded. Launches the
   * application after loading profiles.
   *
   * @param args The command line arguments
   */
  public static void main(String[] args) {
    try {
      ProfileManager.loadProfilesFromJson("profiles.json");
      System.out.println("Profiles loaded: " + ProfileManager.getUserProfiles().size());
    } catch (IOException e) {
      System.err.println("Failed to load profiles: " + e.getMessage());
    }
    launch();
  }
}
