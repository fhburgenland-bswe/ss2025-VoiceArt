package at.fh.burgenland.profilestest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import at.fh.burgenland.profiles.UserProfile;
import at.fh.burgenland.profiles.VoiceProfile;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link UserProfile} class. Verifies that profiles are created with correct
 * usernames and voice profiles.
 */
public class UserProfileTest {

  /** Tests if the user profile stores username and voice profile correctly. */
  @Test
  public void testUserProfileCreation() {
    UserProfile user = new UserProfile("Anna", VoiceProfile.WEIBLICH);
    assertEquals("Anna", user.getUserName());
    assertEquals(VoiceProfile.WEIBLICH, user.getVoiceProfile());
  }
}
