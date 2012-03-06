package com.gmail.takashi316.acousticpositioning;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AcousticPositioningActivity extends MenuActivity {
	Button buttonPlaySine;
	EditText editTextSineHz;
	EditText editTextSineSeconds;
	EditText editTextRecordingDuration;
	Button buttonRecord;
	Button buttonPlayRecordedAudio;
	EditText editTextMd5;

	AudioTrack audioTrack;
	Recorder recorder;

	short[] recordedSamples;

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
		editTextMd5 = (EditText) findViewById(R.id.editTextMd5OfRecordedSamples);

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
					recorder = new Recorder(new Runnable() {
						public void run() {
							recordedSamples = recorder.getRecordedFrames();
							Writer writer;
							try {
								writer = new Writer();
								writer.writeToCsv(recordedSamples);
								writer.writeToWav(recordedSamples);
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
							try {
								final Md5 md5 = new Md5();
								md5.putBigEndian(recordedSamples);
								AcousticPositioningActivity.this
										.runOnUiThread(new Runnable() {
											public void run() {
												editTextMd5.setText(md5
														.getMd5String());
											}// run
										});
							} catch (NoSuchAlgorithmException e) {
								e.printStackTrace();
							}
						}// run
					});
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
		if (recorder != null) {
			recorder.stopRecording();
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
		audioTrack = new MyAudioTrack(recordedSamples);
		audioTrack.play();
	}// PlayBack
}// AcousticPositioningActivity
