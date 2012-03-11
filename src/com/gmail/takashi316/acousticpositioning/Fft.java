package com.gmail.takashi316.acousticpositioning;

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
		WORKSPACE_STATE_IS_UNKNOWN, WORKSPACE_STATE_IS_TIME_DOMAIN, WORKSPACE_STATE_IS_FREQUENCY_DOMAIN
	}

	private WorkspaceState workspaceState = WorkspaceState.WORKSPACE_STATE_IS_UNKNOWN;
	private DoubleFFT_1D fft;
	private double[] workspace;
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
		this.fft.realForwardFull(this.workspace);
		this.workspaceState = WorkspaceState.WORKSPACE_STATE_IS_TIME_DOMAIN;
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
		for (int i = 0; i < this.fftCoefficients.length; ++i) {
			System.out.println(this.fftCoefficients[i]);
		}
	}

	public void printWorkspace() {
		if (this.workspaceState == WorkspaceState.WORKSPACE_STATE_IS_FREQUENCY_DOMAIN) {
			for (int i = 0; i < this.FFT_SIZE; ++i) {
				final double real = this.workspace[i * 2];
				final double imaginary = this.workspace[i * 2 + 1];
				ComplexNumber c = new ComplexNumber(real, imaginary);
				System.out.println("" + i + "=" + c);
			}
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
		this.workspaceState = WorkspaceState.WORKSPACE_STATE_IS_TIME_DOMAIN;
	}// loadSamples

	static public void main(String[] args) {
		String[] samples = new String[] { "0", "1", "1.22514845490862E-16",
				"-1", "-2.45029690981724E-16", "1", "3.67544536472586E-16",
				"-1", "-4.90059381963448E-16", "1", "6.1257422745431E-16",
				"-1", "-7.35089072945172E-16", "1", "8.57603918436034E-16",
				"-1", "-9.80118763926896E-16", "1", "1.10263360941776E-15",
				"-1", "-1.22514845490862E-15", "1", "4.90037697919998E-15",
				"-1", "-1.47017814589034E-15", "1", "-1.96002068741929E-15",
				"-1", "-1.71520783687207E-15", "1", "5.39043636116343E-15",
				"-1" };
		Fft fft = new Fft(32);
		try {
			fft.loadSamples(samples);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// fft.printWorkspace();
		fft.doFft();
		fft.printWorkspace();
		System.out.println("peak = " + fft.getPeak());
		System.out.println("finished");
		ComplexNumber cn = new ComplexNumber(2.0, -3.0);
	}// main
}// Fft
