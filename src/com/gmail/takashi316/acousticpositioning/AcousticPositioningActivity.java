package com.gmail.takashi316.acousticpositioning;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.provider.MediaStore.Audio;

public class AcousticPositioningActivity extends Activity {
	/** Called when the activity is first created. */
	private AudioTrack audioTrack;
	private int minBufferSize = AudioTrack.getMinBufferSize(44100,
			AudioFormat.CHANNEL_CONFIGURATION_MONO,
			AudioFormat.ENCODING_PCM_16BIT);
	private short[] buffer = new short[minBufferSize * 100];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, buffer.length,
				AudioTrack.MODE_STATIC);
		Log.v(new Throwable(), "bufferSize = " + buffer.length);

		SignAudio sign_audio = new SignAudio(44100, 2000);
		sign_audio.writeBuffer(buffer);
		audioTrack.write(this.buffer, 0, buffer.length);
		audioTrack.play();
	}
}
