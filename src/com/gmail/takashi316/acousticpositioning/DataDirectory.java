package com.gmail.takashi316.acousticpositioning;

import java.io.File;
import java.io.FileNotFoundException;
import android.os.Environment;

public class DataDirectory {
	private static File externalStorageDirectory;
	private static File dataDirectory;
	private static String DATA_DIRECTORY_NAME = "AcousticPositioning";

	public DataDirectory() throws FileNotFoundException {
		if (dataDirectory != null)
			return;
		externalStorageDirectory = Environment.getExternalStorageDirectory();
		dataDirectory = new File(externalStorageDirectory, DATA_DIRECTORY_NAME);
		if (!dataDirectory.exists()) {
			dataDirectory.mkdir();
		}// if
		if (!dataDirectory.exists() || !dataDirectory.isDirectory()) {
			throw new FileNotFoundException("Can't create "
					+ dataDirectory.getAbsolutePath());
		}// if
	}// a constructor

	public static File getFile(String file_name) {
		return new File(dataDirectory, file_name);
	}// getFile

}// DataDirectory
