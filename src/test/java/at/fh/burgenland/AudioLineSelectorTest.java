package at.fh.burgenland;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/** Test for Input Selector. */
@ExtendWith(ApplicationExtension.class)
public class AudioLineSelectorTest {

  private HelloController controller;

  /** Sets stage for Tests. * */
  @Start
  public void start(Stage stage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("dropdown.fxml"));
    Parent root = loader.load();
    controller = loader.getController();

    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
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
    robot.clickOn(controller.audioInputComboBox.getItems().get(0));
    assertNotNull(controller.audioInputComboBox.getValue());
  }
}
