package com.microsoft.bot.builder.solutions.directlinespeech;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.microsoft.cognitiveservices.speech.audio.AudioStreamFormat;
import com.microsoft.cognitiveservices.speech.audio.PullAudioInputStreamCallback;

public class MicrophoneStream extends PullAudioInputStreamCallback {

    // CONSTANTS
    private final static int SAMPLE_RATE = 16000;

    // STATE
    private final AudioStreamFormat format;
    private AudioRecord recorder;

    public MicrophoneStream() {
        this.format = AudioStreamFormat.getWaveFormatPCM(SAMPLE_RATE, (short)16, (short)1);
        this.initMic();
    }

    public AudioStreamFormat getFormat() {
        return this.format;
    }

    @Override
    public int read(byte[] bytes) {
        long ret = this.recorder.read(bytes, 0, bytes.length);
        return (int)ret;
    }

    @Override
    public void close() {
        this.recorder.stop();
        this.recorder.release();
        this.recorder = null;
    }

    private void initMic() {
        // Note: currently, the Speech SDK support 16 kHz sample rate, 16 bit samples, mono (single-channel) only.
        AudioFormat af = new AudioFormat.Builder()
                .setSampleRate(SAMPLE_RATE)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                .build();
        this.recorder = new AudioRecord.Builder()
                .setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION)
                .setAudioFormat(af)
                .build();
        Log.d("mic",validateMicAvailability()?"accessible":"not accessible");
        this.recorder.startRecording();
    }

    private boolean validateMicAvailability(){
        Boolean available = true;
        AudioRecord recorder =
                new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, 44100);
        try{
            if(recorder.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED ){
                available = false;
            }

            recorder.startRecording();
            if(recorder.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING){
                recorder.stop();
                available = false;
            }
            recorder.stop();
        } finally{
            recorder.release();
            recorder = null;
        }
        return available;
    }
}

