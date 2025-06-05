package at.fh.burgenland.fft;

import at.fh.burgenland.audioinput.AudioInputController;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Represents a class for capturing audio input from either a microphone or a file, analyzing the
 * frequency pitch and dB levels, and notifying a listener.
 */
public class FrequenzDbOutput {

  private AudioDispatcher dispatcher;
  private Thread audioThread;
  private TargetDataLine targetDataLine;

  private final int sampleRate;
  private final int bufferSize;
  private final int overlap;

  private final InputSourceType sourceType;
  private Mixer mixer;
  private final File audioFile;

  private volatile float currentPitch = -1f;
  private volatile double currentDb = Double.NEGATIVE_INFINITY;
  private volatile boolean running = false;

  private FrequencyDbListener listener;

  private double noiseGateThresholdDb = -60.0;
  private boolean isGateOpen = true;

  private final AudioInputController audioInputController = new AudioInputController();

  /**
   * Constructor for the FrequenzDbOutput class with MIC input option. This constructor sets default
   * values for other parameters.
   *
   * @param mixer The Mixer object representing the microphone input to use.
   */
  // Constructor for MIC input
  public FrequenzDbOutput(Mixer mixer) {
    this(mixer, null, InputSourceType.MICROPHONE, 44100, 16000, 0);
  }

  /**
   * Constructor for FILE input. Initializes the FrequenzDbOutput object with default parameters.
   *
   * @param file The File object representing the input audio file.
   */
  // Constructor for FILE input
  public FrequenzDbOutput(File file) {
    this(null, file, InputSourceType.FILE, 44100, 2048, 0);
  }

  // Internal constructor
  private FrequenzDbOutput(
      Mixer mixer, File file, InputSourceType type, int sampleRate, int bufferSize, int overlap) {
    this.mixer = mixer;
    this.audioFile = file;
    this.sourceType = type;
    this.sampleRate = sampleRate;
    this.bufferSize = bufferSize;
    this.overlap = overlap;
  }

  /**
   * Sets the noise gate threshold in dB. Audio below this threshold will be treated as silence.
   *
   * @param thresholdDb The noise gate threshold in decibels.
   */
  public void setNoiseGateThresholdDb(double thresholdDb) {
    this.noiseGateThresholdDb = thresholdDb;
  }

  /**
   * Sets a FrequencyDbListener to receive data updates with pitch and dB values.
   *
   * @param listener The FrequencyDbListener to set for data updates
   */
  public void setListener(FrequencyDbListener listener) {
    this.listener = listener;
  }

  /**
   * Checks if the frequency db output is currently running or not.
   *
   * @return true if the frequency db output is running, false otherwise
   */
  public boolean isRunning() {
    return running;
  }

  /**
   * Gibt zurück, ob das Noise Gate gerade geöffnet ist.
   *
   * @return true, wenn das Noise Gate geöffnet ist, false andernfalls.
   */
  public boolean isGateOpen() {
    return isGateOpen;
  }

  /**
   * Starts the audio processing and analysis based on the given input source type. Throws
   * LineUnavailableException, IOException, and UnsupportedAudioFileException if initialization
   * fails. The method sets up an audio dispatcher with specified processors for pitch estimation
   * and dB level calculation, and starts the audio processing on a new thread.
   */
  public void start() {
    if (running) {
      return;
    }
    running = true;

    try {
      switch (sourceType) {
        case MICROPHONE -> {
          mixer = audioInputController.getSelectedMixer();
          dispatcher = fromMixer(mixer, sampleRate, bufferSize, overlap);
        }
        case FILE -> {
          dispatcher = AudioDispatcherFactory.fromFile(audioFile, bufferSize, overlap);
        }
        default -> throw new IllegalArgumentException(
            "Unsupported input source type: " + sourceType);
      }
    } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
      running = false;
      throw new RuntimeException("Audio initialization failed: " + e.getMessage(), e);
    }

