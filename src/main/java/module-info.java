module at.fh.burgenland {
  requires javafx.controls;
  requires javafx.fxml;
  requires java.desktop;
  requires TarsosDSP.core;
  requires TarsosDSP.jvm;
  requires org.kordamp.bootstrapfx.core;
  requires jdk.jdi;

  opens at.fh.burgenland to
      javafx.fxml;
  opens at.fh.burgenland.audioinput to
      javafx.fxml;
  opens at.fh.burgenland.fft to
      javafx.fxml;
  opens at.fh.burgenland.coordinatesystem to
      javafx.fxml;
  opens at.fh.burgenland.profiles to
      javafx.fxml;

  exports at.fh.burgenland;
}
