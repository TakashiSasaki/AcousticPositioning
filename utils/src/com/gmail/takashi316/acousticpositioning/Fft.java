package com.gmail.takashi316.acousticpositioning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class Fft {
	private int FFT_SIZE;
	private int FFT_SIZE_HALF;

	static public enum WorkspaceState {
		WORKSPACE_STATE_IS_UNKNOWN, WORKSPACE_STATE_FREQUENCY_DOMAIN, WORKSPACE_STATE_TIME_DOMAIN
	}// WorkspaceState

	private WorkspaceState workspaceState = WorkspaceState.WORKSPACE_STATE_IS_UNKNOWN;
	private DoubleFFT_1D fft;
	private double[] workspace;
	private double[] fftCoefficients;

	public Fft() {
		this.FFT_SIZE = 1024;
		this.FFT_SIZE_HALF = 512;
		this.fft = new DoubleFFT_1D(this.FFT_SIZE);
		this.workspace = new double[this.FFT_SIZE * 2];
		this.fftCoefficients = new double[this.FFT_SIZE * 2];
	}// a constructor

	public Fft(int size) {
		this.FFT_SIZE = size;
		this.FFT_SIZE_HALF = size / 2;
		this.fft = new DoubleFFT_1D(this.FFT_SIZE);
		this.workspace = new double[this.FFT_SIZE * 2];
		this.fftCoefficients = new double[this.FFT_SIZE * 2];
	}// a constructor

	public void doIfft() {
		this.fft.complexInverse(this.workspace, true);
		this.workspaceState = WorkspaceState.WORKSPACE_STATE_TIME_DOMAIN;
	}// doIfft

	public void doFft() {
		this.fft.complexForward(this.workspace);
		this.workspaceState = WorkspaceState.WORKSPACE_STATE_FREQUENCY_DOMAIN;
	}// doFft

	public void multiply() {
		if (this.workspaceState != WorkspaceState.WORKSPACE_STATE_FREQUENCY_DOMAIN) {
			throw new RuntimeException(
					"workspace state is not frequency domain.");
		}
		for (int i = 0; i < this.FFT_SIZE; ++i) {
			final double real1 = this.workspace[i * 2];
			final double imaginary1 = this.workspace[i * 2 + 1];
			final double real2 = this.fftCoefficients[i * 2];
			final double imaginary2 = this.fftCoefficients[i * 2 + 1];
			final double real3 = real1 * real2 - imaginary1 * imaginary2;
			final double imaginary3 = real1 * imaginary2 + real2 * imaginary1;
			this.workspace[i * 2] = real3;
			this.workspace[i * 2 + 1] = imaginary3;
		}// for
	}// multiply

	public void loadWorkspace(String lines[]) throws IOException {
		int i = 0;
		for (String line : lines) {
			this.workspace[i * 2 + 1] = 0.0d;
			this.workspace[i * 2] = Double.parseDouble(line);
			++i;
		}// while
		if (i != this.FFT_SIZE) {
			throw new RuntimeException("given string array doesn't have "
					+ this.FFT_SIZE + " of double values. ");
		}// if
		this.workspaceState = WorkspaceState.WORKSPACE_STATE_TIME_DOMAIN;
	}// loadSamples

	public void loadWorkspace(short[] short_array, int offset) {
		ComplexArray ca = new ComplexArray(this.FFT_SIZE);
		for (int i = 0; i < this.FFT_SIZE; ++i) {
			ComplexNumber cn = new ComplexNumber(short_array[i + offset], 0);
			ca.add(cn);
		}// for
		ca.normalize();
		for (int i = 0; i < this.FFT_SIZE; ++i) {
			this.workspace[i * 2] = ca.get(i).real;
			this.workspace[i * 2 + 1] = ca.get(i).imaginary;
		}// for
	}// loadWorkSpace

	public void loadWorkspace(File file) throws IOException {
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line;
		int i = 0;
		while ((line = br.readLine()) != null) {
			this.workspace[i] = Double.parseDouble(line);
		}// while
		if (i != this.FFT_SIZE) {
			throw new RuntimeException(file.getName() + " doesn't have "
					+ this.FFT_SIZE + " of double values. ");
		}// if
		this.workspaceState = WorkspaceState.WORKSPACE_STATE_TIME_DOMAIN;
	}// loadSamples

	public ComplexArray getWorkspace() {
		ComplexArray a = new ComplexArray(this.FFT_SIZE);
		for (int i = 0; i < this.FFT_SIZE; ++i) {
			final ComplexNumber c = new ComplexNumber(this.workspace[i * 2],
					this.workspace[i * 2 + 1]);
			c.index = i;
			a.add(c);
		}// for
		return a;
	}// getWorkspace

	public ComplexNumber getPeak() {
		ComplexArray ca = this.getWorkspace();
		double positive1_value = 0.0d;
		int positive1_index = 0;
		double positive2_value = 0.0d;
		int positive2_index = FFT_SIZE - 1;
		double negative1_value = 0.0d;
		int negative1_index = 0;
		double negative2_value = 0.0d;
		int negative2_index = FFT_SIZE - 1;
		for (ComplexNumber cn : ca) {
			if (0.0 > cn.real) {
				if (negative1_value > cn.real) {
					negative1_value = cn.real;
					negative1_index = cn.index;
				} else if (negative2_value > cn.real) {
					negative2_value = cn.real;
					negative2_index = cn.index;
				}// if
				assert (negative1_value <= negative2_value);
				assert (negative1_index != negative2_index);
			}// if
			if (0.0 < cn.real) {
				if (positive1_value < cn.real) {
					positive1_value = cn.real;
					positive1_index = cn.index;
				} else if (positive2_value < cn.real) {
					positive2_value = cn.real;
					positive2_index = cn.index;
				}// if
				assert (positive1_value >= positive2_index);
				assert (positive1_index != positive2_index);
			}// if
		}// for
		if (positive1_index - positive2_index > FFT_SIZE_HALF) {
			positive2_index += FFT_SIZE;
		}// if
		if (positive2_index - positive1_index > FFT_SIZE_HALF) {
			positive1_index += FFT_SIZE;
		}// if
		if (positive1_index > positive2_index) {
			final int tmp = positive1_index;
			positive2_index = positive1_index;
			positive1_index = tmp;
		}// if
		if (positive1_index - positive2_index > FFT_SIZE_HALF) {
			positive2_index += FFT_SIZE;
		}// if
		if (negative2_index - negative1_index > FFT_SIZE_HALF) {
			negative1_index += FFT_SIZE;
		}// if
		if (negative1_index > negative2_index) {
			final int tmp = negative1_index;
			negative2_index = negative1_index;
			negative1_index = tmp;
		}// if

		assert (positive1_index < positive2_index);
		assert (negative1_index < negative2_index);
		if (positive1_index > negative2_index
				|| negative1_index > positive2_index) {
			// no overlap
			return ca.get(ca.getPeak());
		}
		if (negative1_index <= positive1_index
				&& positive1_index <= negative2_index
				&& negative2_index <= positive2_index) {
			assert (negative2_value <= 0.0);
			if ((-negative2_value) > positive1_value) {
				return ca.get(negative2_index % FFT_SIZE);
			} else {
				return ca.get(positive1_index % FFT_SIZE);
			}// if
		}// if
		if (positive1_index <= negative1_index
				&& negative1_index <= positive2_index
				&& positive2_index <= negative2_index) {
			assert (negative1_value <= 0.0);
			if ((-negative1_value) > positive2_value) {
				return ca.get(negative1_index % FFT_SIZE);
			} else {
				return ca.get(positive2_index % FFT_SIZE);
			}
		}
		if (negative1_index <= positive1_index
				&& positive2_index <= negative2_index) {
			// negative section is included in positive section
			if (positive1_value >= positive2_value) {
				return ca.get(positive1_index % FFT_SIZE);
			} else {
				return ca.get(positive2_index % FFT_SIZE);
			}// if
		}// if
		if (positive1_index <= negative1_index
				&& negative2_index <= positive2_index) {
			if ((-negative1_value) >= (-negative2_index)) {
				return ca.get(negative1_index % FFT_SIZE);
			} else {
				return ca.get(negative2_index % FFT_SIZE);
			}// if
		}// if
			// last resort (never be reached)
		assert (false);
		return ca.get(ca.getPeak());
	}// getPeak

	public void loadFftCoefficients(String[] lines)
			throws NumberFormatException, IOException {
		// this method does not care that coefficients in the given file is
		// ordered reversed or not;

		ComplexArray excel_format = new ComplexArray();
		for (String line : lines) {
			ComplexNumber cn = new ComplexNumber(line);
			excel_format.add(cn);
		}// while
		if (excel_format.size() != this.FFT_SIZE) {
			throw new RuntimeException("given string array doesn't have "
					+ this.FFT_SIZE + " double values.");
		}// if
		if (excel_format.get(0).imaginary != 0.0d) {
			throw new RuntimeException("0th value is not real.");
		}// if
		if (excel_format.get(this.FFT_SIZE_HALF).imaginary != 0.0d) {
			throw new RuntimeException("" + this.FFT_SIZE_HALF
					+ "th value in is not real.");
		}// if

		for (int i = 0; i < this.FFT_SIZE; ++i) {
			this.fftCoefficients[i * 2] = excel_format.get(i).real;
			this.fftCoefficients[i * 2 + 1] = excel_format.get(i).imaginary;
		}// for
	}// loadFftCoefficients

	public void loadFftCoefficients(File file) throws NumberFormatException,
			IOException {
		// this method does not care that coefficients in the given file is
		// ordered reversed or not;

		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);

		ArrayList<ComplexNumber> excel_format = new ArrayList<ComplexNumber>();
		String line;
		while ((line = br.readLine()) != null) {
			ComplexNumber cn = new ComplexNumber(line);
			excel_format.add(cn);
		}// while
		if (excel_format.size() != this.FFT_SIZE) {
			throw new RuntimeException(file.getName() + " doesn't have "
					+ this.FFT_SIZE + " double values.");
		}// if
		if (excel_format.get(0).imaginary != 0.0d) {
			throw new RuntimeException("0th value in " + file.getName()
					+ " is not real.");
		}// if
		if (excel_format.get(this.FFT_SIZE_HALF).imaginary != 0.0d) {
			throw new RuntimeException("" + this.FFT_SIZE_HALF + "th value in "
					+ file.getName() + " is not real.");
		}// if

		for (int i = 0; i < this.FFT_SIZE; ++i) {
			this.fftCoefficients[i * 2] = excel_format.get(i).real;
			this.fftCoefficients[i * 2 + 1] = excel_format.get(i).imaginary;
		}// for
	}// loadFftCoefficients

	public void printFftCoefficients() {
		for (int i = 0; i < this.FFT_SIZE; ++i) {
			ComplexNumber cn = new ComplexNumber(this.fftCoefficients[i * 2],
					this.fftCoefficients[i * 2 + 1]);
			if (cn.imaginary > 100)
				System.out.println("" + i + "=" + cn);
		}// for
	}// printFftCoefficients

	public void printFftCoefficientsSum() {
		double real_sum = 0;
		double imaginary_sum = 0;
		for (int i = 0; i < this.FFT_SIZE; ++i) {
			real_sum += this.fftCoefficients[i * 2];
			imaginary_sum += this.fftCoefficients[i * 2 + 1];
		}// for
		ComplexNumber cn = new ComplexNumber(real_sum, imaginary_sum);
		System.out.println(cn);
	}// printFftCoefficientsSum

	public void printWorkspace() {
		for (int i = 0; i < this.FFT_SIZE; ++i) {
			final double real = this.workspace[i * 2];
			final double imaginary = this.workspace[i * 2 + 1];
			ComplexNumber c = new ComplexNumber(real, imaginary);
			System.out.println("" + i + "=" + c);
		}
	}// printWorkspace

	public void printWorkspaceSum() {
		double real_sum = 0;
		double imaginary_sum = 0;
		for (int i = 0; i < this.FFT_SIZE; ++i) {
			real_sum += this.workspace[i * 2];
			imaginary_sum += this.workspace[i * 2 + 1];
		}
		System.out.println(new ComplexNumber(real_sum, imaginary_sum));
	}// printWorkSpaceSum

	static public void main(String[] args) throws IOException {

		Fft fft = new Fft(1024);
		fft.loadWorkspace(Sequences.SHIFT10to13);
		fft.loadFftCoefficients(RevFft.REVFFT_1_LOW);
		fft.doFft();
		fft.multiply();
		fft.doIfft();
		System.out.println(fft.getPeak());
		fft.loadWorkspace(Sequences.SHIFT10to13);
		fft.loadFftCoefficients(RevFft.REVFFT_1_HIGH);
		fft.doFft();
		fft.multiply();
		fft.doIfft();
		System.out.println(fft.getPeak());
		fft.loadWorkspace(Sequences.SHIFT10to13);
		fft.loadFftCoefficients(RevFft.REVFFT_2_LOW);
		fft.doFft();
		fft.multiply();
		fft.doIfft();
		System.out.println(fft.getPeak());
		fft.loadWorkspace(Sequences.SHIFT10to13);
		fft.loadFftCoefficients(RevFft.REVFFT_2_HIGH);
		fft.doFft();
		fft.multiply();
		fft.doIfft();
		System.out.println(fft.getPeak());
	}// main

}// Fft
