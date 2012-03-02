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
				audioRecord.release();
			} else if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
				audioRecord.release();
			}
			audioRecord = null;
		}
		audioRecord = new Record(10);
	}// Record

}// AcousticPositioningActivity
