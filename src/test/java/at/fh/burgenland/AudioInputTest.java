package at.fh.burgenland;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

import at.fh.burgenland.audioinput.AudioInputController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/** Test for Input Selector. */
@ExtendWith(ApplicationExtension.class)
public class AudioInputTest {

  private AudioInputController controller;
  private ComboBox<String> audioInputComboBox;
  private ImageView microphoneIcon;

  /** Sets stage for Tests. * */
  @Start
  public void start(Stage stage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("input_controls.fxml"));
    Parent root = loader.load();
    controller = loader.getController();

    loader.setController(controller); // Dem FXMLLoader den Controller zuweisen

    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();

    // Nach dem Laden und Setzen des Controllers die UI-Elemente abrufen
    audioInputComboBox = (ComboBox<String>) root.lookup("#audioInputComboBox");
    microphoneIcon = (ImageView) root.lookup("#microphoneIcon");

    // Wichtig: Initialisiere den Controller, falls er Logik in initialize() hat
    controller.initialize();
  }

  @Test
  void testDropdownIsVisibleInitially(FxRobot robot) {
    assertTrue(robot.lookup("#audioInputComboBox").queryComboBox().isVisible());
  }

  @Test
  void testDropdownIsThereWhileNotVisible(FxRobot robot) {

    robot.clickOn("#microphoneIcon");
    assertNotNull(robot.lookup("#audioInputComboBox").queryComboBox());
  }

  @Test
  void testDopdownHasItems(FxRobot robot) {
    assertNotNull(robot.lookup("#audioInputComboBox").queryComboBox().getItems());
  }

  @Test
  void testRecordButtonInitialText(FxRobot robot) {
    verifyThat("#recordButton", hasText("Aufnehmen"));
  }
}
