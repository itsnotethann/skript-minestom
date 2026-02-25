package com.github.hapily04.skriptminestom.util;

public class ArrayUtils {

	public static int[] toIntArray(Object[] boxed) {
		if (boxed == null) return null;
		int[] primitive = new int[boxed.length];
		for (int i = 0; i < boxed.length; i++) primitive[i] = (Integer) boxed[i]; // auto-unboxing
		return primitive;
	}

	public static long[] toLongArray(Object[] boxed) {
		if (boxed == null) return null;
		long[] primitive = new long[boxed.length];
		for (int i = 0; i < boxed.length; i++) primitive[i] = (Long) boxed[i];
		return primitive;
	}

	public static byte[] toByteArray(Object[] boxed) {
		if (boxed == null) return null;
		byte[] primitive = new byte[boxed.length];
		for (int i = 0; i < boxed.length; i++) primitive[i] = (Byte) boxed[i];
		return primitive;
	}

	public static double[] toDoubleArray(Object[] boxed) {
		if (boxed == null) return null;
		double[] primitive = new double[boxed.length];
		for (int i = 0; i < boxed.length; i++) primitive[i] = (Double) boxed[i];
		return primitive;
	}

	public static float[] toFloatArray(Object[] boxed) {
		if (boxed == null) return null;
		float[] primitive = new float[boxed.length];
		for (int i = 0; i < boxed.length; i++) primitive[i] = (Float) boxed[i];
		return primitive;
	}

	public static short[] toShortArray(Object[] boxed) {
		if (boxed == null) return null;
		short[] primitive = new short[boxed.length];
		for (int i = 0; i < boxed.length; i++) primitive[i] = (Short) boxed[i];
		return primitive;
	}

	public static boolean[] toBooleanArray(Object[] boxed) {
		if (boxed == null) return null;
		boolean[] primitive = new boolean[boxed.length];
		for (int i = 0; i < boxed.length; i++) primitive[i] = (Boolean) boxed[i];
		return primitive;
	}

	public static char[] toCharArray(Object[] boxed) {
		if (boxed == null) return null;
		char[] primitive = new char[boxed.length];
		for (int i = 0; i < boxed.length; i++) primitive[i] = (Character) boxed[i];
		return primitive;
	}

	public static Integer[] toIntegerArray(int[] primitive) {
		if (primitive == null) return null;
		Integer[] boxed = new Integer[primitive.length];
		for (int i = 0; i < primitive.length; i++) {
			boxed[i] = primitive[i]; // auto-boxing
		}
		return boxed;
	}

	public static Long[] toLongArray(long[] primitive) {
		if (primitive == null) return null;
		Long[] boxed = new Long[primitive.length];
		for (int i = 0; i < primitive.length; i++) {
			boxed[i] = primitive[i];
		}
		return boxed;
	}

	public static Byte[] toByteArray(byte[] primitive) {
		if (primitive == null) return null;
		Byte[] boxed = new Byte[primitive.length];
		for (int i = 0; i < primitive.length; i++) {
			boxed[i] = primitive[i];
		}
		return boxed;
	}

	public static Double[] toDoubleArray(double[] primitive) {
		if (primitive == null) return null;
		Double[] boxed = new Double[primitive.length];
		for (int i = 0; i < primitive.length; i++) {
			boxed[i] = primitive[i];
		}
		return boxed;
	}

	public static Float[] toFloatArray(float[] primitive) {
		if (primitive == null) return null;
		Float[] boxed = new Float[primitive.length];
		for (int i = 0; i < primitive.length; i++) {
			boxed[i] = primitive[i];
		}
		return boxed;
	}

	public static Short[] toShortArray(short[] primitive) {
		if (primitive == null) return null;
		Short[] boxed = new Short[primitive.length];
		for (int i = 0; i < primitive.length; i++) {
			boxed[i] = primitive[i];
		}
		return boxed;
	}

	public static Boolean[] toBooleanArray(boolean[] primitive) {
		if (primitive == null) return null;
		Boolean[] boxed = new Boolean[primitive.length];
		for (int i = 0; i < primitive.length; i++) {
			boxed[i] = primitive[i];
		}
		return boxed;
	}

	public static Character[] toCharacterArray(char[] primitive) {
		if (primitive == null) return null;
		Character[] boxed = new Character[primitive.length];
		for (int i = 0; i < primitive.length; i++) {
			boxed[i] = primitive[i];
		}
		return boxed;
	}

}
