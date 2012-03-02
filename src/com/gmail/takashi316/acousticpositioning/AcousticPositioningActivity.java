package com.gmail.takashi316.acousticpositioning;

import android.app.Activity;
import android.media.AudioTrack;
import android.os.Bundle;

public class AcousticPositioningActivity extends Activity {
	/** Called when the activity is first created. */
	AudioTrack audioTrack;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		audioTrack = new SignAudio(2000,60);
		audioTrack.play();
	}
}
