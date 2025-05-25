module at.fh.burgenland {
  requires javafx.controls;
  requires javafx.fxml;
  requires java.desktop;
  requires TarsosDSP.core;
  requires TarsosDSP.jvm;
  requires org.kordamp.bootstrapfx.core;
  requires jdk.jdi;
  requires com.fasterxml.jackson.databind;
  requires static lombok;

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

  exports at.fh.burgenland;
  opens at.fh.burgenland to com.fasterxml.jackson.databind, javafx.fxml;
  exports at.fh.burgenland.profiles;
}
