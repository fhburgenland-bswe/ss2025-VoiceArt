package at.fh.burgenland.profiles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomVoiceProfile implements IVoiceProfile {

  private int minFreq;
  private int maxFreq;
  private int minDb;
  private int maxDb;

  @JsonCreator
  public CustomVoiceProfile(
      @JsonProperty("minDb") int minDb,
      @JsonProperty("maxDb") int maxDb,
      @JsonProperty("minFreq") int minFreq,
      @JsonProperty("maxFreq") int maxFreq
  ) {
    this.minDb = minDb;
    this.maxDb = maxDb;
    this.minFreq = minFreq;
    this.maxFreq = maxFreq;
  }

  public CustomVoiceProfile() {
  }

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
