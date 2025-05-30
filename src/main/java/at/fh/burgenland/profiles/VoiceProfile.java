package at.fh.burgenland.profiles;

/**
 * Enum representing predefined voice profiles (men & female). Each profile contains recommended
 * frequency and volume ranges. These values are used to configure the axes of the coordinate system
 * visualization.
 */
public enum VoiceProfile implements IfVoiceProfile {

  /** Definition of two voice profiles based on typical voice frequence ranges for men and women. */
  MAENNLICH(80, 530, -50, -5),
  WEIBLICH(150, 1050, -45, -5); // Normal -45, -5 - NUR FÜR TEST ABGEÄNDERT

  // neccessary fields for every voice profile
  private final int minFreq;
  private final int maxFreq;
  private final int minDb;
  private final int maxDb;

  // Constructor
  VoiceProfile(int minFreq, int maxFreq, int minDb, int maxDb) {
    this.minFreq = minFreq;
    this.maxFreq = maxFreq;
    this.minDb = minDb;
    this.maxDb = maxDb;
  }

  // Getter

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

  @Override
  public String toString() {
    switch (this) {
      case MAENNLICH:
        return "männlich";
      case WEIBLICH:
        return "weiblich";
      default:
        return super.toString();
    }
  }
}
