package com.gmail.takashi316.acousticpositioning;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComplexNumber {
	public int index;
	public double real;
	public double imaginary;
	final static String COMPLEX_NUMBER_PATTERN = "^([-+]?[0-9]+[.]?[0-9]*E?[+-]?[0-9]*)([-+][0-9]+[.]?[0-9]*E?[-+]?[0-9]*)i$";

	public ComplexNumber(double real, double imaginary) {
		this.real = real;
		this.imaginary = imaginary;
	}

	public ComplexNumber(String s) {
		try {
			this.real = Double.parseDouble(s);
			this.imaginary = 0;
		} catch (NumberFormatException e) {
			Pattern pattern = Pattern.compile(COMPLEX_NUMBER_PATTERN);
			Matcher matcher = pattern.matcher(s);
			if (!matcher.find()) {
				throw new NumberFormatException(s + " doesn't match "
						+ COMPLEX_NUMBER_PATTERN);
			}// if
			this.real = Double.parseDouble(matcher.group(1));
			this.imaginary = Double.parseDouble(matcher.group(2));
		}// try
	}// ComplexNumber

	@Override
	public String toString() {
		if (this.imaginary == 0.0d) {
			return "" + this.real;
		}
		if (this.imaginary < 0.0d) {
			return "" + this.real + "-" + Math.abs(this.imaginary) + "i" + "("
					+ index + ")";
		}
		return "" + this.real + "+" + this.imaginary + "i" + "(" + index + ")";
	}// toString

	public static void main(String[] args) {
		ComplexNumber cn1 = new ComplexNumber("123.456");
		System.out.println(cn1);
		ComplexNumber cn2 = new ComplexNumber("123.456+11.22i");
		System.out.println(cn2);
		ComplexNumber cn3 = new ComplexNumber("123.456-11.22i");
		System.out.println(cn3);
		ComplexNumber cn4 = new ComplexNumber("-123.456+33.44i");
		System.out.println(cn4);
		ComplexNumber cn5 = new ComplexNumber("-123.456-33.44i");
		System.out.println(cn5);
		System.out.println("completed.");
	}

	static public class ComplexNumberComparator implements
			Comparator<ComplexNumber> {
		private boolean reverse;

		public ComplexNumberComparator(boolean reverse) {
			this.reverse = reverse;
		}

		@Override
		public int compare(ComplexNumber o1, ComplexNumber o2) {
			final double power1 = o1.real * o1.real + o1.imaginary
					* o1.imaginary;
			assert (power1 >= 0);
			final double power2 = o2.real * o2.real + o2.imaginary
					* o2.imaginary;
			assert (power2 >= 0);
			int result;
			if (power1 > power2) {
				result = 1;
			} else if (power1 < power2) {
				result = -1;
			} else {
				result = 0;
			}
			if (this.reverse == true) {
				return -1 * result;
			} else {
				return result;
			}
		}// compare
	}// ComplexNumberComparator
}// ComplexNumber
