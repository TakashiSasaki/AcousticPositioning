package com.gmail.takashi316.acousticpositioning;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class WhiteNoise {
	public static void main(String[] args) {
		short[] short_array = new short[10];
		double[] double_array = new double[short_array.length*2];
		for (int i = 0; i < short_array.length; ++i) {
			Log.v("index "+i);
			double_array[i * 2] = (double) short_array[i];
			double_array[i * 2 + 1] = 0.0d;
		}
		DoubleFFT_1D fft = new DoubleFFT_1D(short_array.length);
		fft.complexForward(double_array);
	}
}
