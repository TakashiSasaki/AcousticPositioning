package com.gmail.takashi316.acousticpositioning;


public class SamplesBase {

	protected short[] shortBuffer;
	protected byte[] byteBuffer;

	public short[] getSamplesInShort() {
		return this.shortBuffer;
	}// getSamplesInShort

	public byte[] getSamplesInByte() {
		return this.byteBuffer;
	}// getSamplesInByte
}
