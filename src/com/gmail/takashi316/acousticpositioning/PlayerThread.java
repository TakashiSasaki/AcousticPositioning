package com.gmail.takashi316.acousticpositioning;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;

public class PlayerThread extends Thread {
	private AudioTrack audioTrack;
	public static final int SAMPLING_RATE = 48000;
	private static final int minBufferSize = AudioTrack.getMinBufferSize(
			SAMPLING_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
			AudioFormat.ENCODING_PCM_16BIT);
	private static final int MAX_NUMBER_OF_SAMPLES = SAMPLING_RATE * 2;
	private boolean enabled;
	final private static int MONITOR_PLAY_INTERVAL = 200;
	int elapsed;

	private short[] samples;

	public PlayerThread(short[] samples) {
		this.samples = samples;
		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLING_RATE,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, Math.max(minBufferSize * 4,
						samples.length*2), AudioTrack.MODE_STATIC);
		this.enabled = true;
		// somewhat, it is not called back;
		this.audioTrack
				.setPlaybackPositionUpdateListener(new OnPlaybackPositionUpdateListener() {
					public void onPeriodicNotification(AudioTrack track) {
						Log.v("playing");
					}

					public void onMarkerReached(AudioTrack track) {
						track.stop();
					}
				});// setPlaybackPositionUpdateListeneer
		this.audioTrack.setNotificationMarkerPosition(samples.length);
		this.audioTrack.setPositionNotificationPeriod(100);
		this.audioTrack.write(samples, 0, samples.length);
	}// PlayerThread

	@Override
	public void run() {
		while (enabled) {
			this.audioTrack.play();
			while (this.audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
				try {
					Thread.sleep(MONITOR_PLAY_INTERVAL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}// try
					// this is an workaround because
					// setPlaybackPositionUpdateListener doesn't work.
				this.elapsed += MONITOR_PLAY_INTERVAL;
				if (elapsed > samples.length / SAMPLING_RATE * 1000) {
					elapsed = 0;
					break;
				}
			}// while
				// if (audioTrack.getPlayState() ==
				// AudioTrack.PLAYSTATE_STOPPED) {
			this.audioTrack.stop(); // call stop() before reloadStaticData()
			this.audioTrack.reloadStaticData();
			// }
		}// while
		this.audioTrack.stop();
		this.audioTrack.release();
	}// run

	public void stopPlaying() {
		enabled = false;
	}// stopPlaying

	protected void finalize() {
		if (audioTrack != null) {
			audioTrack.stop();
			audioTrack.release();
		}// if
	}// finalize

}
