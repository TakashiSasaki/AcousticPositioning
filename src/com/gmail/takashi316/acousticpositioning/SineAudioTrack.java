package com.gmail.takashi316.acousticpositioning;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class SineAudioTrack extends AudioTrack {
	private int frequency;
	static final private int SAMPLING_RATE = 44100;
	static final private int minBufferSize = AudioTrack.getMinBufferSize(
			SAMPLING_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
			AudioFormat.ENCODING_PCM_16BIT);
	private short[] buffer;

	public SineAudioTrack(int frequency, int seconds) {
		super(AudioManager.STREAM_MUSIC, SAMPLING_RATE,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, SAMPLING_RATE * seconds,
				AudioTrack.MODE_STATIC);
		int buffer_size = Math.max(minBufferSize, SAMPLING_RATE * seconds);
		buffer = new short[buffer_size];
		this.frequency = frequency;
		writeBuffer(buffer);
		write(buffer, 0, buffer.length);
	}

	protected void writeBuffer(byte[] buffer) {
		for (int i = 0; i < buffer.length; ++i) {
			if (i > SAMPLING_RATE) {
				buffer[i] = buffer[i - SAMPLING_RATE];
			} else {
				double t = (double) i * (1.0d / (double) SAMPLING_RATE);
				double signal = Math.sin(2.0d * Math.PI
						* (double) this.frequency * t) * 127.0d;
				byte byte_signal = (byte) signal;
				buffer[i] = byte_signal;
			}
		}// for
	}

	protected void writeBuffer(short[] buffer) {
		for (int i = 0; i < buffer.length; ++i) {
			if (i > SAMPLING_RATE) {
				buffer[i] = buffer[i - SAMPLING_RATE];
			} else {
				double t = (double) i * (1.0d / (double) SAMPLING_RATE);
				double signal = Math.sin(2.0d * Math.PI
						* (double) this.frequency * t) * 32767.0d;
				short short_signal = (short) signal;
				buffer[i] = short_signal;
			}
		}// for
	}
}
