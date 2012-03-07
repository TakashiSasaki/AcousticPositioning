package com.gmail.takashi316.acousticpositioning;

import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5 {
	private MessageDigest messageDigest;

	public Md5() throws NoSuchAlgorithmException {
		messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.reset();
	}// Md5

	public String getMd5String() {
		StringWriter sw = new StringWriter();
		byte[] digest = messageDigest.digest();
		assert (digest.length == 16);
		for (int i = 0; i < digest.length; ++i) {
			String hex1digit = Integer.toHexString(digest[i] & 0xff);
			sw.append(hex1digit);
		}
		String digest_hex_string = sw.toString();
		assert (digest_hex_string.length() == 32);
		return digest_hex_string;
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
			byte[] tmp = getBigEndian(data[i]);
			messageDigest.update(tmp);
		}
	}

	public void putLittleEndian(short[] data) {
		for (int i = 0; i < data.length; ++i) {
			byte[] tmp = getLittleEndian(data[i]);
			messageDigest.update(tmp);
		}
	}
}
