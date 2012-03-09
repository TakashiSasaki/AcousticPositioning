package com.gmail.takashi316.acousticpositioning;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class PlayerThread extends Thread {
	static private AudioTrack audioTrack;
	public static final int SAMPLING_RATE = 48000;
	static final private int minBufferSize = AudioTrack.getMinBufferSize(
			SAMPLING_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
			AudioFormat.ENCODING_PCM_16BIT);
	static final private int MAX_NUMBER_OF_SAMPLES = SAMPLING_RATE * 2;
	short[] samples;

	public PlayerThread(short[] samples) {
		if (audioTrack == null) {
			audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
					SAMPLING_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT, Math.max(minBufferSize * 4,
							MAX_NUMBER_OF_SAMPLES), AudioTrack.MODE_STATIC);
		}// if
		this.samples = samples;
	}// PlayerThread

	static boolean enabled;
	final private static int MONITOR_PLAY_INTERVAL = 200;

	@Override
	public void run() {
		audioTrack.write(samples, 0, samples.length);
		while (enabled) {
			audioTrack.play();
			try {
				Thread.sleep(MONITOR_PLAY_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}// try
			if(PlayerThread.audioTrack.getPlayState() == AudioTrack.PLAYSTATE_STOPPED){
				PlayerThread.audioTrack.reloadStaticData();
			}
		}// while
		PlayerThread.audioTrack.stop();
	}// run

	public void stopPlaying() {
		PlayerThread.enabled = false;
	}// stopPlaying

	protected void finalize() {
		if (audioTrack != null) {
			audioTrack.stop();
			audioTrack.release();
		}// if
	}// finalize

}
