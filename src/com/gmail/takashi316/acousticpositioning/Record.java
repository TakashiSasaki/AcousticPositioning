package com.gmail.takashi316.acousticpositioning;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;

public class Record extends AudioRecord {
	final static private int SAMPLING_RATE = 48000;
	final static private int NOTIFICATION_PERIOD_IN_FRAME = SAMPLING_RATE;
	final static public int MIN_BUFFER_SIZE = AudioRecord.getMinBufferSize(
			SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO,
			AudioFormat.ENCODING_PCM_16BIT);

	// short[]buffer;

	public Record() throws IllegalArgumentException {
		super(AudioSource.MIC, SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT, Math.max(MIN_BUFFER_SIZE * 4,
						SAMPLING_RATE * 2 * 2));
		// int buffer_size = Math
		// .max(MIN_BUFFER_SIZE * 2, SAMPLING_RATE * seconds);
		// buffer = new short[buffer_size];

		setNotificationMarkerPosition(MIN_BUFFER_SIZE * 3);
		setPositionNotificationPeriod(NOTIFICATION_PERIOD_IN_FRAME);
		setRecordPositionUpdateListener(new OnRecordPositionUpdateListener() {

			public void onPeriodicNotification(AudioRecord recorder) {
				Log.v(new Throwable(), "periodic notification from recorder");
			}

			public void onMarkerReached(AudioRecord recorder) {
				Log.v(new Throwable(), "reached to marker");
			}
		});
	}// a constructor

	@Override
	protected void finalize() {
		super.finalize();
		// buffer = null;
	}// finalize

	@Override
	public void release() {
		super.release();
		// buffer = null;
	}

}// Record