package com.gmail.takashi316.acousticpositioning;

import android.media.AudioRecord;

public class RecorderThread extends Thread {
	private AudioRecord audioRecord;
	private short[] currentBuffer;
	private int currentBufferMarker;
	private short[] previousBuffer;
	final static private int TIME_INTERVAL_TO_READ_FRAMES_IN_MILLISECONDS = 100;
	private Runnable runAfterRecordingCallback;
	private boolean toBeContinued;
	private int nextBufferSize;

	public RecorderThread(int recording_duration_in_millisecond) {
		audioRecord = new MyAudioRecord();
		currentBuffer = new short[recording_duration_in_millisecond / 1000
				* MyAudioRecord.SAMPLING_RATE];
		currentBufferMarker = 0;
		nextBufferSize = currentBuffer.length;
		toBeContinued = false;
	}// a constructor

	public void setRunAfterRecordingCallback(Runnable runnable) {
		this.runAfterRecordingCallback = runnable;
	}// setRunAfterRecordingCallback

	@Override
	public void run() {
		audioRecord.startRecording();
		while (true) {
			if (currentBufferMarker >= currentBuffer.length) {
				if (toBeContinued == false) {
					previousBuffer = currentBuffer;
					currentBuffer = null;
					break;
				}
				if (previousBuffer != null
						&& previousBuffer.length == nextBufferSize) {
					Log.v("recycling previous buffere");
					swapPreviousAndCurrentBuffer();
				} else {
					Log.v("switching to next buffere");
					previousBuffer = currentBuffer;
					currentBuffer = new short[nextBufferSize];
				}
				currentBufferMarker = 0;
			}
			if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
				Log.v("the state is not STATE_INITIALIZED.");
				break;
			}
			if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
				Log.v("the recording state is not RECORDSTATE_RECORDING.");
				break;
			}
			try {
				Log.v("sleeping ...");
				Thread.sleep(TIME_INTERVAL_TO_READ_FRAMES_IN_MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}// try
			Log.v("reading from AudioRecord instance");
			int read_frames = audioRecord.read(currentBuffer,
					currentBufferMarker, currentBuffer.length
							- currentBufferMarker);
			if (read_frames == AudioRecord.ERROR_INVALID_OPERATION) {
				Log.v("ERROR_INVALID_OPERATION while reading from AudioRecord.");
				break;
			}
			if (read_frames == AudioRecord.ERROR_BAD_VALUE) {
				Log.v("ERROR_BAD_VALUE while reading from AudioRecord.");
				break;
			}
			Log.v("" + read_frames + " frames read");
			currentBufferMarker += read_frames;
		}// while

		audioRecord.stop();
		audioRecord.release();
		Log.v("finished recording thread");
		if (runAfterRecordingCallback != null) {
			runAfterRecordingCallback.run();
		}// if
	}// run

	public void stopRecording() {
		this.toBeContinued = false;
	}// stopRecording

	@Override
	protected void finalize() throws Throwable {
		this.stopRecording();
		this.audioRecord.stop();
		this.audioRecord.release();
		super.finalize();
	}// finalize

	private void swapPreviousAndCurrentBuffer() {
		short[] tmp = currentBuffer;
		currentBuffer = previousBuffer;
		previousBuffer = tmp;
	}// swapPreviousAndCurrentBuffer

	public short[] getPreviousBuffer() {
		short[] tmp = previousBuffer;
		previousBuffer = null;
		return tmp;
	}// getRecordedFrames

	public void setNextBufferSize(int next_buffer_size) {
		nextBufferSize = next_buffer_size;
	}// setNextBufferSize

}// RecorderThread
