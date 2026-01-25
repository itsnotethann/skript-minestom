package com.github.hapily04.skriptminestom.util;

import java.util.regex.Pattern;

public class NumberUtils {

	private static final Pattern INTEGER_PATTERN = Pattern.compile("\\d+");

	public static boolean isInteger(String input) {
		return INTEGER_PATTERN.matcher(input).matches();
	}

	public static boolean isOnlyDigits(String input) {
		for (char c : input.toCharArray()) {
			if (!Character.isDigit(c)) return false;
		}
		return true;
	}

}
