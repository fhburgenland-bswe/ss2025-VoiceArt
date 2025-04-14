package at.fh.burgenland;

// java.util.Scanner;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

/** Klasse zum Auswaehlen des Inputs. */
public class AudioLineSelector {

  /* vielleicht später gebraucht
   public static TargetDataLine selectLineInteractively(AudioFormat format)
       throws LineUnavailableException {
     Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
     System.out.println("Available Mixers:");
     for (int i = 0; i < mixerInfos.length; i++) {
       System.out.println(i + ": " + mixerInfos[i].getName());
     }
     Scanner scanner = new Scanner(System.in);
     System.out.print("Enter mixer index: ");
     int mixerIndex = scanner.nextInt();
     return selectLineByIndex(mixerIndex, format);
   }

  */

  /** Methode zu Mikro-Auswahl. * */
  public static TargetDataLine selectLineByIndex(int mixerIndex, AudioFormat format)
      throws LineUnavailableException {
    Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
    if (mixerIndex >= 0 && mixerIndex < mixerInfos.length) {
      Mixer mixer = AudioSystem.getMixer(mixerInfos[mixerIndex]);
      Line.Info[] targetLineInfos = mixer.getTargetLineInfo();
      for (Line.Info lineInfo : targetLineInfos) {
        if (lineInfo.getLineClass().equals(TargetDataLine.class)) {
          try {
            TargetDataLine line = (TargetDataLine) mixer.getLine(lineInfo);
            line.open(format);
            return line;
          } catch (LineUnavailableException e) {
            System.err.println("Line unavailable: " + lineInfo);
          }
        }
      }
      /*
      throw new LineUnavailableException(
          "No suitable TargetDataLine found for mixer at index: " + mixerIndex);

       */
    } else {
      throw new IllegalArgumentException("Invalid mixer index: " + mixerIndex);
    }
    return null;
  }
}
