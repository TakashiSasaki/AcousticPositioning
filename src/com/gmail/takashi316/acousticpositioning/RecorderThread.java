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

	public RecorderThread(int recording_duration_in_millisecond, boolean continuously) {
		this.audioRecord = new MyAudioRecord();
		this.currentBuffer = new short[recording_duration_in_millisecond / 1000
				* MyAudioRecord.SAMPLING_RATE];
		this.currentBufferMarker = 0;
		this.nextBufferSize = this.currentBuffer.length;
		this.toBeContinued = continuously;
	}// a constructor

	public void setRunAfterRecordingCallback(Runnable runnable) {
		this.runAfterRecordingCallback = runnable;
	}// setRunAfterRecordingCallback

	@Override
	public void run() {
		this.audioRecord.startRecording();
		while (true) {
			if (this.currentBufferMarker >= this.currentBuffer.length) {
				if (this.toBeContinued == false) {
					this.previousBuffer = this.currentBuffer;
					this.currentBuffer = null;
					break;
				}
				if (this.previousBuffer != null
						&& this.previousBuffer.length == this.nextBufferSize) {
					Log.v("recycling previous buffere");
					swapPreviousAndCurrentBuffer();
				} else {
					Log.v("switching to next buffere");
					this.previousBuffer = this.currentBuffer;
					this.currentBuffer = new short[this.nextBufferSize];
				}
				this.currentBufferMarker = 0;
			}
			if (this.audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
				Log.v("the state is not STATE_INITIALIZED.");
				break;
			}
			if (this.audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
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
			int read_frames = this.audioRecord.read(this.currentBuffer,
					this.currentBufferMarker, this.currentBuffer.length
							- this.currentBufferMarker);
			if (read_frames == AudioRecord.ERROR_INVALID_OPERATION) {
				Log.v("ERROR_INVALID_OPERATION while reading from AudioRecord.");
				break;
			}
			if (read_frames == AudioRecord.ERROR_BAD_VALUE) {
				Log.v("ERROR_BAD_VALUE while reading from AudioRecord.");
				break;
			}
			Log.v("" + read_frames + " frames read");
			this.currentBufferMarker += read_frames;
		}// while

		this.audioRecord.stop();
		this.audioRecord.release();
		Log.v("finished recording thread");
		if (this.runAfterRecordingCallback != null) {
			this.runAfterRecordingCallback.run();
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
		short[] tmp = this.currentBuffer;
		this.currentBuffer = this.previousBuffer;
		this.previousBuffer = tmp;
	}// swapPreviousAndCurrentBuffer

	public short[] getPreviousBuffer() {
		short[] tmp = this.previousBuffer;
		this.previousBuffer = null;
		return tmp;
	}// getRecordedFrames

	public void setNextBufferSize(int next_buffer_size) {
		this.nextBufferSize = next_buffer_size;
	}// setNextBufferSize

}// RecorderThread
