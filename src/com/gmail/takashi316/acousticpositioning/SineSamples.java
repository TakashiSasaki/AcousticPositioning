package com.gmail.takashi316.acousticpositioning;

public class SineSamples extends SamplesBase {

	private int frequency;
	private int seconds;

	public SineSamples(int frequency, int seconds) {
		this.frequency = frequency;
		this.seconds = seconds;
		this.shortBuffer = new short[PlayerThread.SAMPLING_RATE * this.seconds];
		for (int i = 0; i < this.shortBuffer.length; ++i) {
			if (i > PlayerThread.SAMPLING_RATE) {
				this.shortBuffer[i] = this.shortBuffer[i - PlayerThread.SAMPLING_RATE];
			} else {
				double t = (double) i
						* (1.0d / (double) PlayerThread.SAMPLING_RATE);
				double signal = Math.sin(2.0d * Math.PI
						* (double) this.frequency * t) * 32767.0d;
				short short_signal = (short) signal;
				this.shortBuffer[i] = short_signal;
			}// if
		}// for

		this.byteBuffer = new byte[PlayerThread.SAMPLING_RATE * this.seconds];
		for (int i = 0; i < this.byteBuffer.length; ++i) {
			if (i > PlayerThread.SAMPLING_RATE) {
				this.byteBuffer[i] = this.byteBuffer[i - PlayerThread.SAMPLING_RATE];
			} else {
				double t = (double) i
						* (1.0d / (double) PlayerThread.SAMPLING_RATE);
				double signal = Math.sin(2.0d * Math.PI
						* (double) this.frequency * t) * 127.0d + 128.0d;
				byte byte_signal = (byte) signal;
				this.byteBuffer[i] = byte_signal;
			}
		}// for
	}// a constructor

}// SineSamples
