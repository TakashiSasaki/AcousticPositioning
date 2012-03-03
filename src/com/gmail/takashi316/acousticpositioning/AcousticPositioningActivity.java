package com.gmail.takashi316.acousticpositioning;

import android.app.Activity;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AcousticPositioningActivity extends Activity {
	/** Called when the activity is first created. */
	Button buttonPlaySine;
	EditText editTextSineHz;
	EditText editTextSineSeconds;
	EditText editTextRecordingDuration;
	Button buttonRecord;
	Button buttonPlayRecordedAudio;

	AudioTrack audioTrack;
	AudioRecord audioRecord;

	final static private int RECORDING_BUFFER_SIZE_IN_FRAMES = 48000 * 10;
	final static private int TIME_INTERVAL_TO_READ_FRAMES_IN_MILLISECONDS = 100;
	final static private int THREASHOLD_TO_CONTINUE_READING_FRAMES = 100;
	int recordedFramesMarker = 0;
	short[] recordedFrames = new short[RECORDING_BUFFER_SIZE_IN_FRAMES];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		buttonPlaySine = (Button) findViewById(R.id.buttonPlaySine);
		editTextSineHz = (EditText) findViewById(R.id.editTextSineHz);
		editTextSineSeconds = (EditText) findViewById(R.id.editTextSineSeconds);
		editTextRecordingDuration = (EditText) findViewById(R.id.editTextRecordingDuration);
		buttonRecord = (Button) findViewById(R.id.buttonRecord);
		buttonPlayRecordedAudio = (Button) findViewById(R.id.buttonPlayRecordedAudio);

		buttonPlaySine.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Handler handler = new Handler();
				handler.post(new Runnable() {

					public void run() {
						PlaySine();
					}// run
				});// Runnable
			}// onClick
		});// OnClickListener

		buttonPlayRecordedAudio.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
			}
		});

		buttonRecord.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Record();
			}
		});

		buttonPlayRecordedAudio.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Playback();
			}
		});

	}// onCreate

	private void PlaySine() {
		int sine_hz;
		int sine_seconds;
		try {
			sine_hz = Integer.parseInt(editTextSineHz.getText().toString());
			sine_seconds = Integer.parseInt(editTextSineSeconds.getText()
					.toString());
		} catch (Exception e) {
			return;
		}

		if (audioTrack != null) {
			if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
				audioTrack.stop();
			}
			audioTrack.release();
		}
		audioTrack = new SineAudioTrack(sine_hz, sine_seconds);
		audioTrack.play();
	}// PlaySine

	@Override
	public void onStop() {
		audioTrack.stop();
		audioTrack.release();
		super.onStop();
	}// onStop

	private void Record() {
		if (audioRecord != null) {
			if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
				audioRecord.stop();
			}
			if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
				audioRecord.release();
			}
		}
		recordedFramesMarker = 0;
		audioRecord = new Record();
		Thread thread = new Thread(new Runnable() {
			public void run() {
				if(audioRecord == null)return;
				audioRecord.startRecording();
				if (audioRecord == null)
					return;
				audioRecord.startRecording();
				try {
					Log.v(new Throwable(), "waiting for preparation  ...");
					Thread.sleep(TIME_INTERVAL_TO_READ_FRAMES_IN_MILLISECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}// try
				
				while (true) {
					if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
						Log.v(new Throwable(), "AudioRecord is not initialized");
						break;
					}
					if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
						Log.v(new Throwable(), "AudioRecord is not recording.");
						break;
					}
					Log.v(new Throwable(), "recording ...");
					int read_frames = audioRecord.read(recordedFrames,
							recordedFramesMarker,
							RECORDING_BUFFER_SIZE_IN_FRAMES
									- recordedFramesMarker);
					if (read_frames == AudioRecord.ERROR_INVALID_OPERATION) {
						Log.v(new Throwable(),
								"ERROR_INVALID_OPERATION while reading from AudioRecord.");
						audioRecord.stop();
						break;
					}
					if (read_frames == AudioRecord.ERROR_BAD_VALUE) {
						Log.v(new Throwable(),
								"ERROR_BAD_VALUE while reading from AudioRecord.");
						audioRecord.stop();
						break;
					}
					Log.v(new Throwable(), "" + read_frames + " frames read");
					recordedFramesMarker += read_frames;
					if (recordedFramesMarker >= RECORDING_BUFFER_SIZE_IN_FRAMES) {
						Log.v(new Throwable(),
								"reached to RECORDING_BUFFER_SIZE_IN_FRAMES");
						break;
					}
					if (read_frames > THREASHOLD_TO_CONTINUE_READING_FRAMES)
						continue;
					try {
						Log.v(new Throwable(), "sleeping ...");
						Thread.sleep(TIME_INTERVAL_TO_READ_FRAMES_IN_MILLISECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}// try
				}// while
				Log.v(new Throwable(), "finished recording.");
			}// run
		});// Runnable
		thread.start();
	}// Record

	private void Playback() {
		if (audioTrack != null) {
			if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
				audioTrack.stop();
			}
			audioTrack.release();
		}
		audioTrack = new PlaybackAudioTrack(this.recordedFrames);
		audioTrack.play();
	}// PlayBack

}// AcousticPositioningActivity
