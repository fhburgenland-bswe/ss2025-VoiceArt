package at.fh.burgenland;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import at.fh.burgenland.fft.FrequenzDbOutput;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.Test;

class FrequenzDbOutputTest {

  @Test
  void testFrequenzDbOutputWithFile() throws Exception {
    URL resource = getClass().getClassLoader().getResource("test_audio.wav");
    assertNotNull(resource, "Audio resource should exist");

    File testAudio = new File(resource.toURI());
    assertTrue(testAudio.exists(), "Test audio file must exist");

    FrequenzDbOutput output = new FrequenzDbOutput(testAudio);

    List<Float> capturedPitches = new ArrayList<>();
    List<Double> capturedDb = new ArrayList<>();

    CountDownLatch latch = new CountDownLatch(1);

    output.setListener(
        (pitch, db) -> {
          if (pitch > 0 && !Double.isInfinite(db)) {
            capturedPitches.add(pitch);
            capturedDb.add(db);
            latch.countDown();
          }
        });
    output.start();
    boolean completed = latch.await(5, java.util.concurrent.TimeUnit.SECONDS);

    output.shutdown();

    assertTrue(completed, "Should receive at least 1 data events");
    assertFalse(capturedPitches.isEmpty(), "Captured pitch data should not be empty");
    assertFalse(capturedDb.isEmpty(), "Captured dB data should not be empty");

    System.out.println("Captured Pitches: " + capturedPitches);
    System.out.println("Captured dB Levels: " + capturedDb);
  }
}
