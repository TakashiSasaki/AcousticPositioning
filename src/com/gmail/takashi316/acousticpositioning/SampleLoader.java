package com.gmail.takashi316.acousticpositioning;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class SampleLoader {
	BufferedReader bufferedReader;

	public SampleLoader(String file_name) throws IOException {
		Reader rt = new Reader(file_name);
		this.bufferedReader = rt.getBufferedReader();
	}

	public short[] load() throws IOException {
		ArrayList<Short> array_list = new ArrayList<Short>();
		String line;
		while ((line = this.bufferedReader.readLine()) != null) {
			array_list.add(Short.parseShort(line));
		}// while
		short[] primitive_array = new short[array_list.size()];
		int i = 0;
		for (Short s : array_list) {
			primitive_array[i] = s.shortValue();
			++i;
		}// for
		return primitive_array;
	}// load

	public void load(short[] primitive_array) throws NumberFormatException,
			IOException {
		String line;
		int i = 0;
		while ((line = this.bufferedReader.readLine()) != null) {
			primitive_array[i] = Short.parseShort(line);
			++i;
		}// while
	}

}
