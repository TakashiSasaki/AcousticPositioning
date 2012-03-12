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
		this.enabled = true;
	}

	public void setCallback(Runnable callback) {
		this.callback = callback;
	}

	@Override
	public void run() {
		while (this.enabled) {
			short[] samples = this.recorderThread.getPreviousBuffer();
			if (samples == null) {
				try {
					Thread.sleep(50);
					continue;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}// if
			this.fft1Low.loadWorkspace(samples, 0);
			this.fft1Low.doFft();
			this.fft1Low.multiply();
			this.fft1Low.doIfft();
			this.peak1Low = this.fft1Low.getPeak();
			this.peakAverage1Low = this.fft1Low.getWorkspace().getAveragePower();
			this.fft1Low.getWorkspace().getAveragePower();
			
			this.fft1High.loadWorkspace(samples, 0);
			this.fft1High.doFft();
			this.fft1High.multiply();
			this.fft1High.doIfft();
			this.peak1High = this.fft1High.getPeak();
			this.peakAverage1High = this.fft1High.getWorkspace().getAveragePower();

			
			this.fft2Low.loadWorkspace(samples, 0);
			this.fft2Low.doFft();
			this.fft2Low.multiply();
			this.fft2Low.doIfft();
			this.peak2Low = this.fft2Low.getPeak();
			this.peakAverage2Low = this.fft2Low.getWorkspace().getAveragePower();

			this.fft2High.loadWorkspace(samples, 0);
			this.fft2High.doFft();
			this.fft2High.multiply();
			this.fft2High.doIfft();
			this.peak2High = this.fft2High.getPeak();
			this.peakAverage2High = this.fft2High.getWorkspace().getAveragePower();

			
			if (this.callback != null) {
				this.callback.run();
			}
		}
	}// run
}
