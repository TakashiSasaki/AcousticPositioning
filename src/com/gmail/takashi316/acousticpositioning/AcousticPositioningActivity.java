package com.gmail.takashi316.acousticpositioning;

import java.io.IOException;

import javax.sound.midi.Sequence;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AcousticPositioningActivity extends MenuActivity {
	private EditText editTextSeq1LowPeak;
	private EditText editTextSeq1HighPeak;
	private EditText editTextSeq2LowPeak;
	private EditText editTextSeq2HighPeak;

	private PlayerThread playerThread;
	private Sequences sequences = Sequences.getInstance();
	private RecorderThread recorderThread;
	private DetectingThread detectingThread;
	private EditText editTextPeakPower1Low;
	private EditText editTextPeakPower1High;
	private EditText editTextPeakPower2Low;
	private EditText editTextPeakPower2High;
	private EditText editTextAveragePower1low;
	private EditText editTextAveragePower1High;
	private EditText editTextAveragePower2Low;
	private EditText editTextAveragePower2High;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acousticpositioning);

		editTextSeq1LowPeak = (EditText) findViewById(R.id.editTextSeq1LowPeak);
		editTextSeq1HighPeak = (EditText) findViewById(R.id.editTextSeq1HighPeak);
		editTextSeq2LowPeak = (EditText) findViewById(R.id.editTextSeq2LowPeak);
		editTextSeq2HighPeak = (EditText) findViewById(R.id.editTextSeq2HighPeak);
		editTextPeakPower1Low = (EditText) findViewById(R.id.editTextPeakPower1Low);
		editTextPeakPower1High = (EditText) findViewById(R.id.editTextPeakPower1High);
		editTextPeakPower2Low = (EditText) findViewById(R.id.editTextPeakPower2Low);
		editTextPeakPower2High = (EditText) findViewById(R.id.editTextPeakPower2High);
		editTextAveragePower1low = (EditText) findViewById(R.id.editTextAveragePower1Low);
		editTextAveragePower1High = (EditText) findViewById(R.id.editTextAveragePower1High);
		editTextAveragePower2Low = (EditText) findViewById(R.id.editTextAveragePower2Low);
		editTextAveragePower2High = (EditText) findViewById(R.id.editTextAveragePower2High);

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
								sequences.seq1Low);
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
								Sequences.seq1High);
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
								Sequences.seq1HighLow);
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
								Sequences.seq2Low);
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
								Sequences.seq2High);
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
								Sequences.seq2HighLow);
						AcousticPositioningActivity.this.playerThread.start();
					}// onClick
				});

		((Button) findViewById(R.id.buttonDetect))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View arg0) {
						final Button button = (Button) arg0;
						if (recorderThread != null) {
							recorderThread.stopRecording();
							recorderThread = null;
							detectingThread.enabled = false;
							detectingThread = null;
							return;
						}
						try {
							recorderThread = new RecorderThread(1000, true);
						} catch (IllegalStateException e) {
							Log.v("Can't initialize RecorderThread");
							return;
						}
						recorderThread.setNextBufferSize(1024);
						recorderThread.start();
						try {
							detectingThread = new DetectingThread(
									recorderThread);
						} catch (NumberFormatException e) {
							e.printStackTrace();
							return;
						} catch (IOException e) {
							e.printStackTrace();
							return;
						}// try
						detectingThread.setCallback(new Runnable() {

							public void run() {
								if (detectingThread == null)
									return;
								runOnUiThread(new Runnable() {
									public void run() {
										if (detectingThread.peak1Low != null) {
											editTextSeq1LowPeak
													.setText(""
															+ detectingThread.peak1Low.index);
											editTextPeakPower1Low.setText(""
													+ detectingThread.peak1Low
															.getPower());
											editTextAveragePower1low
													.setText(""
															+ detectingThread.peakAverage1Low);
										}
										if (detectingThread.peak1High != null) {
											editTextSeq1HighPeak
													.setText(""
															+ detectingThread.peak1High.index);
											editTextPeakPower1High.setText(""
													+ detectingThread.peak1High
															.getPower());
											editTextAveragePower1High
													.setText(""
															+ detectingThread.peakAverage1High);
										}
										if (detectingThread.peak2Low != null) {
											editTextSeq2LowPeak
													.setText(""
															+ detectingThread.peak2Low.index);
											editTextPeakPower2Low.setText(""
													+ detectingThread.peak2Low
															.getPower());
											editTextAveragePower2Low
													.setText(""
															+ detectingThread.peakAverage2Low);
										}
										if (detectingThread.peak2High != null) {
											editTextSeq2HighPeak
													.setText(""
															+ detectingThread.peak2High.index);
											editTextPeakPower2High.setText(""
													+ detectingThread.peak2High
															.getPower());
											editTextAveragePower2High
													.setText(""
															+ detectingThread.peakAverage2High);
										}
									}// run
								});
							}// run
						});
						detectingThread.start();
					}// onClick
				});

	}// onCreate
}
