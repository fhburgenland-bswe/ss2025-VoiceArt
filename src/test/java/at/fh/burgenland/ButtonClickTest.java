package at.fh.burgenland;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/** Sample Tests with TestFX. */
@ExtendWith(ApplicationExtension.class)
class ButtonClickTest {

  private Button button;

  /**
   * Will be called with {@code @Before} semantics, i. e. before each test method.
   *
   * @param stage - Will be injected by the test runner.
   */
  @Start
  private void start(Stage stage) {
    button = new Button("click me!");
    button.setId("myButton");
    button.setOnAction(actionEvent -> button.setText("clicked!"));
    stage.setScene(new Scene(new StackPane(button), 100, 100));
    stage.show();
  }

  /**
   * clicks the button.
   *
   * @param robot - Will be injected by the test runner.
   */
  @Test
  void should_contain_button_with_text(FxRobot robot) {
    Assertions.assertThat(button).hasText("click me!");
    // or (lookup by css id):
    Assertions.assertThat(robot.lookup("#myButton").queryAs(Button.class)).hasText("click me!");
    // or (lookup by css class):
    Assertions.assertThat(robot.lookup(".button").queryAs(Button.class)).hasText("click me!");
    // or (query specific type):
    Assertions.assertThat(robot.lookup(".button").queryButton()).hasText("click me!");
  }
}
