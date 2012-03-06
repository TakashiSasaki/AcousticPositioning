package com.gmail.takashi316.acousticpositioning;

import java.io.File;
import java.io.FileNotFoundException;

import android.os.Environment;

public class Writer {
	private static File externalStorageDirectory;
	private static File dataDirectory;
	private static String DATA_DIRECTORY_NAME = "AcousticPositioning";

	static private Writer theWriter;

	static public Writer getInstance() throws FileNotFoundException {
		if (theWriter == null) {
			theWriter = new Writer();
		}
		return theWriter;
	}

	private Writer() throws FileNotFoundException {
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

}// Writer
