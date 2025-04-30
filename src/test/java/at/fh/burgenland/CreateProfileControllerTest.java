package at.fh.burgenland;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;

/** Test for Create Profile Page. */
@ExtendWith(ApplicationExtension.class)
public class CreateProfileControllerTest {

  private FxRobot robot;

  /**
   * Sets Scene for Tests.
   *
   * @param stage stage zum setten
   * @throws IOException IOException
   */
  @Start
  public void start(Stage stage) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("create_profile.fxml"));
    Parent root = loader.load();
    stage.setScene(new Scene(root));
    stage.show();
  }

  @BeforeEach
  public void setUp(FxRobot robot) {
    this.robot = robot;
  }

  @AfterEach
  public void tearDown() throws Exception {
    FxToolkit.hideStage();
  }

  @Test
  void letsGoButtonIsInitiallyDisabled(FxRobot robot) {
    verifyThat("#letsGoButton", LabeledMatchers.hasText("Los geht's"));
    assertTrue(
        robot.lookup("#letsGoButton").queryButton().isDisabled(),
        "Der 'Los geht's'-Button sollte initial deaktiviert sein.");
    robot.sleep(500);
    verifyThat(
        "#usernameErrorLabel", LabeledMatchers.hasText("Bitte geben Sie einen Benutzernamen ein."));
    assertTrue(robot.lookup("#usernameErrorLabel").queryLabeled().isVisible());
    verifyThat(
        "#voiceProfileErrorLabel", LabeledMatchers.hasText("Bitte wählen Sie ein Stimmprofil."));
    assertTrue(robot.lookup("#voiceProfileErrorLabel").queryLabeled().isVisible());
  }

  @Test
  void letsGoButtonIsEnabledAfterUsernameAndProfileAreSelected(FxRobot robot) {
    robot.clickOn("#usernameField").write("TestUser");
    robot.clickOn("#femaleProfile");
    robot.sleep(5000);
    assertFalse(
        robot.lookup("#letsGoButton").queryButton().isDisabled(),
        "Der 'Los geht's'-Button sollte aktiviert sein,"
            + "wenn Benutzername und Profil ausgewählt sind.");
  }

  @Test
  void letsGoButtonRemainsDisabledAndShowsUsernameErrorIfOnlyUsernameIsEntered(FxRobot robot) {
    robot.clickOn("#usernameField").write("TestUser");
    assertTrue(
        robot.lookup("#letsGoButton").queryButton().isDisabled(),
        "Der 'Los geht's'-Button sollte deaktiviert bleiben, "
            + "wenn nur der Benutzername eingegeben wurde.");
    verifyThat(
        "#usernameErrorLabel", LabeledMatchers.hasText("Bitte geben Sie einen Benutzernamen ein."));
    assertFalse(robot.lookup("#usernameErrorLabel").queryLabeled().isVisible());

    // Deletion of Username
    robot.clickOn("#usernameField");
    if (System.getProperty("os.name").toLowerCase().contains("mac")) {
      robot.press(javafx.scene.input.KeyCode.META, javafx.scene.input.KeyCode.A);
    } else {
      robot.press(javafx.scene.input.KeyCode.CONTROL, javafx.scene.input.KeyCode.A);
    }
    robot.release(javafx.scene.input.KeyCode.A);
    robot.release(javafx.scene.input.KeyCode.META); // Wichtig: META loslassen
    robot.release(javafx.scene.input.KeyCode.CONTROL); // Wichtig: CONTROL loslassen
    robot.press(javafx.scene.input.KeyCode.DELETE);
    robot.release(javafx.scene.input.KeyCode.DELETE);

    robot.clickOn("#voiceProfileErrorLabel");

    verifyThat(
        "#usernameErrorLabel", LabeledMatchers.hasText("Bitte geben Sie einen Benutzernamen ein."));
    assertTrue(robot.lookup("#usernameErrorLabel").queryLabeled().isVisible());
  }

  @Test
  void letsGoButtonRemainsDisabledAndShowsProfileErrorIfOnlyProfileIsSelected(FxRobot robot) {
    robot.clickOn("#maleProfile");
    assertTrue(
        robot.lookup("#letsGoButton").queryButton().isDisabled(),
        "Der 'Los geht's'-Button sollte deaktiviert bleiben, wenn nur ein Profil "
            + "ausgewählt wurde.");
    verifyThat(
        "#voiceProfileErrorLabel", LabeledMatchers.hasText("Bitte wählen Sie ein Stimmprofil."));
    assertFalse(robot.lookup("#voiceProfileErrorLabel").queryLabeled().isVisible());
  }

  @Test
  void usernameIsEnteredCorrectly(FxRobot robot) {
    robot.clickOn("#usernameField").write("AnotherUser");
    verifyThat("#usernameField", TextInputControlMatchers.hasText("AnotherUser"));
  }

  @Test
  void profileIsSelectedCorrectly(FxRobot robot) {
    robot.clickOn("#childProfile");
    assertTrue(
        robot.lookup("#childProfile").queryAs(RadioButton.class).isSelected(),
        "Das 'Kind'-Profil sollte ausgewählt sein.");
    assertFalse(
        robot.lookup("#femaleProfile").queryAs(RadioButton.class).isSelected(),
        "Das 'Weiblich'-Profil sollte nicht ausgewählt sein.");
    assertFalse(
        robot.lookup("#maleProfile").queryAs(RadioButton.class).isSelected(),
        "Das 'Männlich'-Profil sollte nicht ausgewählt sein.");
  }

  @Test
  void clickingLetsGoButtonPrintsToConsole(FxRobot robot) {
    robot.clickOn("#usernameField").write("FinalUser");
    robot.clickOn("#maleProfile");
    robot.clickOn("#letsGoButton");
  }

  @Test
  void clickingBackButtonLoadsPreviousScreen(FxRobot robot) {
    robot.clickOn("#backButton");
  }

  @Test
  void errorMessagesAreShownWhenFieldsAreEmptyOnClickingLetsGo(FxRobot robot) {
    robot.clickOn("#letsGoButton");
    verifyThat(
        "#usernameErrorLabel", LabeledMatchers.hasText("Bitte geben Sie einen Benutzernamen ein."));
    assertTrue(robot.lookup("#usernameErrorLabel").queryLabeled().isVisible());
    verifyThat(
        "#voiceProfileErrorLabel", LabeledMatchers.hasText("Bitte wählen Sie ein Stimmprofil."));
    assertTrue(robot.lookup("#voiceProfileErrorLabel").queryLabeled().isVisible());
  }

  @Test
  void errorMessagesDisappearAfterFillingFields(FxRobot robot) {
    robot.clickOn("#letsGoButton");
    robot.clickOn("#usernameField").write("FixedUser");
    robot.clickOn("#femaleProfile");
    verifyThat(
        "#usernameErrorLabel", LabeledMatchers.hasText("Bitte geben Sie einen Benutzernamen ein."));
    assertFalse(robot.lookup("#usernameErrorLabel").queryLabeled().isVisible());
    verifyThat(
        "#voiceProfileErrorLabel", LabeledMatchers.hasText("Bitte wählen Sie ein Stimmprofil."));
    assertFalse(robot.lookup("#voiceProfileErrorLabel").queryLabeled().isVisible());
  }
}
