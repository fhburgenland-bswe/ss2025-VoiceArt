package at.fh.burgenland.fft;

/** Represents a listener interface for handling incoming frequency and decibel data. */
public interface FrequencyDbListener {
  /**
   * Method to handle incoming frequency and decibel data.
   *
   * @param pitch The frequency pitch value
   * @param db The decibel value
   */
  void onData(float pitch, double db);
}
