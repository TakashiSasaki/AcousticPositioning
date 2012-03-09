package com.gmail.takashi316.acousticpositioning;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

import android.os.Environment;

public class WriterThread extends Thread {
	public static File externalStorageDirectory;
	public static File dataDirectory;
	public static String DATA_DIRECTORY_NAME = "AcousticPositioning";
	private Date date;
	private File file;

	protected WriterThread(Date date, String extension)
			throws FileNotFoundException {
		externalStorageDirectory = Environment.getExternalStorageDirectory();
		dataDirectory = new File(externalStorageDirectory, DATA_DIRECTORY_NAME);
		if (!dataDirectory.exists()) {
			dataDirectory.mkdir();
		}// if
		if (!dataDirectory.exists() || !dataDirectory.isDirectory()) {
			throw new FileNotFoundException("Can't create "
					+ dataDirectory.getAbsolutePath());
		}// if
		this.file = new File(dataDirectory, "" + date.getTime() + "."
				+ extension);
		this.date = date;
	}// WriterThread

	protected File getFile() {
		return this.file;

	}

	protected RandomAccessFile getRandomAccessFile()
			throws FileNotFoundException {
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		return raf;
	}

	protected BufferedWriter getBufferedWriter() throws IOException {
		File output_file = new File(dataDirectory, "" + date.getTime() + ".csv");
		if (output_file.exists())
			throw new IOException(output_file.getAbsoluteFile()
					+ " already exists.");
		Log.d("writing to " + output_file.getAbsolutePath());
		FileWriter fw = new FileWriter(output_file);
		BufferedWriter bw = new BufferedWriter(fw);
		return bw;
	}

	protected long getTime() {
		return date.getTime();
	}
}// WriterThread
