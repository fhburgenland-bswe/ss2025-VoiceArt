module at.fh.burgenland {
  requires javafx.controls;
  requires javafx.fxml;
  requires java.desktop;
  requires TarsosDSP.core;
  requires TarsosDSP.jvm;
  requires com.fasterxml.jackson.databind;
  requires static lombok;
  requires javafx.graphics;

  opens at.fh.burgenland to
      javafx.fxml;
  opens at.fh.burgenland.audioinput to
      javafx.fxml;
  opens at.fh.burgenland.fft to
      javafx.fxml;
  opens at.fh.burgenland.coordinatesystem to
      javafx.fxml;
  opens at.fh.burgenland.profiles to
      javafx.fxml,
      com.fasterxml.jackson.databind;
  opens at.fh.burgenland.card to
      javafx.fxml;
  opens at.fh.burgenland.games.treasurehunt to
      javafx.fxml;
  opens at.fh.burgenland.games.hitthepoints to
      javafx.fxml;
  opens at.fh.burgenland.games.voicezone to
      javafx.fxml;

  exports at.fh.burgenland;
  exports at.fh.burgenland.games;

  opens at.fh.burgenland.games to
      javafx.fxml;
}
