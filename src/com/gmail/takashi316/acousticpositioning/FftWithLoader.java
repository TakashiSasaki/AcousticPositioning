package com.gmail.takashi316.acousticpositioning;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import com.gmail.takashi316.acousticpositioning.Fft.WorkspaceState;

public class FftWithLoader extends Fft {
	public void loadFftCoefficients(String file_name)
			throws NumberFormatException, IOException {
		// this method does not care that coefficients in the given file is
		// ordered reversed or not;
		Reader reader = new Reader(file_name);
		BufferedReader br = reader.getBufferedReader();

		ArrayList<ComplexNumber> excel_format = new ArrayList<ComplexNumber>();
		String line;
		while ((line = br.readLine()) != null) {
			ComplexNumber cn = new ComplexNumber(line);
			excel_format.add(cn);
		}// while
		if (excel_format.size() != this.FFT_SIZE) {
			throw new RuntimeException(file_name + " doesn't have "
					+ this.FFT_SIZE + " double values.");
		}// if
		if (excel_format.get(0).imaginary != 0.0d) {
			throw new RuntimeException("0th value in " + file_name
					+ " is not real.");
		}// if
		if (excel_format.get(this.FFT_SIZE_HALF).imaginary != 0.0d) {
			throw new RuntimeException("" + this.FFT_SIZE_HALF + "th value in "
					+ file_name + " is not real.");
		}

		for (int i = 0; i < this.FFT_SIZE; ++i) {
			this.fftCoefficients[i * 2] = excel_format.get(i).real;
			this.fftCoefficients[i * 2 + 1] = excel_format.get(i).imaginary;
		}// for
	}// loadFftCoefficients

	public void loadSamples(String file_name) throws IOException {
		Reader reader = new Reader(file_name);
		BufferedReader br = reader.getBufferedReader();
		String line;
		int i = 0;
		while ((line = br.readLine()) != null) {
			this.workspace[i] = Double.parseDouble(line);
		}// while
		if (i != this.FFT_SIZE) {
			throw new RuntimeException(file_name + " doesn't have "
					+ this.FFT_SIZE + " of double values. ");
		}// if
		this.workspaceState = WorkspaceState.WORKSPACE_STATE_TIME_DOMAIN;
	}
}
