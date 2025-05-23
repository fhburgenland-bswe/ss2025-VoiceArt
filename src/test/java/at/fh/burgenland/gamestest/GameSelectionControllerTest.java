package at.fh.burgenland.gamestest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
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

/** Test for GameSelectionController. */
@ExtendWith(ApplicationExtension.class)
public class GameSelectionControllerTest {

  private FxRobot robot;

  /**
   * Starts the application by setting up the initial user profile, loading the game selection view,
   * and displaying it on the stage.
   *
   * @param stage The primary stage of the application
   * @throws IOException If an error occurs while loading the FXML file
   */
  @Start
  public void start(Stage stage) throws IOException {
    UserProfile dummyProfile = new UserProfile("TestUser", VoiceProfile.MAENNLICH);
    ProfileManager.setCurrentProfile(dummyProfile);
    FXMLLoader loader =
        new FXMLLoader(getClass().getResource("/at/fh/burgenland/game_selection.fxml"));
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
  void pageLoadsWithCorrectTitle(FxRobot robot) {
    verifyThat(".label", LabeledMatchers.hasText("VoiceArt"));
    verifyThat(
        "#weclomeText", LabeledMatchers.hasText("Willkommen TestUser, wählen Sie ein Spiel aus:"));
  }

  @Test
  void twoGameCardsAreLoaded(FxRobot robot) {
    FlowPane container = robot.lookup("#cardContainer").queryAs(FlowPane.class);
    assertEquals(4, container.getChildren().size(), "There should be 2 game cards loaded.");
  }

  @Test
  void backButtonExistsAndIsClickable(FxRobot robot) {
    verifyThat("#backButton", NodeMatchers.isVisible());
    Node backButtonNode = robot.lookup("#backButton").query();

    assertTrue(backButtonNode.isVisible(), "Back button should be visible");
    assertFalse(backButtonNode.isDisabled(), "Back button should be enabled");
  }

  @Test
  void gameCardsContainCorrectTexts(FxRobot robot) {
    FlowPane container = robot.lookup("#cardContainer").queryAs(FlowPane.class);

    VBox firstCard = (VBox) container.getChildren().get(0);
    Label firstTitle = (Label) firstCard.lookup("#titleLabel");
    assertEquals("Draw Game", firstTitle.getText(), "First card should be 'Draw Game'");

    VBox secondCard = (VBox) container.getChildren().get(1);
    Label secondTitle = (Label) secondCard.lookup("#titleLabel");
    assertEquals(
        "Hit the Points!", secondTitle.getText(), "Second card should be 'Hit the Points!'");

    VBox thirdCard = (VBox) container.getChildren().get(2);
    Label thirdTitle = (Label) thirdCard.lookup("#titleLabel");
    assertEquals("VoiceZone", thirdTitle.getText(), "Third card should be 'VoiceZone'");

    VBox fourthCard = (VBox) container.getChildren().get(3);
    Label fourthTitle = (Label) fourthCard.lookup("#titleLabel");
    assertEquals("Treasure Hunt", fourthTitle.getText(), "Fourth card should be 'Treasure Hunt'");
  }
}
