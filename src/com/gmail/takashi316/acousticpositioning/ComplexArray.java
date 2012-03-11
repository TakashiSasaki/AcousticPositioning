package com.gmail.takashi316.acousticpositioning;

import java.util.ArrayList;
import java.util.Collections;

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
}
