package com.gmail.takashi316.acousticpositioning;

public class Log {
	static public void v(String message) {
		Throwable t = new Throwable();
		StackTraceElement top_of_stack = t.getStackTrace()[1];
		String atline = getAtLine(top_of_stack);
		String method_name = top_of_stack.getMethodName();
		android.util.Log.v(method_name, atline);
		android.util.Log.v(method_name, "   " + message);
	}// v

	static public void d(String message) {
		Throwable t = new Throwable();
		StackTraceElement top_of_stack = t.getStackTrace()[1];
		String atline = getAtLine(top_of_stack);
		String method_name = top_of_stack.getMethodName();
		android.util.Log.d(method_name, atline);
		android.util.Log.d(method_name, "   " + message);
	}// d

	static public void e(String message) {
		Throwable t = new Throwable();
		StackTraceElement top_of_stack = t.getStackTrace()[1];
		String atline = getAtLine(top_of_stack);
		String method_name = top_of_stack.getMethodName();
		android.util.Log.e(method_name, atline);
		android.util.Log.e(method_name, "   " + message);
	}// e

	static private String getAtLine(StackTraceElement top_of_stack) {
		String file_name = top_of_stack.getFileName();
		int line_numer = top_of_stack.getLineNumber();
		String class_name = top_of_stack.getClassName();
		String method_name = top_of_stack.getMethodName();
		String atline = "at " + class_name + "." + method_name + "("
				+ file_name + ":" + line_numer + ")";
		return atline;
	}// getAtLine

	static public void e(String message, Exception e) {
		Throwable t = new Throwable();
		StackTraceElement top_of_stack = t.getStackTrace()[0];
		String file_name = top_of_stack.getFileName();
		int line_numer = top_of_stack.getLineNumber();
		String class_name = top_of_stack.getClassName();
		String method_name = top_of_stack.getMethodName();
		String atline = "at " + class_name + "." + method_name + "("
				+ file_name + ":" + line_numer + ")";
		android.util.Log.v(method_name, atline);
		android.util.Log.v(method_name, "   " + message);
		e.printStackTrace();
	}// e
}// Log
