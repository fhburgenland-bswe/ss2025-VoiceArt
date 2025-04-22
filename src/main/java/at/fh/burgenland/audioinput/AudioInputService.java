package at.fh.burgenland.audioinput;

import javax.sound.sampled.Mixer;

/** Klasse für AudioInput Singleton. */
public class AudioInputService {

  private static AudioInputService instance;
  private Mixer selectedMixer;
  private int selectedMixerIndex = -1; // Optional: Speichere den Index

  private AudioInputService() {
    // Private Konstruktor für Singleton
  }

  /**
   * Liefert die Singleton-Instanz des {@code AudioInputService}. Falls noch keine Instanz
   * existiert, wird eine neue erstellt. Die Methode ist thread-sicher.
   *
   * @return Die Singleton-Instanz des {@code AudioInputService}.
   */
  public static synchronized AudioInputService getInstance() {
    if (instance == null) {
      instance = new AudioInputService();
    }
    return instance;
  }

  /**
   * Gibt den aktuell ausgewählten {@code Mixer} zurück.
   *
   * @return Der ausgewählte {@code Mixer}, oder {@code null} falls keiner ausgewählt ist.
   */
  public Mixer getSelectedMixer() {
    return selectedMixer;
  }

  /**
   * Setzt den aktuell ausgewählten {@code Mixer}.
   *
   * @param selectedMixer Der auszuwählende {@code Mixer}.
   */
  public void setSelectedMixer(Mixer selectedMixer) {
    this.selectedMixer = selectedMixer;
  }

  /**
   * Gibt den Index des aktuell ausgewählten {@code Mixer} in der Liste der verfügbaren Mixer
   * zurück.
   *
   * @return Der Index des ausgewählten {@code Mixer}, oder -1 falls keiner ausgewählt ist.
   */
  public int getSelectedMixerIndex() {
    return selectedMixerIndex;
  }

  /**
   * Setzt den Index des aktuell ausgewählten {@code Mixer} in der Liste der verfügbaren Mixer.
   *
   * @param selectedMixerIndex Der Index des auszuwählenden {@code Mixer}.
   */
  public void setSelectedMixerIndex(int selectedMixerIndex) {
    this.selectedMixerIndex = selectedMixerIndex;
  }
}
