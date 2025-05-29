package at.fh.burgenland.card;

import at.fh.burgenland.utils.SceneUtil;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lombok.Setter;

/** Controller class for handling individual card items in a game selection menu. */
public class CardController {
  @FXML private Label titleLabel;

  @FXML private Label descriptionLabel;

  @Setter private String nextPageFxml;

  /**
   * Sets the title of the card controller view.
   *
   * @param title The title text to be displayed on the card.
   */
  public void setTitle(String title) {
    titleLabel.setText(title);
  }

  /**
   * Sets the description for the card controller view.
   *
   * @param description The text to be displayed as the description on the card.
   */
  public void setDescription(String description) {
    descriptionLabel.setText(description);
  }

  @FXML
  private void handleCardClick(MouseEvent event) {
    if (nextPageFxml != null && !nextPageFxml.isEmpty()) {
      SceneUtil.changeScene(
          (Stage) ((Node) event.getSource()).getScene().getWindow(), nextPageFxml);
    }
  }
}
