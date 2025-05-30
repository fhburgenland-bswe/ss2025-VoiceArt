package at.fh.burgenland.profiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;
import static org.testfx.util.NodeQueryUtils.hasText;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

/**
 * Test class for {@link CustomVoiceProfile}.
 *
 * <p>This class contains unit and UI tests for the {@code CustomVoiceProfile} class using JUnit 5
 * and TestFX. It verifies correct behavior for profile creation, validation, serialization, and
 * error handling in the profile creation UI.
 */
@ExtendWith(ApplicationExtension.class)
public class CustomVoiceProfileTest {

  /**
   * Initializes the JavaFX application stage for testing.
   *
   * <p>Loads the FXML layout for the profile creation screen and sets it as the scene of the given
   * stage. This method is automatically called by TestFX before each test to prepare the UI
   * environment.
   *
   * @param stage the primary stage provided by the TestFX framework
   * @throws IOException if the FXML file cannot be loaded
   */
  @Start
  public void start(Stage stage) throws IOException {
    FXMLLoader loader =
        new FXMLLoader(getClass().getResource("/at/fh/burgenland/create_profile.fxml"));
    Parent root = loader.load();
    stage.setScene(new Scene(root));
    stage.show();
  }

  @BeforeEach
  void setUpProfile() {
    CustomVoiceProfile customProfile = new CustomVoiceProfile(100, 500, -40, -10);
    UserProfile userProfile = new UserProfile("TestUser", customProfile);
    ProfileManager.setCurrentProfile(userProfile);
  }

  @AfterEach
  public void tearDown() throws Exception {
    FxToolkit.hideStage();
  }

  @Test
  void showsErrorWhenFieldsAreEmpty(FxRobot robot) {
    robot.clickOn("#letsGoButton");
    verifyThat("#usernameErrorLabel", hasText("Bitte geben Sie einen Benutzernamen ein."));
    assertTrue(robot.lookup("#usernameErrorLabel").queryLabeled().isVisible());
    verifyThat("#voiceProfileErrorLabel", hasText("Bitte wählen Sie ein Stimmprofil."));
    assertTrue(robot.lookup("#voiceProfileErrorLabel").queryLabeled().isVisible());
  }

  @Test
  void showsAlertWithCorrectMessage(FxRobot robot) {
    robot.clickOn("#customProfile");
    robot.clickOn("#usernameField").write("TestUser"); // Name eintragen

    robot.clickOn("#minHzField").write("-10");
    robot.clickOn("#maxHzField").write("abc");
    robot.clickOn("#minDbField").write("200");
    robot.clickOn("#maxDbField").write("");
    robot.clickOn("#letsGoButton");

    // Nach dem Klick auf den Button
    WaitForAsyncUtils.waitForFxEvents();
    robot.sleep(500); // Warten, bis Alert sichtbar ist

    DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
    assertThat(dialogPane).isNotNull();
    String alertText = dialogPane.getContentText();
    assertEquals("Bitte geben Sie gültige Zahlen ein.", alertText);
  }

  @Test
  void showsErrorWhenMinHzGreaterThanMaxHz(FxRobot robot) {
    robot.clickOn("#customProfile");
    robot.clickOn("#usernameField").write("TestUser");
    robot.clickOn("#minHzField").write("600");
    robot.clickOn("#maxHzField").write("500");
    robot.clickOn("#minDbField").write("-40");
    robot.clickOn("#maxDbField").write("-10");
    robot.clickOn("#letsGoButton");

    WaitForAsyncUtils.waitForFxEvents();
    robot.sleep(500);

    DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
    assertThat(dialogPane).isNotNull();
    assertEquals(
        "Minimale Frequenz muss kleiner als maximale Frequenz sein.", dialogPane.getContentText());
  }

  @Test
  void showsErrorWhenMinDbGreaterThanMaxDb(FxRobot robot) {
    robot.clickOn("#customProfile");
    robot.clickOn("#usernameField").write("TestUser");
    robot.clickOn("#minHzField").write("100");
    robot.clickOn("#maxHzField").write("500");
    robot.clickOn("#minDbField").write("-5");
    robot.clickOn("#maxDbField").write("-10");
    robot.clickOn("#letsGoButton");

    WaitForAsyncUtils.waitForFxEvents();
    robot.sleep(500);

    DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
    assertThat(dialogPane).isNotNull();
    assertEquals("Minimale dB muss kleiner als maximale dB sein.", dialogPane.getContentText());
  }

  @Test
  public void testCustomVoiceProfile() {
    CustomVoiceProfile customProfile = new CustomVoiceProfile(-35, -8, 120, 800);
    assertEquals(-35, customProfile.getMinDb(), "Minimum dB should be -35");
    assertEquals(-8, customProfile.getMaxDb(), "Maximum dB should be -8");
    assertEquals(120, customProfile.getMinFreq(), "Minimum frequency should be 120 Hz");
    assertEquals(800, customProfile.getMaxFreq(), "Maximum frequency should be 800 Hz");
  }

  @Test
  void customProfileToStringTest() {
    CustomVoiceProfile customProfile = new CustomVoiceProfile(100, 500, -40, -10);
    assertEquals("benutzerdefiniert", customProfile.toString());
  }

  @Test
  void testSaveAndLoadCustomVoiceProfile() throws IOException {
    CustomVoiceProfile original = new CustomVoiceProfile(-30, -5, 150, 1000);

    // save profile in file
    ObjectMapper mapper = new ObjectMapper();
    File file = File.createTempFile("test", ".json");
    mapper.writeValue(file, original);

    // load profile from file
    CustomVoiceProfile loaded = mapper.readValue(file, CustomVoiceProfile.class);

    assertEquals(original.getMinDb(), loaded.getMinDb(), "Minimum dB should match");
    assertEquals(original.getMaxDb(), loaded.getMaxDb(), "Maximum dB should match");
    assertEquals(original.getMinFreq(), loaded.getMinFreq(), "Minimum frequency should match");
    assertEquals(original.getMaxFreq(), loaded.getMaxFreq(), "Maximum frequency should match");
    assertEquals(
        "benutzerdefiniert", loaded.toString(), "Profile name " + "should be 'benutzerdefiniert'");

    // delete temporary file
    file.delete();
  }
}
