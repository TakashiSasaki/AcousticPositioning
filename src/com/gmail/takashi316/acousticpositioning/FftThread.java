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
		if (size % 2 == 1) {
			throw new IllegalArgumentException(
					"size of input array should be even number.");
		}
		startDate = new Date();
		fft = new DoubleFFT_1D(size);
		workspace = new double[size * 2];
		for (int i = 0; i < size; ++i) {
			workspace[i] = (double) input[i];
		}// for
	}// a constructor

	@Override
	public void run() {
		fft.realForward(workspace);
		endDate = new Date();
		if (callback != null) {
			callback.run();
		}
	}// run

	public long getConsumedTime() {
		return endDate.getTime() - startDate.getTime();
	}

	public double[] getResult() {
		return workspace;
	}

	public int getPeakIndex() {
		// workspace[0] is is real because the input is real
		int peak_index = 0;
		double peak_value = workspace[0] * workspace[0];
		if (workspace[1] * workspace[1] > peak_value) {
			peak_index = size / 2;
		}// if

		for (int i = 1; i < size; ++i) {
			final double real = workspace[i * 2];
			final double imaginary = workspace[i * 2 + 1];
			final double power = real * real + imaginary * imaginary;
			if (power > peak_value) {
				peak_index = i;
				peak_value = power;
			}// if
		}// for
		return peak_index;
	}// getPeakIndex

	public double getPeakFrequency() {
		return size / SAMPLING_RATE * getPeakIndex();
	}

	public void setCallback(Runnable callback) {
		this.callback = callback;
	}
}// FftThread
