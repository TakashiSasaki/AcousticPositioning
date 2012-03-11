package com.gmail.takashi316.acousticpositioning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ReaderThread extends Thread {
	private static DataDirectory dataDirectory;
	private File file;

	protected ReaderThread(String file_name) throws FileNotFoundException {
		dataDirectory = new DataDirectory();
		this.file = dataDirectory.getFile(file_name);
	}// WriterThread

	protected File getFile() {
		return this.file;
	}

	protected RandomAccessFile getRandomAccessFile()
			throws FileNotFoundException {
		RandomAccessFile raf = new RandomAccessFile(this.file, "rw");
		return raf;
	}

	protected BufferedReader getBufferedReader() throws IOException {
		Log.d("reading from " + this.file.getAbsolutePath());
		FileReader fr = new FileReader(this.file);
		BufferedReader br = new BufferedReader(fr);
		return br;
	}

}// WriterThread
