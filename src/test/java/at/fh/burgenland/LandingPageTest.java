package at.fh.burgenland;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.isVisible;

import at.fh.burgenland.profiles.ProfileManager;
import at.fh.burgenland.profiles.UserProfile;
import at.fh.burgenland.profiles.VoiceProfile;
import java.io.File;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;

/**
 * This class contains test methods for the Landing Page functionality of the application.
 * It uses JavaFX testing utilities along with a mock file to demonstrate user interaction with
 * the landing page elements.
 */
@ExtendWith(ApplicationExtension.class)
public class LandingPageTest {

  private FxRobot robot;
  private File tempFile;

  /**
   * Starts the application by loading user profiles, adding a new user profile,
   * setting up the UI with the landing.fxml file, and showing the Stage.
   *
   * @param stage The primary stage for the application
   * @throws IOException If an error occurs while setting up the application
   */
  @Start
  public void start(Stage stage) throws IOException {
    tempFile = File.createTempFile("profiles", ".json");
    tempFile.deleteOnExit();
    ProfileManager.loadProfilesFromJson(tempFile.getAbsolutePath());

    UserProfile newUser = new UserProfile("Anna", VoiceProfile.WEIBLICH);
    ProfileManager.addProfile(newUser);
    FXMLLoader loader = new FXMLLoader(getClass().getResource("landing.fxml"));
    Parent root = loader.load();
    stage.setScene(new Scene(root));
    stage.show();
  }

  @BeforeEach
  public void setUp(FxRobot robot) throws IOException {

    this.robot = robot;
  }

  @Test
  public void testLandingPage() {
    robot.clickOn("#profiles");
    verifyThat("Anna", NodeMatchers.isVisible());
    robot.clickOn("Anna");
    //assertEquals("Anna", ProfileManager.getCurrentProfile().getUserName());

    verifyThat("#weclomeText", isVisible());
  }
}
