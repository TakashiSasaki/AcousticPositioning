package com.gmail.takashi316.acousticpositioning;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

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
	private Recorder recorder;

	private short[] recordedSamples;
	private short[] generatedSamples;
	private Date recordedDate;
	private Date generatedDate;

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
						try {
							doRecord();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}// onClick
				});

		((Button) findViewById(R.id.buttonSaveRecordedSamplesToCsv))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						Thread thread = new Thread(new Runnable() {
							public void run() {
								try {
									saveRecordedSamplesToCsv();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}// run
						});
						thread.start();
					}// onCick
				});

		((Button) findViewById(R.id.buttonSaveRecordedSamplesToWav))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						Thread thread = new Thread(new Runnable() {
							public void run() {
								try {
									saveRecordedSamplesToWav();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
						thread.start();
					}// onClick
				});

		((Button) findViewById(R.id.buttonPlayRecordedSamples))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						try {
							PlayRecordedSamples();
						} catch (NullPointerException e) {
						}// try
					}// onClick
				});

		((Button) findViewById(R.id.buttonPlayGeneratedSamples))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						try {
							PlayGeneratedSamples();
						} catch (NullPointerException e) {
						}// try
					}// onClick
				});

		((Button) findViewById(R.id.buttonSaveGeneratedSamplesToCsv))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						Thread thread = new Thread(new Runnable() {
							public void run() {
								try {
									saveGeneratedSamplesToCsv();
								} catch (IOException e) {
									e.printStackTrace();
								}// try
							}// run
						});
						thread.start();
					}// onClick
				});

		((Button) findViewById(R.id.buttonSaveGeneratedSamplesToWav))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						Thread thread = new Thread(new Runnable() {
							public void run() {
								try {
									saveGeneratedSamplesToWav();
								} catch (IOException e) {
									e.printStackTrace();
								}// try
							}// run
						});
						thread.start();
					}// onClick
				});

	}// onCreate

	private void doRecord() throws FileNotFoundException {
		editTextMd5OfRecordedSamples.setText("recording ...");
		recordedDate = new Date();
		recorder = new Recorder(new Runnable() {
			public void run() {
				recordedSamples = recorder.getRecordedFrames();
				runOnUiThread(new Runnable() {
					public void run() {
						try {
							RefreshRecordedSamplesMd5();
						} catch (NoSuchAlgorithmException e) {
							e.printStackTrace();
						}// try
					}// run
				});
			}// run
		});
		recorder.startRecording();
	}// doRecord

	private void saveRecordedSamplesToCsv() throws IOException {
		Writer writer = new Writer(recordedDate);
		writer.writeToCsv(recordedSamples);
	}

	private void saveRecordedSamplesToWav() throws IOException {
		Writer writer = new Writer(recordedDate);
		writer.writeToWav(recordedSamples);
	}

	private void saveGeneratedSamplesToCsv() throws IOException {
		Writer writer = new Writer(generatedDate);
		writer.writeToCsv(generatedSamples);
	}

	private void saveGeneratedSamplesToWav() throws IOException {
		Writer writer = new Writer(generatedDate);
		writer.writeToWav(generatedSamples);
	}

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
		generatedDate = new Date();
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
		});// thread
		md5CalculatingThread.start();
	}// RefreshGeneratedSamplesMd5

	private void RefreshRecordedSamplesMd5() throws NoSuchAlgorithmException {
		Md5 md5 = new Md5();
		md5.putBigEndian(recordedSamples);
		editTextMd5OfRecordedSamples.setText(md5.getMd5String());
	}

	private void PlayGeneratedSamples() throws NullPointerException {
		if (audioTrack != null) {
			if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
				audioTrack.stop();
			}
			audioTrack.release();
		}
		if (generatedSamples == null) {
			throw new NullPointerException("generatedSamples is null");
		}
		audioTrack = new MyAudioTrack(generatedSamples);
		audioTrack.play();
	}// PlayBack

	private void PlayRecordedSamples() throws NullPointerException {
		if (audioTrack != null) {
			if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
				audioTrack.stop();
			}
			audioTrack.release();
		}
		if (recordedSamples == null) {
			throw new NullPointerException("recordedSamples is null");
		}
		audioTrack = new MyAudioTrack(recordedSamples);
		audioTrack.play();
	}// PlayBack

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
}// AcousticPositioningActivity
