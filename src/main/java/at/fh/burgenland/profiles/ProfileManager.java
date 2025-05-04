package at.fh.burgenland.profiles;

/**
 * Singleton-style manager to temporarily hold the currently active user profile. Useful for
 * maintaining state across scences in this application.
 */
public class ProfileManager {

  private static UserProfile currentProfile;

  public static void setCurrentProfile(UserProfile userProfile) {
    currentProfile = userProfile;
  }

  public static UserProfile getCurrentProfile() {
    return currentProfile;
  }
}
