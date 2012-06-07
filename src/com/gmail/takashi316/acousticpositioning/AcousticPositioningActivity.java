package com.gmail.takashi316.acousticpositioning;

import java.io.IOException;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AcousticPositioningActivity extends MenuActivity {
	EditText editTextSeq1LowPeak;
	EditText editTextSeq1HighPeak;
	EditText editTextSeq2LowPeak;
	EditText editTextSeq2HighPeak;
	EditText editTextPeakPower1Low;
	EditText editTextPeakPower1High;
	EditText editTextPeakPower2Low;
	EditText editTextPeakPower2High;
	EditText editTextAveragePower1low;
	EditText editTextAveragePower1High;
	EditText editTextAveragePower2Low;
	EditText editTextAveragePower2High;
	EditText editTextPeakFrequency;

	PlayerThread playerThread;
	RecorderThread recorderThread;
	DetectingThread detectingThread;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acousticpositioning);

		this.editTextSeq1LowPeak = (EditText) findViewById(R.id.editTextSeq1LowPeak);
		this.editTextSeq1HighPeak = (EditText) findViewById(R.id.editTextSeq1HighPeak);
		this.editTextSeq2LowPeak = (EditText) findViewById(R.id.editTextSeq2LowPeak);
		this.editTextSeq2HighPeak = (EditText) findViewById(R.id.editTextSeq2HighPeak);
		this.editTextPeakPower1Low = (EditText) findViewById(R.id.editTextPeakPower1Low);
		this.editTextPeakPower1High = (EditText) findViewById(R.id.editTextPeakPower1High);
		this.editTextPeakPower2Low = (EditText) findViewById(R.id.editTextPeakPower2Low);
		this.editTextPeakPower2High = (EditText) findViewById(R.id.editTextPeakPower2High);
		this.editTextAveragePower1low = (EditText) findViewById(R.id.editTextAveragePower1Low);
		this.editTextAveragePower1High = (EditText) findViewById(R.id.editTextAveragePower1High);
		this.editTextAveragePower2Low = (EditText) findViewById(R.id.editTextAveragePower2Low);
		this.editTextAveragePower2High = (EditText) findViewById(R.id.editTextAveragePower2High);
		this.editTextPeakFrequency = (EditText) findViewById(R.id.editTextPeakFrequency);

		((Button) findViewById(R.id.buttonSeq1Low))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View arg0) {
						if (AcousticPositioningActivity.this.playerThread != null) {
							AcousticPositioningActivity.this.playerThread
									.stopPlaying();
							AcousticPositioningActivity.this.playerThread = null;
							return;
						}// if
						AcousticPositioningActivity.this.playerThread = new PlayerThread(
								Sequences.getInstance().getSeq1Low());
						AcousticPositioningActivity.this.playerThread.start();
					}// onClick
				});

		((Button) findViewById(R.id.buttonSeq1High))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View arg0) {
						if (AcousticPositioningActivity.this.playerThread != null) {
							AcousticPositioningActivity.this.playerThread
									.stopPlaying();
							AcousticPositioningActivity.this.playerThread = null;
							return;
						}// if
						AcousticPositioningActivity.this.playerThread = new PlayerThread(
								Sequences.getInstance().getSeq1High());
						AcousticPositioningActivity.this.playerThread.start();
					}// onClick
				});

		((Button) findViewById(R.id.buttonSeq1LowHigh))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View arg0) {
						if (AcousticPositioningActivity.this.playerThread != null) {
							AcousticPositioningActivity.this.playerThread
									.stopPlaying();
							AcousticPositioningActivity.this.playerThread = null;
							return;
						}// if
						AcousticPositioningActivity.this.playerThread = new PlayerThread(
								Sequences.getInstance().getSeq1HighLow());
						AcousticPositioningActivity.this.playerThread.start();
					}// onClick
				});

		((Button) findViewById(R.id.buttonSeq2Low))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View arg0) {
						if (AcousticPositioningActivity.this.playerThread != null) {
							AcousticPositioningActivity.this.playerThread
									.stopPlaying();
							AcousticPositioningActivity.this.playerThread = null;
							return;
						}// if
						AcousticPositioningActivity.this.playerThread = new PlayerThread(
								Sequences.getInstance().getSeq2Low());
						AcousticPositioningActivity.this.playerThread.start();
					}// onClick
				});

		((Button) findViewById(R.id.buttonSeq2High))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View arg0) {
						if (AcousticPositioningActivity.this.playerThread != null) {
							AcousticPositioningActivity.this.playerThread
									.stopPlaying();
							AcousticPositioningActivity.this.playerThread = null;
							return;
						}// if
						AcousticPositioningActivity.this.playerThread = new PlayerThread(
								Sequences.getInstance().getSeq2High());
						AcousticPositioningActivity.this.playerThread.start();
					}// onClick
				});

		((Button) findViewById(R.id.buttonSeq2LowHigh))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View arg0) {
						if (AcousticPositioningActivity.this.playerThread != null) {
							AcousticPositioningActivity.this.playerThread
									.stopPlaying();
							AcousticPositioningActivity.this.playerThread = null;
							return;
						}// if
						AcousticPositioningActivity.this.playerThread = new PlayerThread(
								Sequences.getInstance().getSeq2HighLow());
						AcousticPositioningActivity.this.playerThread.start();
					}// onClick
				});

		((Button) findViewById(R.id.buttonSine1234))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						if (AcousticPositioningActivity.this.playerThread != null) {
							AcousticPositioningActivity.this.playerThread
									.stopPlaying();
							AcousticPositioningActivity.this.playerThread = null;
							return;
						}// if
						SineSamples sine_samples = new SineSamples(1234, 10);
						AcousticPositioningActivity.this.playerThread = new PlayerThread(
								sine_samples.getInShort());
						AcousticPositioningActivity.this.playerThread.start();
					}// onClick
				});

		((Button) findViewById(R.id.buttonDetect))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View arg0) {
						if (AcousticPositioningActivity.this.recorderThread != null) {
							AcousticPositioningActivity.this.recorderThread
									.stopRecording();
							AcousticPositioningActivity.this.recorderThread = null;
							AcousticPositioningActivity.this.detectingThread.enabled = false;
							AcousticPositioningActivity.this.detectingThread = null;
							return;
						}
						if (detectingThread != null) {
							AcousticPositioningActivity.this.detectingThread.enabled = false;
							AcousticPositioningActivity.this.detectingThread = null;
							return;
						}
						try {
							AcousticPositioningActivity.this.recorderThread = new RecorderThread(
									1000, true);
						} catch (IllegalStateException e) {
							Log.v("Can't initialize RecorderThread");
							return;
						}
						AcousticPositioningActivity.this.recorderThread
								.setNextBufferSize(1024);
						AcousticPositioningActivity.this.recorderThread.start();
						try {
							AcousticPositioningActivity.this.detectingThread = new DetectingThread(
									AcousticPositioningActivity.this.recorderThread);
						} catch (NumberFormatException e) {
							e.printStackTrace();
							return;
						} catch (IOException e) {
							e.printStackTrace();
							return;
						}// try
						AcousticPositioningActivity.this.detectingThread
								.setCallback(new Runnable() {

									public void run() {
										if (AcousticPositioningActivity.this.detectingThread == null)
											return;
										runOnUiThread(new Runnable() {
											public void run() {
												if (AcousticPositioningActivity.this.detectingThread.peakFrequency != null) {
													int peak_index = AcousticPositioningActivity.this.detectingThread.peakFrequency.index;
													if (peak_index >= 512)
														peak_index = 1024 - peak_index;
													AcousticPositioningActivity.this.editTextPeakFrequency
															.setText(""
																	+ (48000d / 1024d * peak_index));
												}
												if (AcousticPositioningActivity.this.detectingThread.peak1Low != null) {
													AcousticPositioningActivity.this.editTextSeq1LowPeak
															.setText(""
																	+ AcousticPositioningActivity.this.detectingThread.peak1Low.index);
													AcousticPositioningActivity.this.editTextPeakPower1Low.setText(""
															+ AcousticPositioningActivity.this.detectingThread.peak1Low
																	.getPower());
													AcousticPositioningActivity.this.editTextAveragePower1low
															.setText(""
																	+ AcousticPositioningActivity.this.detectingThread.peakAverage1Low);
												}
												if (AcousticPositioningActivity.this.detectingThread.peak1High != null) {
													AcousticPositioningActivity.this.editTextSeq1HighPeak
															.setText(""
																	+ AcousticPositioningActivity.this.detectingThread.peak1High.index);
													AcousticPositioningActivity.this.editTextPeakPower1High.setText(""
															+ AcousticPositioningActivity.this.detectingThread.peak1High
																	.getPower());
													AcousticPositioningActivity.this.editTextAveragePower1High
															.setText(""
																	+ AcousticPositioningActivity.this.detectingThread.peakAverage1High);
												}
												if (AcousticPositioningActivity.this.detectingThread.peak2Low != null) {
													AcousticPositioningActivity.this.editTextSeq2LowPeak
															.setText(""
																	+ AcousticPositioningActivity.this.detectingThread.peak2Low.index);
													AcousticPositioningActivity.this.editTextPeakPower2Low.setText(""
															+ AcousticPositioningActivity.this.detectingThread.peak2Low
																	.getPower());
													AcousticPositioningActivity.this.editTextAveragePower2Low
															.setText(""
																	+ AcousticPositioningActivity.this.detectingThread.peakAverage2Low);
												}
												if (AcousticPositioningActivity.this.detectingThread.peak2High != null) {
													AcousticPositioningActivity.this.editTextSeq2HighPeak
															.setText(""
																	+ AcousticPositioningActivity.this.detectingThread.peak2High.index);
													AcousticPositioningActivity.this.editTextPeakPower2High.setText(""
															+ AcousticPositioningActivity.this.detectingThread.peak2High
																	.getPower());
													AcousticPositioningActivity.this.editTextAveragePower2High
															.setText(""
																	+ AcousticPositioningActivity.this.detectingThread.peakAverage2High);
												}
											}// run
										});
									}// run
								});
						AcousticPositioningActivity.this.detectingThread
								.start();
					}// onClick
				});

	}// onCreate
}
