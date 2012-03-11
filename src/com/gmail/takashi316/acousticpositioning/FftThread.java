package com.gmail.takashi316.acousticpositioning;

import java.util.Date;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import static com.gmail.takashi316.acousticpositioning.MyAudioRecord.SAMPLING_RATE;

public class FftThread extends Thread {
	private Date startDate;
	private Date endDate;
	private DoubleFFT_1D fft;
	private double[] workspace;
	private int size;
	private Runnable callback;

	public FftThread(short[] input) {
		this.size = input.length;
		if (this.size % 2 == 1) {
			throw new IllegalArgumentException(
					"size of input array should be even number.");
		}
		this.startDate = new Date();
		this.fft = new DoubleFFT_1D(this.size);
		this.workspace = new double[this.size * 2];
		for (int i = 0; i < this.size; ++i) {
			this.workspace[i] = (double) input[i];
		}// for
	}// a constructor

	@Override
	public void run() {
		this.fft.realForward(this.workspace);
		this.endDate = new Date();
		if (this.callback != null) {
			this.callback.run();
		}
	}// run

	public long getConsumedTime() {
		return this.endDate.getTime() - this.startDate.getTime();
	}

	public double[] getResult() {
		return this.workspace;
	}

	public int getPeakIndex() {
		// workspace[0] is is real because the input is real
		int peak_index = 0;
		double peak_value = this.workspace[0] * this.workspace[0];
		if (this.workspace[1] * this.workspace[1] > peak_value) {
			peak_index = this.size / 2;
		}// if

		for (int i = 1; i < this.size; ++i) {
			final double real = this.workspace[i * 2];
			final double imaginary = this.workspace[i * 2 + 1];
			final double power = real * real + imaginary * imaginary;
			if (power > peak_value) {
				peak_index = i;
				peak_value = power;
			}// if
		}// for
		return peak_index;
	}// getPeakIndex

	public double getPeakFrequency(Double peak_power) {
		final int peak_index = getPeakIndex();
		if (peak_power != null) {
			peak_power = workspace[peak_index];
		}
		return this.size / SAMPLING_RATE * peak_index;
	}

	public void setCallback(Runnable callback) {
		this.callback = callback;
	}
}// FftThread
