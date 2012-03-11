package com.gmail.takashi316.acousticpositioning;

import java.io.IOException;

import javax.sound.midi.Sequence;

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

	PlayerThread playerThread;
	Sequences sequences = Sequences.getInstance();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acousticpositioning);

		editTextSeq1LowPeak = (EditText) findViewById(R.id.editTextSeq1LowPeak);
		editTextSeq1HighPeak = (EditText) findViewById(R.id.editTextSeq1HighPeak);
		editTextSeq2LowPeak = (EditText) findViewById(R.id.editTextSeq2LowPeak);
		editTextSeq2HighPeak = (EditText) findViewById(R.id.editTextSeq2HighPeak);

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
								Sequences.seq1HighLow);
						AcousticPositioningActivity.this.playerThread.start();
					}// onClick
				});

	}// onCreate
}
