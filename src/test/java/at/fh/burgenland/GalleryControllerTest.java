package at.fh.burgenland;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import at.fh.burgenland.profiles.ProfileManager;
import at.fh.burgenland.profiles.UserProfile;
import at.fh.burgenland.profiles.VoiceProfile;
import java.io.File;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/**
 * Test class for the `GalleryController` in the JavaFX application. This class uses TestFX to
 * perform UI tests on the gallery view, ensuring that the welcome text and accordion are correctly
 * populated.
 */
@ExtendWith(ApplicationExtension.class)
public class GalleryControllerTest {

  private FxRobot robot;

  /**
   * Initializes the JavaFX application for testing purposes. This method sets up a dummy user
   * profile, creates a directory with test image files, and loads the `gallery.fxml` file to
   * display the gallery scene.
   *
   * @param stage The primary stage for the JavaFX application.
   * @throws IOException If the `gallery.fxml` file cannot be loaded or if there is an error
   *     creating the test image files.
   */
  @Start
  public void start(Stage stage) throws IOException {
    // Create a dummy user with a folder and some test images
    String userName = "TestUser";
    ProfileManager.setCurrentProfile(new UserProfile(userName, VoiceProfile.MAENNLICH));

    // Create dummy image files for testing (optional: can be mocked better)
    File userDir = new File(userName);
    userDir.mkdirs();
    try {
      new File(userDir, "HitThePoints_2025-06-04_12-00.png").createNewFile();
      new File(userDir, "Draw_2025-06-03_18-30.png").createNewFile();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/at/fh/burgenland/gallery.fxml"));
    Parent root = loader.load();
    stage.setScene(new Scene(root));
    stage.show();
  }

  @BeforeEach
  public void setUp(FxRobot robot) {
    this.robot = robot;
  }

  @Test
  void welcomeTextIsCorrect() {
    Label welcomeLabel = robot.lookup("#weclomeText").queryAs(Label.class);
    assertNotNull(welcomeLabel);
    assertEquals(
        "Willkommen TestUser, in ihrer Galerie!",
        welcomeLabel.getText(),
        "Welcome message should include username");
  }

  @Test
  void accordionIsPopulated() {
    Accordion accordion = robot.lookup(".accordion").queryAs(Accordion.class);
    assertNotNull(accordion, "Accordion should be present");
    assertFalse(accordion.getPanes().isEmpty(), "Accordion should have panes");
  }
}
