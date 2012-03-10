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
	private EditText editTextRecordingDuration;
	private EditText editTextMd5OfGeneratedSamples;
	private EditText editTextMd5OfRecordedSamples;

	private AudioTrack audioTrack;
	// private Recorder recorder;
	private RecorderThread recorderThread;
	private PlayerThread playerThread;

	private short[] recordedSamples;
	private short[] generatedSamples;

	volatile Thread md5CalculatingThread;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		editTextSineHz = (EditText) findViewById(R.id.editTextSineHz);
		editTextSineSeconds = (EditText) findViewById(R.id.editTextSineSeconds);
		editTextRecordingDuration = (EditText) findViewById(R.id.editTextRecordingDuration);
		editTextMd5OfGeneratedSamples = (EditText) findViewById(R.id.editTextMd5OfGeneratedSamples);
		editTextMd5OfRecordedSamples = (EditText) findViewById(R.id.editTextMd5OfRecordedSamples);

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
						if (recorderThread != null) {
							recorderThread.stopRecording();
							recorderThread = null;
							button.setBackgroundColor(Color.YELLOW);
							button.setText("stopping");
							return;
						}
						int recording_duration = Integer
								.parseInt(editTextRecordingDuration.getText()
										.toString());
						recorderThread = new RecorderThread(recording_duration,
								true);
						recorderThread
								.setRunAfterRecordingCallback(new Runnable() {
									public void run() {
										try {
											recordedSamples = recorderThread
													.getPreviousBuffer();
										} catch (NullPointerException e) {
											recordedSamples = null;
										}
										runOnUiThread(new Runnable() {
											public void run() {
												button.setText("record");
												button.setBackgroundColor(Color.GRAY);
											}
										});
									}// run
								});// setRunAfterRecordingCallback
						recorderThread.start();
						button.setBackgroundColor(Color.RED);
						button.setText("stop");
					}// onClick
				});// setOnClickListener

		((Button) findViewById(R.id.buttonSaveRecordedSamplesToCsv))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						Thread thread;
						try {
							thread = new CsvWriter(new Date(), recordedSamples,
									0, recordedSamples.length);
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
							thread = new WavWriterThread(recordedSamples, 0,
									recordedSamples.length);
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
						if (playerThread != null) {
							playerThread.stopPlaying();
							playerThread = null;
							button.setBackgroundColor(Color.GRAY);
							button.setText("play");
							return;
						}
						if (recorderThread != null) {
							recordedSamples = recorderThread
									.getPreviousBuffer();
						}
						if (recordedSamples == null) {
							editTextMd5OfRecordedSamples
									.setText("no recorded samples");
							return;
						}
						playerThread = new PlayerThread(recordedSamples);
						playerThread.start();
						button.setBackgroundColor(Color.RED);
						button.setText("stop");
					}// onClick
				});

		((Button) findViewById(R.id.buttonPlayGeneratedSamples))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						Button button = (Button) arg0;
						if (playerThread != null) {
							playerThread.stopPlaying();
							playerThread = null;
							button.setBackgroundColor(Color.GRAY);
							button.setText("play");
							return;
						}
						if (generatedSamples == null) {
							editTextMd5OfGeneratedSamples
									.setText("no generated samples");
							return;
						}
						playerThread = new PlayerThread(generatedSamples);
						playerThread.start();
						button.setBackgroundColor(Color.RED);
						button.setText("stop");
					}// onClick
				});

		((Button) findViewById(R.id.buttonSaveGeneratedSamplesToCsv))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						try {
							(new CsvWriter(new Date(), generatedSamples, 0,
									generatedSamples.length)).start();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}// onClick
				});

		((Button) findViewById(R.id.buttonSaveGeneratedSamplesToWav))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						try {
							(new WavWriterThread(generatedSamples, 0,
									generatedSamples.length)).start();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
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

	private void generateSine() throws NoSuchAlgorithmException {
		editTextMd5OfGeneratedSamples.setText("generating sine curve");
		int sine_hz;
		int sine_seconds;
		try {
			sine_hz = Integer.parseInt(editTextSineHz.getText().toString());
			sine_seconds = Integer.parseInt(editTextSineSeconds.getText()
					.toString());
		} catch (Exception e) {
			return;
		}// try

		if (audioTrack != null) {
			if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
				audioTrack.stop();
			}
			audioTrack.release();
		}// if
		SineSamples sine_samples = new SineSamples(sine_hz, sine_seconds);
		generatedSamples = sine_samples.getSamplesInShort();
		RefreshGeneratedSamplesMd5();
	}// PlaySine

	private void RefreshGeneratedSamplesMd5() throws NoSuchAlgorithmException {
		if (md5CalculatingThread != null && md5CalculatingThread.isAlive()) {
			editTextMd5OfGeneratedSamples.setText("digest calculator is busy");
			return;
		}// if
		md5CalculatingThread = new Thread(new Runnable() {
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						editTextMd5OfGeneratedSamples
								.setText("calculating digest ...");
					}// run
				});// runOnUiThread
				try {
					final Md5 md5;
					md5 = new Md5();
					md5.putBigEndian(generatedSamples);
					runOnUiThread(new Runnable() {
						public void run() {
							editTextMd5OfGeneratedSamples.setText(md5
									.getMd5String());
						}// run
					});// runOnUiThread
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}// try
			}// run
		});// md5CalculationThread
		md5CalculatingThread.start();
	}// RefreshGeneratedSamplesMd5

	private void RefreshRecordedSamplesMd5() throws NoSuchAlgorithmException {
		if (md5CalculatingThread != null && md5CalculatingThread.isAlive()) {
			editTextMd5OfGeneratedSamples.setText("digest calculator is busy");
			return;
		}// if
		md5CalculatingThread = new Thread(new Runnable() {
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						editTextMd5OfRecordedSamples
								.setText("calculating digest ...");
					}// run
				});// runOnUiThread
				try {
					final Md5 md5 = new Md5();
					md5.putBigEndian(recordedSamples);
					runOnUiThread(new Runnable() {
						public void run() {
							editTextMd5OfRecordedSamples.setText(md5
									.getMd5String());
						}// run
					});
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}// try
			}// run
		});// md5CalculationThread
		md5CalculatingThread.start();
	}// refreshRecordedSamplesMd5

	@Override
	public void onStop() {
		if (audioTrack != null) {
			audioTrack.stop();
			audioTrack.release();
		}
		if (recorderThread != null) {
			recorderThread.stopRecording();
		}
		super.onStop();
	}// onStop
}// AcousticPositioningActivity
