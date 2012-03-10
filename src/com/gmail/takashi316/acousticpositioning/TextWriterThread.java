package com.gmail.takashi316.acousticpositioning;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

public class TextWriterThread extends WriterThread {

	private short[] samples;

	public TextWriterThread(short[] samples) throws FileNotFoundException {
		super(new Date(), "txt");
		this.samples = samples;
	}

	@Override
	public void run() {
		try {
			BufferedWriter bw = getBufferedWriter();
			for (int i = 0; i < this.samples.length; ++i) {
				bw.write("" + this.samples[i] + "\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
