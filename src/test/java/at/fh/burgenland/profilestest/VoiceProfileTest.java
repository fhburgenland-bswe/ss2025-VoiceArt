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
    assertEquals(80, male.getMinFreq());
    assertEquals(530, male.getMaxFreq());
    assertEquals(-50, male.getMinDb());
    assertEquals(-5, male.getMaxDb());
  }

  /** Tests that the female profile has the expected frequency and dB range. */
  @Test
  public void testFemaleProfileRanges() {
    VoiceProfile female = VoiceProfile.WEIBLICH;
    assertEquals(150, female.getMinFreq());
    assertEquals(1050, female.getMaxFreq());
    assertEquals(-45, female.getMinDb());
    assertEquals(-5, female.getMaxDb());
  }
}
