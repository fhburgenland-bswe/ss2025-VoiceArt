package at.fh.burgenland.games.treasurehunt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/**
 * Unit tests for the {@link TreasureHuntController} class. Verifies that the UI elements in the
 * treasure hunt view are initialized correctly and that button actions (such as reset) update the
 * UI as expected. Uses TestFX to interact with and assert the state of JavaFX components.
 */
@ExtendWith(ApplicationExtension.class)
public class TreasureHuntControllerTest {

  /**
   * Initializes the test environment by loading the Treasure Hunt FXML view, creating the scene,
   * and displaying it on the provided stage.
   *
   * @param stage The primary stage of the application on which the scene is shown.
   * @throws IOException If the FXML file cannot be loaded.
   */
  @Start
  public void start(Stage stage) throws IOException {
    FXMLLoader loader =
        new FXMLLoader(getClass().getResource("/at/fh/burgenland/treasurehunt.fxml"));
    Parent root = loader.load();
    stage.setScene(new Scene(root));
    stage.show();
  }

  @Test
  void testLevelLabelInitial(FxRobot robot) {
    Label levelLabel = robot.lookup("#levelLabel").queryAs(Label.class);
    assertEquals(
        "Level: 1", levelLabel.getText(), "Der Level-Label sollte initial 'Level 1' sein.");
  }

  @Test
  void testStartRecordingButton(FxRobot robot) {
    Button startButton = robot.lookup("#startRecordingButton").queryAs(Button.class);
    assertEquals(
        "Start Recording", startButton.getText(), "Der Button sollte 'Start Recording' sein.");
  }

  @Test
  void testResetButtonExists(FxRobot robot) {
    Button resetButton = robot.lookup("#resetButton").queryAs(Button.class);
    assertEquals("Restart Level", resetButton.getText(), "Der Reset-Button sollte 'Reset' heißen.");
  }

  @Test
  void testResetButtonSetsOverlayVisible(FxRobot robot) {
    Button resetButton = robot.lookup("#resetButton").queryAs(Button.class);
    robot.clickOn(resetButton);

    // Wartet kurz, da e sec PauseTransition verwendet wird
    robot.sleep(3500);

    // Überprüft, ob das Overlay wieder sichtbar ist
    javafx.scene.canvas.Canvas overlapCanvas =
        robot.lookup("#overlapCanvas").queryAs(javafx.scene.canvas.Canvas.class);
    assertEquals(
        true, overlapCanvas.isVisible(), "Das Overlay-Canvas sollte nach Reset sichtbar sein.");
  }
}
