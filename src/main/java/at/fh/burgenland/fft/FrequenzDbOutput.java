package at.fh.burgenland.fft;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

import javax.sound.sampled.LineUnavailableException;

public class FrequenzDbOutput {

    private AudioDispatcher dispatcher;
    private Thread audioThread;

    private volatile float currentPitch = -1;
    private volatile double currentDb = Double.NEGATIVE_INFINITY;

    private boolean running = false;

    private FrequencyDbListener listener;

    public void setListener(FrequencyDbListener listener) {
        this.listener = listener;
    }

    public void start() throws LineUnavailableException {
        if (running) return;

        int sampleRate = 44100;
        int bufferSize = 4096;
        int overlap = 0;

        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, bufferSize, overlap);

        dispatcher.addAudioProcessor(new PitchProcessor(
                PitchProcessor.PitchEstimationAlgorithm.YIN,
                sampleRate,
                bufferSize,
                (PitchDetectionResult result, AudioEvent audioEvent) -> {
                    currentPitch = result.getPitch();
                    maybeNotify();
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
                maybeNotify();
                return true;
            }

            @Override
            public void processingFinished() {}
        });

        audioThread = new Thread(dispatcher, "Audio Dispatcher");
        audioThread.start();
        running = true;
    }

    public void stop() {
        if (!running) return;

        dispatcher.stop();
        running = false;
    }

    private synchronized void maybeNotify() {
        if (listener != null && currentPitch != -1 && !Double.isInfinite(currentDb)) {
            listener.onData(currentPitch, currentDb);
        }
    }

    public boolean isRunning() {
        return running;
    }
}
