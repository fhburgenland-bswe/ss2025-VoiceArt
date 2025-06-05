package at.fh.burgenland.coordinatesystem;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;

import at.fh.burgenland.profiles.ProfileManager;
import at.fh.burgenland.profiles.UserProfile;
import at.fh.burgenland.profiles.VoiceProfile;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

@ExtendWith(ApplicationExtension.class)
public class CoordinateSystemControllerTest {

  @Start
  public void start(Stage stage) throws IOException {
    ProfileManager.setCurrentProfile(new UserProfile("TestUser", VoiceProfile.MAENNLICH));
    FXMLLoader loader =
        new FXMLLoader(getClass().getResource("/at/fh/burgenland/coordinate-system.fxml"));
    Parent root = loader.load();
    stage.setScene(new Scene(root));
    stage.show();
  }

  @BeforeEach
  void setUp(FxRobot robot) {
    // no-op
  }

  @AfterEach
  void tearDown() throws Exception {
    FxToolkit.hideStage();
  }

  @Test
  void testAllUiElementsVisible(FxRobot robot) {
    verifyThat("#logoLabel", LabeledMatchers.hasText("FreeDraw"));
    verifyThat("#colorPicker", NodeMatchers.isVisible());
    verifyThat("#coordinateSystemCanvas", NodeMatchers.isVisible());
    verifyThat("#textLabel", NodeMatchers.isVisible());
    verifyThat("#backButton", NodeMatchers.isVisible());
    verifyThat("#exportButton", NodeMatchers.isVisible());
    verifyThat("#recordingIndicator", NodeMatchers.isVisible());
  }

  @Test
  void testBackButtonIsEnabled(FxRobot robot) {
    Button backButton = robot.lookup("#backButton").queryAs(Button.class);
    assertTrue(backButton.isVisible());
    assertFalse(backButton.isDisabled());
  }

  @Test
  void testRecordingIndicatorIsInitiallyDisabled(FxRobot robot) {
    CheckBox recordingIndicator = robot.lookup("#recordingIndicator").queryAs(CheckBox.class);
    assertTrue(recordingIndicator.isDisabled(), "Checkbox should be disabled on load");
  }

  @Test
  void testCanvasExistsAndHasCorrectId(FxRobot robot) {
    Canvas canvas = robot.lookup("#coordinateSystemCanvas").queryAs(Canvas.class);
    assertTrue(canvas.isVisible());
  }

  @Test
  void testExportButtonIsEnabled(FxRobot robot) {
    Button exportButton = robot.lookup("#exportButton").queryAs(Button.class);
    assertTrue(exportButton.isVisible());
    assertFalse(exportButton.isDisabled());
  }

  @Test
  void testColorPickerCanBeOpened(FxRobot robot) {
    ColorPicker colorPicker = robot.lookup("#colorPicker").queryAs(ColorPicker.class);
    assertTrue(colorPicker.isVisible());
    robot.clickOn(colorPicker);
  }
}
