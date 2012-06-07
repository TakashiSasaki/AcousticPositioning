package com.gmail.takashi316.acousticpositioning;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

public class CsvWriter extends WriterThread {

	private int start;
	private int length;
	short[] samples;

	public CsvWriter(Date date, short[] samples, int start, int length)
			throws FileNotFoundException {
		super(date, "csv");
		this.start = start;
		this.length = length;
		this.samples = samples;
	}

	@Override
	public void run() {
		BufferedWriter bw;
		try {
			bw = getBufferedWriter();
			double start_date = (double) getTime();
			final double sampling_interval = 1000.0d / 48000.0d;
			for (int i = this.start; i < this.start + this.length; ++i) {
				double offset_in_millisecond = sampling_interval * i;
				double time = start_date + offset_in_millisecond;
				bw.write("" + time + "," + this.samples[i] + "\r\n");
			}// for
		} catch (IOException e) {
			e.printStackTrace();
		}// try
	}// run

}// Writer
