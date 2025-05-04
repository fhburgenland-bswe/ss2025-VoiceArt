package at.fh.burgenland.profilestest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import at.fh.burgenland.profiles.ProfileManager;
import at.fh.burgenland.profiles.UserProfile;
import at.fh.burgenland.profiles.VoiceProfile;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link ProfileManager} singleton class. Ensures that the current user profile
 * is stored and retrieved correctly.
 */
public class ProfileManagerTest {

  /** Tests if a user profile can be set and retrieved via the singleton. */
  @Test
  public void testSetAndGetCurrentProfile() {
    UserProfile user = new UserProfile("Tom", VoiceProfile.MAENNLICH);
    ProfileManager.setCurrentProfile(user);

    UserProfile stored = ProfileManager.getCurrentProfile();
    assertNotNull(stored);
    assertEquals("Tom", stored.getUserName());
    assertEquals(VoiceProfile.MAENNLICH, stored.getVoiceProfile());
  }
}
