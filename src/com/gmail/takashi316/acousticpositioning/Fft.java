package com.gmail.takashi316.acousticpositioning;

import java.awt.image.SampleModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class Fft {
	public static final String REVERSED_FFT_COEFFICIENTS_OF_HIGH_FREQUENCY_OF_M_SEQUENCE_1 = "rev_high_1.csv";
	public static final String REVERSED_FFT_COEFFICIENTS_OF_LOW_FREQUENCY_OF_M_SEQUENCE_1 = "rev_low_1.csv";
	public static final String REVERSED_FFT_COEFFICIENTS_OF_HIGH_FREQUENCY_OF_M_SEQUENCE_2 = "rev_high_2.csv";
	public static final String REVERSED_FFT_COEFFICIENTS_OF_LOW_FREQUENCY_OF_M_SEQUENCE_2 = "rev_low_2.csv";

	public int FFT_SIZE = 1024;
	public int FFT_SIZE_HALF = 512;

	// the given file must have even number of coefficients
	// public static int EXCEL_FORMAT_SIZE = 4096;
	// the index of even component
	// public static int EXCEL_FORMAT_HIGHEST_EVEN_COMPONENT = 2048;
	// the beginning of conjugate coefficients
	// public static int EXCEL_FORMAT_CONJUGATE = 2049;
	public enum WorkspaceState {
		WORKSPACE_STATE_IS_UNKNOWN, WORKSPACE_STATE_IS_REAL_TIME_DOMAIN, WORKSPACE_STATE_IS_FREQUENCY_DOMAIN, WORKSPACE_STATE_IS_COMPLEX_TIME_DOMAIN
	}

	protected WorkspaceState workspaceState = WorkspaceState.WORKSPACE_STATE_IS_UNKNOWN;
	private DoubleFFT_1D fft;
	protected double[] workspace;
	protected double[] fftCoefficients;

	public Fft() {
		this.FFT_SIZE = 1024;
		this.FFT_SIZE_HALF = 512;
		this.fft = new DoubleFFT_1D(this.FFT_SIZE);
		this.workspace = new double[this.FFT_SIZE * 2];
		this.fftCoefficients = new double[this.FFT_SIZE * 2];
	}

	public Fft(int size) {
		this.FFT_SIZE = size;
		this.FFT_SIZE_HALF = size / 2;
		this.fft = new DoubleFFT_1D(this.FFT_SIZE);
		this.workspace = new double[this.FFT_SIZE * 2];
		this.fftCoefficients = new double[this.FFT_SIZE * 2];
	}

	public void doFft() {
		this.fft.realForwardFull(this.workspace);
		this.workspaceState = WorkspaceState.WORKSPACE_STATE_IS_FREQUENCY_DOMAIN;
	}

	public void multiply() {
		if (this.workspaceState != WorkspaceState.WORKSPACE_STATE_IS_FREQUENCY_DOMAIN) {
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

	public void doIfft() {
		this.fft.complexInverse(this.workspace, true);
		this.workspaceState = WorkspaceState.WORKSPACE_STATE_IS_COMPLEX_TIME_DOMAIN;
	}// doIfft

	public ArrayList<ComplexNumber> getWorkspace() {
		ArrayList<ComplexNumber> a = new ArrayList<ComplexNumber>(this.FFT_SIZE);
		for (int i = 0; i < this.FFT_SIZE; ++i) {
			final ComplexNumber c = new ComplexNumber(this.workspace[i * 2],
					this.workspace[i * 2 + 1]);
			c.index = i;
			a.add(c);
		}// for
		return a;
	}// getWorkspace

	public int getPeak() {
		ArrayList<ComplexNumber> a = this.getWorkspace();
		Collections.sort(a, new ComplexNumber.ComplexNumberComparator(true));
		return a.get(0).index;
	}// getPeak

	public void loadFftCoefficients(String[] lines)
			throws NumberFormatException, IOException {
		// this method does not care that coefficients in the given file is
		// ordered reversed or not;

		ArrayList<ComplexNumber> excel_format = new ArrayList<ComplexNumber>();
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

	public void printFftCoefficients() {
		for (int i = 0; i < this.FFT_SIZE; ++i) {
			ComplexNumber cn = new ComplexNumber(this.fftCoefficients[i * 2],
					this.fftCoefficients[i * 2 + 1]);
			if (cn.imaginary > 100)
				System.out.println("" + i + "=" + cn);
		}
	}

	public void printFftCoefficientsSum() {
		double real_sum = 0;
		double imaginary_sum = 0;
		for (int i = 0; i < this.FFT_SIZE; ++i) {
			real_sum += this.fftCoefficients[i * 2];
			imaginary_sum += this.fftCoefficients[i * 2 + 1];
		}
		ComplexNumber cn = new ComplexNumber(real_sum, imaginary_sum);
		System.out.println(cn);
	}

	public void printWorkspace() {
		if (this.workspaceState == WorkspaceState.WORKSPACE_STATE_IS_FREQUENCY_DOMAIN
				|| this.workspaceState == WorkspaceState.WORKSPACE_STATE_IS_COMPLEX_TIME_DOMAIN) {
			for (int i = 0; i < this.FFT_SIZE; ++i) {
				final double real = this.workspace[i * 2];
				final double imaginary = this.workspace[i * 2 + 1];
				ComplexNumber c = new ComplexNumber(real, imaginary);
				System.out.println("" + i + "=" + c);
			}
		}
		if (this.workspaceState == WorkspaceState.WORKSPACE_STATE_IS_REAL_TIME_DOMAIN) {
			for (int i = 0; i < this.FFT_SIZE; ++i) {
				System.out.println("" + i + "=" + this.workspace[i]);
			}
		}
	}

	public void printWorkspaceSum() {
		if (this.workspaceState == WorkspaceState.WORKSPACE_STATE_IS_FREQUENCY_DOMAIN
				|| this.workspaceState == WorkspaceState.WORKSPACE_STATE_IS_COMPLEX_TIME_DOMAIN) {
			double real_sum = 0;
			double imaginary_sum = 0;
			for (int i = 0; i < this.FFT_SIZE; ++i) {
				real_sum += this.workspace[i * 2];
				imaginary_sum += this.workspace[i * 2 + 1];
			}
			System.out.println(new ComplexNumber(real_sum, imaginary_sum));
		}
		if (this.workspaceState == WorkspaceState.WORKSPACE_STATE_IS_REAL_TIME_DOMAIN) {
			double real_sum = 0;
			for (int i = 0; i < this.FFT_SIZE; ++i) {
				real_sum += this.workspace[i];
			}
			System.out.println(real_sum);
		}
	}

	public void loadSamples(String lines[]) throws IOException {
		int i = 0;
		for (String line : lines) {
			this.workspace[i] = Double.parseDouble(line);
			++i;
		}// while
		if (i != this.FFT_SIZE) {
			throw new RuntimeException("given string array doesn't have "
					+ this.FFT_SIZE + " of double values. ");
		}// if
		this.workspaceState = WorkspaceState.WORKSPACE_STATE_IS_REAL_TIME_DOMAIN;
	}// loadSamples

	static public void main(String[] args) throws IOException {

		Fft fft = new Fft(1024);
		fft.loadSamples(SampleSignal.SHIFT10_13);
		fft.loadFftCoefficients(RevFft.REVFFT_1_LOW);
		fft.doFft();
		fft.multiply();
		fft.doIfft();
		System.out.println(fft.getPeak());
		fft.loadSamples(SampleSignal.SHIFT10_13);
		fft.loadFftCoefficients(RevFft.REVFFT_1_HIGH);
		fft.doFft();
		fft.multiply();
		fft.doIfft();
		System.out.println(fft.getPeak());
		fft.loadSamples(SampleSignal.SHIFT10_13);
		fft.loadFftCoefficients(RevFft.REVFFT_2_LOW);
		fft.doFft();
		fft.multiply();
		fft.doIfft();
		System.out.println(fft.getPeak());
		fft.loadSamples(SampleSignal.SHIFT10_13);
		fft.loadFftCoefficients(RevFft.REVFFT_2_HIGH);
		fft.doFft();
		fft.multiply();
		fft.doIfft();
		System.out.println(fft.getPeak());
	}// main
}// Fft
