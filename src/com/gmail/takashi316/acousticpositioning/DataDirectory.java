package com.gmail.takashi316.acousticpositioning;

import java.io.File;
import java.io.FileNotFoundException;
import android.os.Environment;

public class DataDirectory {
	private static File externalStorageDirectory;
	protected static File dataDirectory;
	private static String DATA_DIRECTORY_NAME = "AcousticPositioning";

	protected DataDirectory()
			throws FileNotFoundException {
		externalStorageDirectory = Environment.getExternalStorageDirectory();
		dataDirectory = new File(externalStorageDirectory, DATA_DIRECTORY_NAME);
		if (!dataDirectory.exists()) {
			dataDirectory.mkdir();
		}// if
		if (!dataDirectory.exists() || !dataDirectory.isDirectory()) {
			throw new FileNotFoundException("Can't create "
					+ dataDirectory.getAbsolutePath());
		}// if
	}// FileThread
	
}// FileThread
