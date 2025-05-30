package at.fh.burgenland.profiles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a custom voice profile with user-defined frequency and decibel ranges. This class
 * implements the {@link IfVoiceProfile} interface and allows the creation of voice profiles with
 * arbitrary minimum and maximum frequency (Hz) and decibel (dB) values. It is used for cases where
 * the predefined profiles do not fit the user's needs.
 */
public class CustomVoiceProfile implements IfVoiceProfile {

  private int minFreq;
  private int maxFreq;
  private int minDb;
  private int maxDb;

  /**
   * Constructs a new {@code CustomVoiceProfile} with the specified minimum and maximum decibel and
   * frequency values.
   *
   * <p>This constructor is annotated with {@link com.fasterxml.jackson.annotation.JsonCreator} and
   * {@link com.fasterxml.jackson.annotation.JsonProperty} to support JSON deserialization.
   *
   * @param minDb the minimum decibel value for the profile
   * @param maxDb the maximum decibel value for the profile
   * @param minFreq the minimum frequency (Hz) for the profile
   * @param maxFreq the maximum frequency (Hz) for the profile
   */
  @JsonCreator
  public CustomVoiceProfile(
      @JsonProperty("minDb") int minDb,
      @JsonProperty("maxDb") int maxDb,
      @JsonProperty("minFreq") int minFreq,
      @JsonProperty("maxFreq") int maxFreq) {
    this.minDb = minDb;
    this.maxDb = maxDb;
    this.minFreq = minFreq;
    this.maxFreq = maxFreq;
  }

  public CustomVoiceProfile() {}

  @Override
  public int getMinFreq() {
    return minFreq;
  }

  @Override
  public int getMaxFreq() {
    return maxFreq;
  }

  @Override
  public int getMinDb() {
    return minDb;
  }

  @Override
  public int getMaxDb() {
    return maxDb;
  }

  public void setMinFreq(int minFreq) {
    this.minFreq = minFreq;
  }

  public void setMaxFreq(int maxFreq) {
    this.maxFreq = maxFreq;
  }

  public void setMinDb(int minDb) {
    this.minDb = minDb;
  }

  public void setMaxDb(int maxDb) {
    this.maxDb = maxDb;
  }

  @Override
  public String toString() {
    return "benutzerdefiniert";
  }
}
