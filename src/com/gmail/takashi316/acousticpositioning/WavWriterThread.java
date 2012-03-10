package com.gmail.takashi316.acousticpositioning;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

public class WavWriterThread extends WriterThread {
	public WavWriterThread(short[] samples, int start, int length)
			throws FileNotFoundException {
		super(new Date(), "wav");
		this.samples = samples;
		this.start = start;
		this.length = length;
	}// a constructor

	private int start;
	private int length;
	private short[] samples;

	public void run() {
		RandomAccessFile raf;
		try {
			raf = getRandomAccessFile();
			raf.setLength(0); // Set file length to 0, to prevent
			// unexpected behavior in case the
			// file already existed

			raf.writeBytes("RIFF");
			raf.writeInt(0); // Final file size not known yet, write 0
			raf.writeBytes("WAVE");
			raf.writeBytes("fmt ");
			raf.writeInt(Integer.reverseBytes(16)); // Sub-chunk size, 16 for
													// PCM
			raf.writeShort(Short.reverseBytes((short) 1)); // AudioFormat, 1 for
															// PCM
			raf.writeShort(Short.reverseBytes((short) 1));// Number of channels,
															// 1
															// for mono, 2 for
															// stereo
			raf.writeInt(Integer.reverseBytes(MyAudioRecord.SAMPLING_RATE)); // Sample
			// rate
			raf.writeInt(Integer
					.reverseBytes(MyAudioRecord.SAMPLING_RATE * 16 * 1 / 8)); // Byte
			// rate,
			// SampleRate*NumberOfChannels*BitsPerSample/8
			raf.writeShort(Short.reverseBytes((short) (1 * 16 / 8))); // Block
			// align,
			// NumberOfChannels*BitsPerSample/8
			raf.writeShort(Short.reverseBytes((short) 16)); // Bits per sample
			raf.writeBytes("data");
			raf.writeInt(0); // Data chunk size not known yet, write 0
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		byte[] byte_buffer = new byte[length * 2];
		for (int i = start; i < start + length; ++i) {
			byte_buffer[i * 2] = (byte) (samples[i] & 0xff);
			byte_buffer[i * 2 + 1] = (byte) (samples[i] >>> 8);
		}// for

		try {
			raf.write(byte_buffer);
			raf.seek(4); // Write size to RIFF header
			raf.writeInt(Integer.reverseBytes(36 + byte_buffer.length));

			raf.seek(40); // Write size to Subchunk2Size field
			raf.writeInt(Integer.reverseBytes(byte_buffer.length));
			raf.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} // Write buffer to file
	}// run
}
