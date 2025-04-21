package at.fh.burgenland.fft;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;

public class FrequenzDbOutput {

    private static volatile float currentPitch = -1;
    private static volatile double currentDb = Double.NEGATIVE_INFINITY;

    public static void main(String[] args) throws LineUnavailableException {
        int sampleRate = 44100;
        int bufferSize = 4096;
        int overlap = 0;

        Mixer.Info selectedMixer = AudioSystem.getMixerInfo()[0];
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, bufferSize, overlap);

        dispatcher.addAudioProcessor(new PitchProcessor(
                PitchProcessor.PitchEstimationAlgorithm.YIN,
                sampleRate,
                bufferSize,
                (PitchDetectionResult result, AudioEvent audioEvent) -> {
                    currentPitch = result.getPitch();
                    maybePrint(currentPitch, currentDb);
                }
        ));

        dispatcher.addAudioProcessor(new AudioProcessor() {
            @Override
            public boolean process(AudioEvent audioEvent) {
                float[] buffer = audioEvent.getFloatBuffer();
                double rms = 0;
                for (float sample : buffer) {
                    rms += sample * sample;
                }
                rms = Math.sqrt(rms / buffer.length);
                currentDb = 20.0 * Math.log10(rms);
                maybePrint(currentPitch, currentDb);
                return true;
            }

            @Override
            public void processingFinished() {}
        });

        new Thread(dispatcher).start();
    }

    private static synchronized void maybePrint(float pitch, double db) {
        if (pitch != -1 && !Double.isInfinite(db)) {
            System.out.printf("Frequency: %.2f Hz | dB Level: %.2f dB%n", pitch, db);
        }
    }
}
