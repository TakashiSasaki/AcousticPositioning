package com.gmail.takashi316.acousticpositioning;

import android.app.Activity;
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
	AudioTrack audioTrack;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		buttonPlaySine = (Button) findViewById(R.id.buttonPlaySine);
		editTextSineHz = (EditText) findViewById(R.id.editTextSineHz);
		editTextSineSeconds = (EditText) findViewById(R.id.editTextSineSeconds);

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
		audioTrack.release();
		super.onStop();
	}// onStop

}// AcousticPositioningActivity
