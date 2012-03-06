package com.gmail.takashi316.acousticpositioning;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AcousticPositioningActivity extends MenuActivity {
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
		{
			SineSamples sine_samples = new SineSamples(1000, 1);
			Writer writer;
			try {
				writer = new Writer();
				short[] samples = sine_samples.getSamplesInShort();
				writer.writeToWav(samples);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

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

		buttonRecord.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				try {
					Recorder.getTheRecorder().startRecording();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}// onClick
		});

		buttonPlayRecordedAudio.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					Playback();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}// onClick
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
		SineSamples sine_samples = new SineSamples(sine_hz, sine_seconds);
		audioTrack = new MyAudioTrack(sine_samples.getSamplesInShort());
		audioTrack.play();
	}// PlaySine

	@Override
	public void onStop() {
		if (audioTrack != null) {
			audioTrack.stop();
			audioTrack.release();
		}
		if (audioRecord != null) {
			audioRecord.stop();
			audioRecord.release();
			super.onStop();
		}
		super.onStop();
	}// onStop

	private void Playback() throws FileNotFoundException {
		if (audioTrack != null) {
			if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
				audioTrack.stop();
			}
			audioTrack.release();
		}
		audioTrack = new MyAudioTrack(Recorder.getTheRecorder()
				.getRecordedFrames());
		audioTrack.play();
	}// PlayBack
}// AcousticPositioningActivity
