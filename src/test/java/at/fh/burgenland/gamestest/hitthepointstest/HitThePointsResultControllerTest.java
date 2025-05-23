package at.fh.burgenland.gamestest.hitthepointstest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;

import at.fh.burgenland.profiles.ProfileManager;
import at.fh.burgenland.profiles.UserProfile;
import at.fh.burgenland.profiles.VoiceProfile;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;

/** Test class for the HitThePointsResultController. */
@ExtendWith(ApplicationExtension.class)
public class HitThePointsResultControllerTest {

  private FxRobot robot;

  /**
   * Initializes the JavaFX application for testing. It loads the hitpoints_result.fxml and creates
   * a dummyUser.
   *
   * @param stage the primary stage for this application
   * @throws IOException if the FXML file cannot be loaded
   */
  @Start
  public void start(Stage stage) throws IOException {
    UserProfile dummyProfile = new UserProfile("TestUser", VoiceProfile.MAENNLICH);
    ProfileManager.setCurrentProfile(dummyProfile);

    FXMLLoader loader =
        new FXMLLoader(getClass().getResource("/at/fh/burgenland/hitpoints_result.fxml"));
    Parent root = loader.load();
    stage.setScene(new Scene(root));
    stage.show();
  }

  @BeforeEach
  public void setUp(FxRobot robot) {
    this.robot = robot;
  }

  @Test
  public void testCanvasInitialization() {
    Canvas canvas = robot.lookup("#resultCanvas").queryAs(Canvas.class);
    assertNotNull(canvas, "Canvas should be initialized");
  }

  @Test
  void scoreLabelTest() {
    Label scoreLabel = robot.lookup("#scoreLabel").queryAs(Label.class);
    assertEquals("Your Score: 0", scoreLabel.getText(), "Der Score-Label sollte 'Score: 0' sein.");
  }

  @Test
  void backButtonTest(FxRobot robot) {
    verifyThat("#backButton", NodeMatchers.isVisible());
    Node backButtonNode = robot.lookup("#backButton").query();

    assertTrue(backButtonNode.isVisible(), "Back button should be visible");
    assertFalse(backButtonNode.isDisabled(), "Back button should be enabled");
  }
}
