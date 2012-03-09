package com.gmail.takashi316.acousticpositioning;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

import android.os.Environment;

public class Writer {
	
	private static File externalStorageDirectory;
	private static File dataDirectory;
	private static String DATA_DIRECTORY_NAME = "AcousticPositioning";
	private Date date;

	public Writer() throws FileNotFoundException {
		init(new Date());
	}

	public Writer(Date date) throws FileNotFoundException {
		init(date);
	}// a constructor

	public void init(Date date) throws FileNotFoundException {
		this.date = date;
		externalStorageDirectory = Environment.getExternalStorageDirectory();
		dataDirectory = new File(externalStorageDirectory, DATA_DIRECTORY_NAME);
		if (!dataDirectory.exists()) {
			dataDirectory.mkdir();
		}
		if (!dataDirectory.exists() || !dataDirectory.isDirectory()) {
			throw new FileNotFoundException("Can't create "
					+ dataDirectory.getAbsolutePath());
		}
	}

	public void writeToCsv(double[] fft_result) {

	}

	public void writeToCsv(short[] samples) throws IOException {
		writeToCsv(samples, 0, samples.length);
	}

	public void writeToCsv(short[] samples, int start, int end)
			throws IOException {
		File output_file = new File(dataDirectory, "" + date.getTime() + ".csv");
		if (output_file.exists())
			throw new IOException(output_file.getAbsoluteFile()
					+ " already exists.");
		Log.d("writing to " + output_file.getAbsolutePath());
		FileWriter fw = new FileWriter(output_file);
		BufferedWriter bw = new BufferedWriter(fw);
		double start_date = (double) date.getTime();
		final double sampling_interval = 1000.0d / 48000.0d;
		for (int i = start; i < end; ++i) {
			// Log.d("writing offset " + i);
			double offset_in_millisecond = sampling_interval * i;
			double time = start_date + offset_in_millisecond;
			bw.write("" + time + "," + samples[i] + "\r\n");
		}// for
	}// writeSamplesToCsv

	public void writeToWav(short[] sample) throws IOException {
		writeToWav(sample, 0, sample.length);
	}

	public void writeToWav(short[] samples, int start, int end)
			throws IOException {
		File wav_file = new File(dataDirectory, "" + date.getTime() + ".wav");
		if (wav_file.exists())
			throw new IOException(wav_file.getAbsoluteFile()
					+ " already exists.");
		Log.d("writing to " + wav_file.getAbsolutePath());

		RandomAccessFile raf = new RandomAccessFile(wav_file, "rw");
		raf.setLength(0); // Set file length to 0, to prevent
							// unexpected behavior in case the
							// file already existed
		raf.writeBytes("RIFF");
		raf.writeInt(0); // Final file size not known yet, write 0
		raf.writeBytes("WAVE");
		raf.writeBytes("fmt ");
		raf.writeInt(Integer.reverseBytes(16)); // Sub-chunk size, 16 for PCM
		raf.writeShort(Short.reverseBytes((short) 1)); // AudioFormat, 1 for PCM
		raf.writeShort(Short.reverseBytes((short) 1));// Number of channels, 1
														// for mono, 2 for
														// stereo
		raf.writeInt(Integer.reverseBytes(Record.SAMPLING_RATE)); // Sample
																	// rate
		raf.writeInt(Integer.reverseBytes(Record.SAMPLING_RATE * 16 * 1 / 8)); // Byte
																				// rate,
																				// SampleRate*NumberOfChannels*BitsPerSample/8
		raf.writeShort(Short.reverseBytes((short) (1 * 16 / 8))); // Block
																	// align,
																	// NumberOfChannels*BitsPerSample/8
		raf.writeShort(Short.reverseBytes((short) 16)); // Bits per sample
		raf.writeBytes("data");
		raf.writeInt(0); // Data chunk size not known yet, write 0

		byte[] byte_buffer = new byte[end * 2];
		for (int i = 0; i < end; ++i) {
			byte_buffer[i * 2] = (byte) (samples[i] & 0xff);
			byte_buffer[i * 2 + 1] = (byte) (samples[i] >>> 8);
		}// for

		raf.write(byte_buffer); // Write buffer to file

		raf.seek(4); // Write size to RIFF header
		raf.writeInt(Integer.reverseBytes(36 + byte_buffer.length));

		raf.seek(40); // Write size to Subchunk2Size field
		raf.writeInt(Integer.reverseBytes(byte_buffer.length));
		raf.close();
	}// writeToWav

}// Writer
