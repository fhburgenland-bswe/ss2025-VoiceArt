package at.fh.burgenland.profilestest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import at.fh.burgenland.profiles.VoiceProfile;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link VoiceProfile} enum. These tests verify the correct frequency and dB
 * ranges for each voice profile (e.g., male and female).
 */
public class VoiceProfileTest {

  /** Tests that the male profile has the expected frequency and dB range. */
  @Test
  public void testMaleProfileRanges() {
    VoiceProfile male = VoiceProfile.MAENNLICH;
    assertEquals(85, male.getMinFreq());
    assertEquals(180, male.getMaxFreq());
    assertEquals(-50, male.getMinDb());
    assertEquals(0, male.getMaxDb());
  }

  /** Tests that the female profile has the expected frequency and dB range. */
  @Test
  public void testFemaleProfileRanges() {
    VoiceProfile female = VoiceProfile.WEIBLICH;
    assertEquals(165, female.getMinFreq());
    assertEquals(255, female.getMaxFreq());
    assertEquals(-45, female.getMinDb());
    assertEquals(0, female.getMaxDb());
  }
}
