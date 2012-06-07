package com.gmail.takashi316.acousticpositioning;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

public class WriterThread extends Thread {
	private static DataDirectory dataDirectory;
	private Date date;
	private File file;

	protected WriterThread(Date date, String extension)
			throws FileNotFoundException {
		dataDirectory = new DataDirectory();
		this.file = dataDirectory
				.getFile("" + date.getTime() + "." + extension);
		this.date = date;
	}// WriterThread

	protected File getFile() {
		return this.file;

	}

	protected RandomAccessFile getRandomAccessFile()
			throws FileNotFoundException {
		RandomAccessFile raf = new RandomAccessFile(this.file, "rw");
		return raf;
	}

	protected BufferedWriter getBufferedWriter() throws IOException {
		//File output_file = dataDirectory.getFile("" + this.date.getTime()
		//		+ ".csv");
		if (file.exists())
			throw new IOException(file.getAbsoluteFile()
					+ " already exists.");
		Log.d("writing to " + file.getAbsolutePath());
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		return bw;
	}

	protected long getTime() {
		return this.date.getTime();
	}
}// WriterThread
