package com.highbeauty.util;

public class ArrayEx {
	public static final String toString(boolean[] v){
		StringBuffer sb = new StringBuffer();
		int len = v.length;
		int i = 0;
		for (boolean b : v) {
			i ++;
			sb.append(b);
			if(i < len)
				sb.append(",");
		}
		return sb.toString();
	}

	public static final String toString(byte[] v){
		StringBuffer sb = new StringBuffer();
		int len = v.length;
		int i = 0;
		for (byte b : v) {
			i ++;
			sb.append(b);
			if(i < len)
				sb.append(",");
		}
		return sb.toString();
	}

	public static final String toString(short[] v){
		StringBuffer sb = new StringBuffer();
		int len = v.length;
		int i = 0;
		for (short b : v) {
			i ++;
			sb.append(b);
			if(i < len)
				sb.append(",");
		}
		return sb.toString();
	}

	public static final String toString(int[] v){
		StringBuffer sb = new StringBuffer();
		int len = v.length;
		int i = 0;
		for (int b : v) {
			i ++;
			sb.append(b);
			if(i < len)
				sb.append(",");
		}
		return sb.toString();
	}

	public static final String toString(long[] v){
		StringBuffer sb = new StringBuffer();
		int len = v.length;
		int i = 0;
		for (long b : v) {
			i ++;
			sb.append(b);
			if(i < len)
				sb.append(",");
		}
		return sb.toString();
	}

	public static final String toString(float[] v){
		StringBuffer sb = new StringBuffer();
		int len = v.length;
		int i = 0;
		for (float b : v) {
			i ++;
			sb.append(b);
			if(i < len)
				sb.append(",");
		}
		return sb.toString();
	}

	public static final String toString(double[] v){
		StringBuffer sb = new StringBuffer();
		int len = v.length;
		int i = 0;
		for (double b : v) {
			i ++;
			sb.append(b);
			if(i < len)
				sb.append(",");
		}
		return sb.toString();
	}

	public static final String toString(String[] v){
		StringBuffer sb = new StringBuffer();
		int len = v.length;
		int i = 0;
		for (String b : v) {
			i ++;
			sb.append(b);
			if(i < len)
				sb.append(",");
		}
		return sb.toString();
	}

}
