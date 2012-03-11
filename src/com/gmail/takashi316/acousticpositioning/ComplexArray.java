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

	public short[] getRealShortArrayNormalized() {
		short[] short_array = new short[this.size()];
		final double peak_power = get(getPeak()).getPower();
		final double denominator = Math.sqrt(peak_power);
		for (int i = 0; i < this.size(); ++i) {
			final double normalized_double = this.get(i).real / denominator
					* Short.MAX_VALUE;
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
}
