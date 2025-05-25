package at.fh.burgenland.profiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link ProfileManager} singleton class. Ensures that the current user profile
 * is stored, saved, and retrieved correctly.
 */
public class ProfileManagerTest {

  private File tempFile;

  /**
   * Initializes the test environment before each test method execution. Creates a temporary file
   * for profiles in JSON format, sets it to delete on exit, and loads user profiles from the file.
   *
   * @throws IOException If an error occurs during file operations
   */
  @BeforeEach
  public void setUp() throws IOException {
    tempFile = File.createTempFile("profiles", ".json");
    tempFile.deleteOnExit();
    ProfileManager.loadProfilesFromJson(tempFile.getAbsolutePath());
  }

  /**
   * Performs cleanup after each test method is executed. Deletes the temporary file if it exists.
   * This ensures that the test environment is in a clean state after each test execution.
   */
  @AfterEach
  public void tearDown() {
    if (tempFile.exists()) {
      tempFile.delete();
    }
  }

  @Test
  public void testSetAndGetCurrentProfile() {
    UserProfile user = new UserProfile("Tom", VoiceProfile.MAENNLICH);
    ProfileManager.setCurrentProfile(user);

    UserProfile stored = ProfileManager.getCurrentProfile();
    assertNotNull(stored);
    assertEquals("Tom", stored.getUserName());
    assertEquals(VoiceProfile.MAENNLICH, stored.getVoiceProfile());
  }

  @Test
  public void testSaveAndLoadProfilesFromJson() throws IOException {
    // Add a profile and save it
    UserProfile newUser = new UserProfile("Anna", VoiceProfile.WEIBLICH);
    ProfileManager.addProfile(newUser);

    // Force reloading from JSON
    ProfileManager.loadProfilesFromJson(tempFile.getAbsolutePath());

    UserProfileList loadedProfiles = ProfileManager.getUserProfiles();
    assertNotNull(loadedProfiles);
    assertEquals(1, loadedProfiles.size(), "There should be one profile loaded");

    UserProfile loadedUser = loadedProfiles.get(0);
    assertEquals("Anna", loadedUser.getUserName());
    assertEquals(VoiceProfile.WEIBLICH, loadedUser.getVoiceProfile());
  }

  @Test
  public void testFileIsActuallyWritten() throws IOException {
    UserProfile newUser = new UserProfile("Max", VoiceProfile.MAENNLICH);
    ProfileManager.addProfile(newUser);

    String fileContent = Files.readString(tempFile.toPath());
    assertTrue(fileContent.contains("Max"), "The saved file should contain the username 'Max'");
  }
}
