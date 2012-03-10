package com.gmail.takashi316.acousticpositioning;

import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import android.graphics.Color;
import android.media.AudioTrack;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AcousticPositioningActivity extends MenuActivity {
	private EditText editTextSineHz;
	private EditText editTextSineSeconds;
	EditText editTextRecordingDuration;
	EditText editTextMd5OfGeneratedSamples;
	EditText editTextMd5OfRecordedSamples;
	EditText editTextPeakFrequency;

	private AudioTrack audioTrack;
	// private Recorder recorder;
	RecorderThread recorderThread;
	PlayerThread playerThread;

	short[] recordedSamples;
	short[] generatedSamples;

	volatile Thread md5CalculatingThread;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		this.editTextSineHz = (EditText) findViewById(R.id.editTextSineHz);
		this.editTextSineSeconds = (EditText) findViewById(R.id.editTextSineSeconds);
		this.editTextRecordingDuration = (EditText) findViewById(R.id.editTextRecordingDuration);
		this.editTextMd5OfGeneratedSamples = (EditText) findViewById(R.id.editTextMd5OfGeneratedSamples);
		this.editTextMd5OfRecordedSamples = (EditText) findViewById(R.id.editTextMd5OfRecordedSamples);
		this.editTextPeakFrequency = (EditText) findViewById(R.id.editTextPeakFrequency);

		((Button) findViewById(R.id.buttonGenerateSine))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						try {
							generateSine();
						} catch (NoSuchAlgorithmException e) {
							e.printStackTrace();
						}
					}
				});

		((Button) findViewById(R.id.buttonRecord))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						final Button button = (Button) arg0;
						if (AcousticPositioningActivity.this.recorderThread != null) {
							AcousticPositioningActivity.this.recorderThread
									.stopRecording();
							AcousticPositioningActivity.this.recorderThread = null;
							button.setBackgroundColor(Color.YELLOW);
							button.setText("stopping");
							return;
						}
						int recording_duration = Integer
								.parseInt(AcousticPositioningActivity.this.editTextRecordingDuration
										.getText().toString());
						AcousticPositioningActivity.this.recorderThread = new RecorderThread(
								recording_duration, true);
						AcousticPositioningActivity.this.recorderThread
								.setRunAfterRecordingCallback(new Runnable() {
									public void run() {
										try {
											AcousticPositioningActivity.this.recordedSamples = AcousticPositioningActivity.this.recorderThread
													.getPreviousBuffer();
										} catch (NullPointerException e) {
											AcousticPositioningActivity.this.recordedSamples = null;
										}
										runOnUiThread(new Runnable() {
											public void run() {
												button.setText("record");
												button.setBackgroundColor(Color.GRAY);
											}
										});
									}// run
								});// setRunAfterRecordingCallback
						AcousticPositioningActivity.this.recorderThread.start();
						button.setBackgroundColor(Color.RED);
						button.setText("stop");
					}// onClick
				});// setOnClickListener

		((Button) findViewById(R.id.buttonSaveRecordedSamplesToCsv))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						Thread thread;
						try {
							thread = new CsvWriter(
									new Date(),
									AcousticPositioningActivity.this.recordedSamples,
									0,
									AcousticPositioningActivity.this.recordedSamples.length);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
							return;
						}
						thread.start();
					}// onCick
				});

		((Button) findViewById(R.id.buttonSaveRecordedSamplesToWav))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						Thread thread;
						try {
							thread = new WavWriterThread(
									AcousticPositioningActivity.this.recordedSamples,
									0,
									AcousticPositioningActivity.this.recordedSamples.length);
							thread.start();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}// onClick
				});

		((Button) findViewById(R.id.buttonPlayRecordedSamples))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						final Button button = (Button) arg0;
						if (AcousticPositioningActivity.this.playerThread != null) {
							AcousticPositioningActivity.this.playerThread
									.stopPlaying();
							AcousticPositioningActivity.this.playerThread = null;
							button.setBackgroundColor(Color.GRAY);
							button.setText("play");
							return;
						}
						if (AcousticPositioningActivity.this.recorderThread != null) {
							AcousticPositioningActivity.this.recordedSamples = AcousticPositioningActivity.this.recorderThread
									.getPreviousBuffer();
						}
						if (AcousticPositioningActivity.this.recordedSamples == null) {
							AcousticPositioningActivity.this.editTextMd5OfRecordedSamples
									.setText("no recorded samples");
							return;
						}
						AcousticPositioningActivity.this.playerThread = new PlayerThread(
								AcousticPositioningActivity.this.recordedSamples);
						AcousticPositioningActivity.this.playerThread.start();
						button.setBackgroundColor(Color.RED);
						button.setText("stop");
					}// onClick
				});

		((Button) findViewById(R.id.buttonPlayGeneratedSamples))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						Button button = (Button) arg0;
						if (AcousticPositioningActivity.this.playerThread != null) {
							AcousticPositioningActivity.this.playerThread
									.stopPlaying();
							AcousticPositioningActivity.this.playerThread = null;
							button.setBackgroundColor(Color.GRAY);
							button.setText("play");
							return;
						}
						if (AcousticPositioningActivity.this.generatedSamples == null) {
							AcousticPositioningActivity.this.editTextMd5OfGeneratedSamples
									.setText("no generated samples");
							return;
						}
						AcousticPositioningActivity.this.playerThread = new PlayerThread(
								AcousticPositioningActivity.this.generatedSamples);
						AcousticPositioningActivity.this.playerThread.start();
						button.setBackgroundColor(Color.RED);
						button.setText("stop");
					}// onClick
				});

		((Button) findViewById(R.id.buttonSaveGeneratedSamplesToCsv))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						try {
							(new CsvWriter(
									new Date(),
									AcousticPositioningActivity.this.generatedSamples,
									0,
									AcousticPositioningActivity.this.generatedSamples.length))
									.start();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}// onClick
				});

		((Button) findViewById(R.id.buttonSaveGeneratedSamplesToWav))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						try {
							(new WavWriterThread(
									AcousticPositioningActivity.this.generatedSamples,
									0,
									AcousticPositioningActivity.this.generatedSamples.length))
									.start();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}// onClick
				});

		((Button) findViewById(R.id.buttonFftGeneratedSamples))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						final FftThread fft_thread = new FftThread(
								AcousticPositioningActivity.this.generatedSamples);
						fft_thread.setCallback(new Runnable() {
							public void run() {
								runOnUiThread(new Runnable() {
									public void run() {
										AcousticPositioningActivity.this.editTextPeakFrequency.setText(""
												+ fft_thread.getPeakFrequency());
									}// run
								});
							}// run
						});
						fft_thread.start();
					}// onClick
				});

	}// onCreate

	// private void doRecord() throws FileNotFoundException {
	// if (recorder != null)
	// recorder.stopRecording();
	// recorder = new Recorder(new Runnable() {
	// public void run() {
	// recordedSamples = recorder.getRecordedFrames();
	// runOnUiThread(new Runnable() {
	// public void run() {
	// try {
	// RefreshRecordedSamplesMd5();
	// } catch (NoSuchAlgorithmException e) {
	// e.printStackTrace();
	// }// try
	// }// run
	// });
	// }// runOnUiThread
	// });// recorder
	// editTextMd5OfRecordedSamples.setText("recording ...");
	// try {
	// recorder.startRecording();
	// } catch (IllegalStateException e) {
	// editTextMd5OfRecordedSamples.setText(e.getMessage());
	// recorder.stopRecording();
	// }
	// }// doRecord

	void generateSine() throws NoSuchAlgorithmException {
		this.editTextMd5OfGeneratedSamples.setText("generating sine curve");
		int sine_hz;
		int sine_seconds;
		try {
			sine_hz = Integer
					.parseInt(this.editTextSineHz.getText().toString());
			sine_seconds = Integer.parseInt(this.editTextSineSeconds.getText()
					.toString());
		} catch (Exception e) {
			return;
		}// try

		if (this.audioTrack != null) {
			if (this.audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
				this.audioTrack.stop();
			}
			this.audioTrack.release();
		}// if
		SineSamples sine_samples = new SineSamples(sine_hz, sine_seconds);
		this.generatedSamples = sine_samples.getSamplesInShort();
		RefreshGeneratedSamplesMd5();
	}// PlaySine

	private void RefreshGeneratedSamplesMd5() throws NoSuchAlgorithmException {
		if (this.md5CalculatingThread != null
				&& this.md5CalculatingThread.isAlive()) {
			this.editTextMd5OfGeneratedSamples
					.setText("digest calculator is busy");
			return;
		}// if
		this.md5CalculatingThread = new Thread(new Runnable() {
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						AcousticPositioningActivity.this.editTextMd5OfGeneratedSamples
								.setText("calculating digest ...");
					}// run
				});// runOnUiThread
				try {
					final Md5 md5;
					md5 = new Md5();
					md5.putBigEndian(AcousticPositioningActivity.this.generatedSamples);
					runOnUiThread(new Runnable() {
						public void run() {
							AcousticPositioningActivity.this.editTextMd5OfGeneratedSamples
									.setText(md5.getMd5String());
						}// run
					});// runOnUiThread
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}// try
			}// run
		});// md5CalculationThread
		this.md5CalculatingThread.start();
	}// RefreshGeneratedSamplesMd5

	private void RefreshRecordedSamplesMd5() throws NoSuchAlgorithmException {
		if (this.md5CalculatingThread != null
				&& this.md5CalculatingThread.isAlive()) {
			this.editTextMd5OfGeneratedSamples
					.setText("digest calculator is busy");
			return;
		}// if
		this.md5CalculatingThread = new Thread(new Runnable() {
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						AcousticPositioningActivity.this.editTextMd5OfRecordedSamples
								.setText("calculating digest ...");
					}// run
				});// runOnUiThread
				try {
					final Md5 md5 = new Md5();
					md5.putBigEndian(AcousticPositioningActivity.this.recordedSamples);
					runOnUiThread(new Runnable() {
						public void run() {
							AcousticPositioningActivity.this.editTextMd5OfRecordedSamples
									.setText(md5.getMd5String());
						}// run
					});
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}// try
			}// run
		});// md5CalculationThread
		this.md5CalculatingThread.start();
	}// refreshRecordedSamplesMd5

	@Override
	public void onStop() {
		if (this.audioTrack != null) {
			this.audioTrack.stop();
			this.audioTrack.release();
		}
		if (this.recorderThread != null) {
			this.recorderThread.stopRecording();
		}
		super.onStop();
	}// onStop
}// AcousticPositioningActivity
