package com.gmail.takashi316.acousticpositioning;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class MyAudioTrack extends AudioTrack {
	public static final int SAMPLING_RATE = 48000;
	static final private int minBufferSize = AudioTrack.getMinBufferSize(
			SAMPLING_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
			AudioFormat.ENCODING_PCM_16BIT);

	public MyAudioTrack(short[] samples) {
		super(AudioManager.STREAM_MUSIC, SAMPLING_RATE,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, Math.max(minBufferSize * 4,
						samples.length * 2), AudioTrack.MODE_STATIC);
		write(samples, 0, samples.length);
	}// a constructor
}// MyAudioTrack
