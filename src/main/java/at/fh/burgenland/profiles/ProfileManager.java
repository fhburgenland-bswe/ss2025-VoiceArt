package at.fh.burgenland.profiles;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.Getter;
import lombok.Setter;

/**
 * Singleton-style manager to temporarily hold the currently active user profile. Useful for
 * maintaining state across scences in this application.
 */
public class ProfileManager {

  @Getter private static UserProfileList userProfiles;
  @Getter @Setter private static UserProfile currentProfile;
  private static String filePath;

  private static void saveProfileToJson() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      String data = mapper.writeValueAsString(userProfiles);
      Files.writeString(Path.of(getFilePath()), data);

      System.out.println("Profiles saved to " + getFilePath());
    } catch (IOException e) {
      System.err.println("Failed to save profiles: " + e.getMessage());
    }
  }

  private static void setFilePath(String filePath) {
    ProfileManager.filePath = filePath;
  }

  public static String getFilePath() {
    return ProfileManager.filePath;
  }

  /**
   * Loads user profiles from a JSON file specified by the file path. If the file does not exist or
   * is empty, a new UserProfileList is created. Otherwise, the existing user profiles are read from
   * the file and set.
   *
   * @param filePath The file path of the JSON file containing user profiles
   * @throws IOException If an error occurs while reading the JSON file
   */
  public static void loadProfilesFromJson(String filePath) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    setFilePath(filePath);
    File file = new File(filePath);

    try {
      if (file.createNewFile() || file.length() == 0) {
        userProfiles = new UserProfileList();
      } else {
        setUserProfiles(mapper.readValue(file, new TypeReference<UserProfileList>() {}));
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Sets the user profiles to the specified UserProfileList. This method is used to update the
   * currently active user profiles in the ProfileManager singleton.
   *
   * @param userProfiles The UserProfileList containing the user profiles to be set
   */
  public static void setUserProfiles(UserProfileList userProfiles) {
    ProfileManager.userProfiles = userProfiles;
  }

  /**
   * Adds a user profile to the list of user profiles and saves the updated list to a JSON file.
   *
   * @param userProfile The UserProfile object to be added
   */
  public static void addProfile(UserProfile userProfile) {
    userProfiles.add(userProfile);
    saveProfileToJson();
  }
}
