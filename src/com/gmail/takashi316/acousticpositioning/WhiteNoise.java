package com.gmail.takashi316.acousticpositioning;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import android.os.Environment;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class WhiteNoise {
	private static File externalStorageDirectory;
	private static File dataDirectory;
	private static String DATA_DIRECTORY_NAME = "AcousticPositioning";

	public static void main(String[] args) {
		SineAudioTrack sine_audio_track = new SineAudioTrack(1000, 1);
		short[] short_array = sine_audio_track.getBuffer();
		final int size = 32768;
		double[] double_array = new double[size * 2];
		for (int i = 0; i < size; ++i) {
			double_array[i * 2] = (double) short_array[i];
			double_array[i * 2 + 1] = 0.0d;
		}
		DoubleFFT_1D fft = new DoubleFFT_1D(size);
		fft.complexForward(double_array);
		try {
			writeArrayToCsv(double_array);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}// main

	static public void writeArrayToCsv(double[] fft_result) throws IOException {
		if (fft_result.length % 2 != 0) {
			throw new IllegalArgumentException(
					"the length of the array containing FFT result should be even number");
		}// if
		final int size = fft_result.length / 2;
		externalStorageDirectory = Environment.getExternalStorageDirectory();
		dataDirectory = new File(externalStorageDirectory, DATA_DIRECTORY_NAME);
		if (!dataDirectory.exists()) {
			dataDirectory.mkdir();
		}
		if (!dataDirectory.exists() || !dataDirectory.isDirectory()) {
			throw new FileNotFoundException("Can't create "
					+ dataDirectory.getAbsolutePath());
		}
		Date date = new Date();
		File csv_file = new File(dataDirectory, "" + date.getTime() + ".csv");
		FileWriter fw = new FileWriter(csv_file);
		BufferedWriter bw = new BufferedWriter(fw);
		for (int i = 0; i < size; ++i) {
			bw.write("" + i + ", " + fft_result[i * 2] + ", "
					+ fft_result[i * 2 + 1] + "\r\n");
		}// for
		fw.close();
	}// writeArrayToCsv
}// WhiteNote
