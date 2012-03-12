package com.gmail.takashi316.acousticpositioning;

import java.io.IOException;

public class DetectingThread extends Thread {

	static final public int FFT_SIZE = 1024;
	private Fft fft1Low = new Fft(FFT_SIZE);
	private Fft fft1High = new Fft(FFT_SIZE);
	private Fft fft2Low = new Fft(FFT_SIZE);
	private Fft fft2High = new Fft(FFT_SIZE);
	RecorderThread recorderThread;
	public boolean enabled = true;
	private Runnable callback;

	public ComplexNumber peak1Low;
	public ComplexNumber peak1High;
	public ComplexNumber peak2Low;
	public ComplexNumber peak2High;
	public double peakAverage1Low;
	public double peakAverage1High;
	public double peakAverage2Low;
	public double peakAverage2High;
	public DetectingThread(RecorderThread recorder_thread)
			throws NumberFormatException, IOException {
		this.recorderThread = recorder_thread;
		this.fft1Low.loadFftCoefficients(RevFft.REVFFT_1_LOW);
		this.fft1High.loadFftCoefficients(RevFft.REVFFT_1_HIGH);
		this.fft2Low.loadFftCoefficients(RevFft.REVFFT_2_LOW);
		this.fft2High.loadFftCoefficients(RevFft.REVFFT_2_HIGH);
		enabled = true;
	}

	public void setCallback(Runnable callback) {
		this.callback = callback;
	}

	@Override
	public void run() {
		while (enabled) {
			short[] samples = this.recorderThread.getPreviousBuffer();
			if (samples == null) {
				try {
					Thread.sleep(50);
					continue;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}// if
			fft1Low.loadWorkspace(samples, 0);
			fft1Low.doFft();
			fft1Low.multiply();
			fft1Low.doIfft();
			peak1Low = fft1Low.getPeak();
			peakAverage1Low = fft1Low.getWorkspace().getAveragePower();
			fft1Low.getWorkspace().getAveragePower();
			
			fft1High.loadWorkspace(samples, 0);
			fft1High.doFft();
			fft1High.multiply();
			fft1High.doIfft();
			peak1High = fft1High.getPeak();
			peakAverage1High = fft1High.getWorkspace().getAveragePower();

			
			fft2Low.loadWorkspace(samples, 0);
			fft2Low.doFft();
			fft2Low.multiply();
			fft2Low.doIfft();
			peak2Low = fft2Low.getPeak();
			peakAverage2Low = fft2Low.getWorkspace().getAveragePower();

			fft2High.loadWorkspace(samples, 0);
			fft2High.doFft();
			fft2High.multiply();
			fft2High.doIfft();
			peak2High = fft2High.getPeak();
			peakAverage2High = fft2High.getWorkspace().getAveragePower();

			
			if (callback != null) {
				callback.run();
			}
		}
	}// run
}
