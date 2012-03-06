package com.gmail.takashi316.acousticpositioning;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5 {
	static private MessageDigest messageDigest;
	private String md5String;

	public Md5() throws NoSuchAlgorithmException {
		if (messageDigest != null) {
			messageDigest = MessageDigest.getInstance("MD5");
		}// if
	}// Md5

	public String getMd5String() {
		if (md5String == null) {
			throw new NullPointerException(
					"message digest is not calculated yet.");
		}// if
		return md5String;
	}// getMd5String

	static private byte getLowerByte(short s) {
		return (byte) (s & 0xff);
	}

	static private byte getUpperByte(short s) {
		return (byte) (s >>> 8);
	}

	static private byte[] getBigEndian(short s) {
		byte[] tmp = new byte[2];
		tmp[0] = getUpperByte(s);
		tmp[1] = getLowerByte(s);
		return tmp;
	}

	static private byte[] getLittleEndian(short s) {
		byte[] tmp = new byte[2];
		tmp[0] = getLowerByte(s);
		tmp[1] = getUpperByte(s);
		return tmp;
	}

	public void putBigEndian(short[] data) {
		for (int i = 0; i < data.length; ++i) {
			messageDigest.update(getBigEndian(data[i]));
		}
	}

	public void putLittleEndian(short[] data) {
		for (int i = 0; i < data.length; ++i) {
			messageDigest.update(getLittleEndian(data[i]));
		}
	}
}