    // the noise gate processor
    dispatcher.addAudioProcessor(
        new AudioProcessor() {
          @Override
          public boolean process(AudioEvent audioEvent) {
            float[] buffer = audioEvent.getFloatBuffer();
            double rms = 0.0;
            for (float sample : buffer) {
              rms += sample * sample;
            }
            rms = Math.sqrt(rms / buffer.length);
            double currentBlockDb = 20.0 * Math.log10(rms + 1e-10);

            if (currentBlockDb < noiseGateThresholdDb) {
              for (int i = 0; i < buffer.length; i++) {
                buffer[i] = 0;
              }
              isGateOpen = false;
            } else {
              isGateOpen = true;
            }
            return true;
          }

          @Override
          public void processingFinished() {}
        });

    dispatcher.addAudioProcessor(
        new PitchProcessor(
            PitchProcessor.PitchEstimationAlgorithm.YIN,
            sampleRate,
            bufferSize,
            (PitchDetectionResult result, AudioEvent audioEvent) -> {
              float newPitch = result.getPitch();
              if (newPitch > 0 && Math.abs(newPitch - currentPitch) > 0.01f) {
                currentPitch = newPitch;
                maybeNotify();
              }
            }));

    dispatcher.addAudioProcessor(
        new AudioProcessor() {
          @Override
          public boolean process(AudioEvent audioEvent) {
            float[] buffer = audioEvent.getFloatBuffer();
            double rms = 0.0;
            for (float sample : buffer) {
              rms += sample * sample;
            }
            rms = Math.sqrt(rms / buffer.length);
            double newDb = 20.0 * Math.log10(rms + 1e-10);

            if (!Double.isInfinite(newDb) && Math.abs(newDb - currentDb) > 0.5) {
              currentDb = newDb;
              maybeNotify();
            }
            return true;
          }

          @Override
          public void processingFinished() {}
        });

    audioThread =
        new Thread(
            () -> {
              try {
                dispatcher.run();
              } catch (Throwable t) {
                // Fehler beim Lesen des Streams ignorieren,, for testing (thx copilot)
                if (running) {
                  t.printStackTrace();
                }
              } finally {
                running = false;
              }
            },
            "Audio Dispatcher");
    audioThread.start();
  }

  /**
   * Stops the audio processing and analysis if it is currently running. This method checks if the
   * process is running, stops the audio dispatcher, and sets the running status to false.
   */
  public void stop() {
    if (!running) {
      return;
    }
    running = false;
    if (dispatcher != null) {
      dispatcher.stop();
    }
    if (audioThread != null) {
      audioThread.interrupt();
    }

    if (targetDataLine != null) {
      targetDataLine.stop();
      targetDataLine.close();
      targetDataLine = null;
    }
  }

  /**
   * Initiates the shutdown sequence for the audio processing. Stops the processing and waits for
   * the audioThread to join.
   */
  public void shutdown() {
    stop();
    if (audioThread != null) {
      try {
        audioThread.join();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
    dispatcher = null;
  }

  private synchronized void maybeNotify() {
    if (listener != null && currentPitch > 0 && !Double.isInfinite(currentDb)) {
      listener.onData(currentPitch, currentDb);
    }
  }

  private AudioDispatcher fromMixer(Mixer mixer, int sampleRate, int bufferSize, int overlap)
      throws LineUnavailableException {
    if (mixer == null) {
      return AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, bufferSize, overlap);
    }
    AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
    DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, format);

    if (!mixer.isLineSupported(dataLineInfo)) {
      throw new LineUnavailableException("Mixer does not support TargetDataLine with this format.");
    }

    TargetDataLine line = (TargetDataLine) mixer.getLine(dataLineInfo);
    line.open(format, bufferSize);
    line.start();

    this.targetDataLine = line;

    AudioInputStream stream = new AudioInputStream(line);
    return new AudioDispatcher(new JVMAudioInputStream(stream), bufferSize, overlap);
  }
}
