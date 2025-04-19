package at.fh.burgenland;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import at.fh.burgenland.audioinput.AudioInputController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;


/**
 * Test for Input Selector.
 */
@ExtendWith(ApplicationExtension.class)
public class AudioInputTest {

  private AudioInputController controller;
  private ComboBox<String> audioInputComboBox;


  /**
   * Sets stage for Tests. *
   */
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

    // Wichtig: Initialisiere den Controller, falls er Logik in initialize() hat
    controller.initialize();
  }

  @Test
  void testDropdownIsVisible(FxRobot robot) {
    assertNotNull(robot.lookup("#audioInputComboBox").queryComboBox());
  }

  @Test
  void testDopdownHasItems(FxRobot robot) {
    assertNotNull(robot.lookup("#audioInputComboBox").queryComboBox().getItems());
  }


  @Test
  void testDropdownSelectsItems(FxRobot robot) {
    robot.clickOn("#audioInputComboBox");
    robot.clickOn(audioInputComboBox.getItems().get(0));
    assertNotNull(audioInputComboBox.getValue());
  }


}

