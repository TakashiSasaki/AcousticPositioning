package com.gmail.takashi316.acousticpositioning;

import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings("serial")
public class ComplexArray extends ArrayList<ComplexNumber> {
	public ComplexArray(int fft_SIZE) {
		super(fft_SIZE);
	}

	public ComplexArray() {
		super();
	}

	public void sortDesc() {
		Collections.sort(this, new ComplexNumber.ComplexNumberComparator(true));
	}

	public void sortAsc() {
		Collections
				.sort(this, new ComplexNumber.ComplexNumberComparator(false));
	}

	public int getPeak() {
		double peak_power = 0.0d;
		int peak_index = 0;
		for (int i = 0; i < this.size(); ++i) {
			final double power = this.get(i).getPower();
			if (power > peak_power) {
				peak_power = power;
				peak_index = i;
			}// if
		}// for
		return peak_index;
	}// getPeak

	public short[] getRealShortArray(short max) {
		short[] short_array = new short[this.size()];
		final double peak_power = get(getPeak()).getPower();
		final double denominator = Math.sqrt(peak_power);
		for (int i = 0; i < this.size(); ++i) {
			final double normalized_double = this.get(i).real / denominator
					* max;
			short_array[i] = (short) normalized_double;
		}
		return short_array;
	}// getRealShortArrayNormalized

	public void normalize() {
		final double denominator = Math.sqrt(get(getPeak()).getPower());
		for (int i = 0; i < this.size(); ++i) {
			this.get(i).div(denominator);
		}// for
	}// normalize

	public double getAveragePower() {
		double sum_power = 0.0;
		// double sum_real = 0;
		// double sum_imaginary = 0;
		for (int i = 0; i < this.size(); ++i) {
			sum_power += this.get(i).getPower();
		}// for
		return sum_power;
	}// getAverage
}
