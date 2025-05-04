package at.fh.burgenland.profiles;

/**
 * Represents a user profile including the username and the selected voice profile. Used to apply
 * the correct coordinate system scaling depending on the user's voice.
 */
public class UserProfile {

  // neccessary fields
  private final String userName;
  private final VoiceProfile voiceProfile;

  // Constructor
  public UserProfile(String userName, VoiceProfile voiceProfile) {
    this.userName = userName;
    this.voiceProfile = voiceProfile;
  }

  // Getter
  public String getUserName() {
    return userName;
  }

  public VoiceProfile getVoiceProfile() {
    return voiceProfile;
  }
}
