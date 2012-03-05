package com.gmail.takashi316.acousticpositioning;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import android.media.AudioRecord;
import android.os.Environment;

public class Recorder {
	private static Recorder theRecorder;
	private Thread thread;
	private Record record;
	private int recordedFramesMarker = 0;
	final static private int RECORDING_BUFFER_SIZE_IN_FRAMES = 48000 * 10;
	final static private int TIME_INTERVAL_TO_READ_FRAMES_IN_MILLISECONDS = 100;
	final static private int THREASHOLD_TO_CONTINUE_READING_FRAMES = 100;
	short[] recordedFrames;
	Date startDate;
	private File externalStorageDirectory;
	private File dataDirectory;
	String DATA_DIRECTORY_NAME = "AcousticPositioning";

	static public Recorder getTheRecorder() throws FileNotFoundException {
		if (theRecorder == null) {
			theRecorder = new Recorder();
		}
		return theRecorder;
	}// getTheRecorder

	private Recorder() throws FileNotFoundException {
		externalStorageDirectory = Environment.getExternalStorageDirectory();
		dataDirectory = new File(externalStorageDirectory, DATA_DIRECTORY_NAME);
		if (!dataDirectory.exists()) {
			dataDirectory.mkdir();
		}
		if (!dataDirectory.exists() || !dataDirectory.isDirectory()) {
			throw new FileNotFoundException("Can't create "
					+ dataDirectory.getAbsolutePath());
		}
	}// a constructor

	class RecordingThread extends Thread {
		@Override
		public void run() {
			if (record == null) {
				Log.d("no record object");
				return;
			}
			record.startRecording();
			try {
				Log.v("waiting for preparation  ...");
				Thread.sleep(TIME_INTERVAL_TO_READ_FRAMES_IN_MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}// try

			while (true) {
				if (record.getState() != AudioRecord.STATE_INITIALIZED) {
					Log.v("the state is not STATE_INITIALIZED.");
					break;
				}
				if (record.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
					Log.v("the recording state is not RECORDSTATE_RECORDING.");
					break;
				}
				Log.v("reading from AudioRecord instance");
				int read_frames = record.read(recordedFrames,
						recordedFramesMarker, RECORDING_BUFFER_SIZE_IN_FRAMES
								- recordedFramesMarker);
				if (read_frames == AudioRecord.ERROR_INVALID_OPERATION) {
					Log.v("ERROR_INVALID_OPERATION while reading from AudioRecord.");
					record.stop();
					break;
				}
				if (read_frames == AudioRecord.ERROR_BAD_VALUE) {
					Log.v("ERROR_BAD_VALUE while reading from AudioRecord.");
					record.stop();
					break;
				}
				Log.v("" + read_frames + " frames read");
				recordedFramesMarker += read_frames;
				if (recordedFramesMarker >= RECORDING_BUFFER_SIZE_IN_FRAMES) {
					Log.v("reached to RECORDING_BUFFER_SIZE_IN_FRAMES");
					break;
				}
				if (read_frames > THREASHOLD_TO_CONTINUE_READING_FRAMES)
					continue;
				try {
					Log.v("sleeping ...");
					Thread.sleep(TIME_INTERVAL_TO_READ_FRAMES_IN_MILLISECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}// try
			}// while
			Log.v("finished recording.");
		}// run
	}// RecordingThread
	
	private void writeToFile() throws IOException {
		File output_file = new File(dataDirectory, "r_" + startDate.getTime()
				+ ".csv");
		if (output_file.exists())
			throw new IOException(output_file.getAbsoluteFile()
					+ " already exists.");
		Log.d("writing to " + output_file.getAbsolutePath());
		FileWriter fw = new FileWriter(output_file);
		BufferedWriter bw = new BufferedWriter(fw);
		double start_date = (double) startDate.getTime();
		final double sampling_interval =  1000.0d / 48000.0d;
		for (int i = 0; i < recordedFramesMarker; ++i) {
			//Log.d("writing offset " + i);
			double offset_in_millisecond = sampling_interval * i;
			double time = start_date + offset_in_millisecond;
			bw.write("" + time + "," + recordedFrames[i]+"\r\n");
		}// for
		Log.d("written");
	}// writeToFile

	private void stopRecording() {
		if (thread == null) {
			Log.v("No recorder object.");
			return;
		}// if
		if (record.getRecordingState() == Record.RECORDSTATE_RECORDING) {
			Log.v("stopping recording");
			record.stop();
		}// if
		if (record.getState() == Record.STATE_INITIALIZED) {
			Log.v("releasing internal buffer of record object");
			record.release();
		}// if
		try {
			Log.v("waiting until the recording thread stops");
			thread.join();
			record = null;
			writeToFile();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}// try
	}// stopRecording

	synchronized public void startRecording() {
		stopRecording();
		recordedFrames = new short[RECORDING_BUFFER_SIZE_IN_FRAMES];
		recordedFramesMarker = 0;
		record = new Record();
		thread = new RecordingThread();
		startDate = new Date();
		thread.start();
	}// startRecording

	@Override
	protected void finalize() throws Throwable {
		stopRecording();
		super.finalize();
	}// finalize

	short[] getRecordedFrames() {
		short[] recorded_frames = this.recordedFrames;
		this.recordedFrames = null;
		return recorded_frames;
	}// getRecordedFrames
}// Recorder
