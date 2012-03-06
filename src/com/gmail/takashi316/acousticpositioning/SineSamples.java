package com.gmail.takashi316.acousticpositioning;

import static com.gmail.takashi316.acousticpositioning.MyAudioTrack.SAMPLING_RATE;


public class SineSamples extends SamplesBase {

	private int frequency;
	private int seconds;

	public SineSamples(int frequency, int seconds) {
		this.frequency = frequency;
		this.seconds = seconds;
		shortBuffer = new short[SAMPLING_RATE * this.seconds];
		for (int i = 0; i < shortBuffer.length; ++i) {
			if (i > SAMPLING_RATE) {
				shortBuffer[i] = shortBuffer[i - SAMPLING_RATE];
			} else {
				double t = (double) i * (1.0d / (double) SAMPLING_RATE);
				double signal = Math.sin(2.0d * Math.PI
						* (double) this.frequency * t) * 32767.0d;
				short short_signal = (short) signal;
				shortBuffer[i] = short_signal;
			}// if
		}// for

		byteBuffer = new byte[SAMPLING_RATE * this.seconds];
		for (int i = 0; i < byteBuffer.length; ++i) {
			if (i > SAMPLING_RATE) {
				byteBuffer[i] = byteBuffer[i - SAMPLING_RATE];
			} else {
				double t = (double) i * (1.0d / (double) SAMPLING_RATE);
				double signal = Math.sin(2.0d * Math.PI
						* (double) this.frequency * t) * 127.0d + 128.0d;
				byte byte_signal = (byte) signal;
				byteBuffer[i] = byte_signal;
			}
		}// for
	}// a constructor

}// SineSamples
