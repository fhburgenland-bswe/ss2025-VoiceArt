package at.fh.burgenland.profiles;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a user profile including the username and the selected voice profile. Used to apply
 * the correct coordinate system scaling depending on the user's voice.
 */
@Data
@NoArgsConstructor
@Getter
public class UserProfile {

  // neccessary fields
  private String userName;
  private IVoiceProfile voiceProfile;

  // Constructor
  public UserProfile(String userName, IVoiceProfile voiceProfile) {
    this.userName = userName;
    this.voiceProfile = voiceProfile;
  }

  @Override
  public String toString() {
    return this.userName;
  }
}
