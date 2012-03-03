package com.gmail.takashi316.acousticpositioning;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class PlaybackAudioTrack extends AudioTrack {
	static final private int SAMPLING_RATE = 48000;
	static final public int minBufferSize = AudioTrack.getMinBufferSize(
			SAMPLING_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
			AudioFormat.ENCODING_PCM_16BIT);
	private short[] buffer;

	public PlaybackAudioTrack(short[] buffer) {
		super(AudioManager.STREAM_MUSIC, SAMPLING_RATE,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, buffer.length,
				AudioTrack.MODE_STATIC);
		this.buffer = buffer;
		write(buffer, 0, buffer.length);
	}// SineAudioTrack

	@Override
	public void play() {
		if (buffer != null)
			super.play();
		else
			throw new NullPointerException("audio buffer was already released.");
	}// play

	@Override
	public void release() {
		super.release();
		buffer = null;
	}// release

	@Override
	protected void finalize() {
		super.finalize();
		buffer = null;
	}// finalize

}// PlaybackAudioTrack
