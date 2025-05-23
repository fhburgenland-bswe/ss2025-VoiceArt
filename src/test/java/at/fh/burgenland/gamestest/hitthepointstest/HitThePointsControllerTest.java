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

/** Test class for the HitThePointsController. */
@ExtendWith(ApplicationExtension.class)
public class HitThePointsControllerTest {

  private FxRobot robot;

  /**
   * Initializes the JavaFX application for testing. It loads the hitpoints.fxml and creates a
   * dummyUser.
   *
   * @param stage the primary stage for this application
   * @throws IOException if the FXML file cannot be loaded
   */
  @Start
  public void start(Stage stage) throws IOException {
    UserProfile dummyProfile = new UserProfile("TestUser", VoiceProfile.MAENNLICH);
    ProfileManager.setCurrentProfile(dummyProfile);

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/at/fh/burgenland/hitpoints.fxml"));
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
    Canvas canvas = robot.lookup("#gameCanvas").queryAs(Canvas.class);
    assertNotNull(canvas, "Canvas should be initialized");

    Canvas canvasResult = robot.lookup("#resultCanvas").queryAs(Canvas.class);
    assertNotNull(canvasResult, "Canvas should be initialized");
  }

  @Test
  void scoreLabelTest() {
    Label scoreLabel = robot.lookup("#scoreLabel").queryAs(Label.class);
    assertEquals("Score: 0", scoreLabel.getText(), "Der Score-Label sollte 'Score: 0' sein.");
  }

  @Test
  void userInfoTest() {
    Label userInfoLabel = robot.lookup("#usernameLabel").queryAs(Label.class);
    assertEquals(
        "Benutzer: TestUser",
        userInfoLabel.getText(),
        "Der UserInfo-Label sollte 'TestUser' sein.");

    Label userProfileLabel = robot.lookup("#profileLabel").queryAs(Label.class);
    assertEquals(
        "Stimmprofil: MAENNLICH",
        userProfileLabel.getText(),
        "Der UserInfo-Label sollte 'TestUser' sein.");
  }

  @Test
  public void gameSelectionButtonTest() {
    verifyThat("#gameSelectionButton", NodeMatchers.isVisible());
    Node backButtonNode = robot.lookup("#gameSelectionButton").query();

    assertTrue(backButtonNode.isVisible(), "Back button should be visible");
    assertFalse(backButtonNode.isDisabled(), "Back button should be enabled");
  }

  @Test
  void backButtonTest(FxRobot robot) {
    verifyThat("#backButton", NodeMatchers.isVisible());
    Node backButtonNode = robot.lookup("#backButton").query();

    assertTrue(backButtonNode.isVisible(), "Back button should be visible");
    assertFalse(backButtonNode.isDisabled(), "Back button should be enabled");
  }

  @Test
  void startRecordindButtonTest(FxRobot robot) {
    verifyThat("#startRecordingButton", NodeMatchers.isVisible());
    Node backButtonNode = robot.lookup("#startRecordingButton").query();

    assertTrue(backButtonNode.isVisible(), "Back button should be visible");
    assertFalse(backButtonNode.isDisabled(), "Back button should be enabled");
  }

  @Test
  void stopRecordingButtonTest(FxRobot robot) {
    verifyThat("#stopRecording", NodeMatchers.isVisible());
    Node backButtonNode = robot.lookup("#stopRecording").query();

    assertTrue(backButtonNode.isVisible(), "Back button should be visible");
    assertFalse(backButtonNode.isDisabled(), "Back button should be enabled");
  }
}
