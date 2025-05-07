package at.fh.burgenland.profiles;

import java.util.List;

/**
 * Singleton-style manager to temporarily hold the currently active user profile. Useful for
 * maintaining state across scences in this application.
 */
public class ProfileManager {

  private static List<UserProfile> userProfiles;
  private static UserProfile currentProfile;

  public static void setUserProfiles(List<UserProfile> userProfiles) {
    ProfileManager.userProfiles = userProfiles;
  }
  public static List<UserProfile> getUserProfiles() {
    return userProfiles;
  }
  public static void addProfile(UserProfile userProfile) {
    userProfiles.add(userProfile);
  }

  public static void setCurrentProfile(UserProfile userProfile) {
    currentProfile = userProfile;
  }

  public static UserProfile getCurrentProfile() {
    return currentProfile;
  }
}