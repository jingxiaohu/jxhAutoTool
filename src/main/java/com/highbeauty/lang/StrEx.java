package com.highbeauty.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class StrEx {
	public static String fix6Str(String s) {
		return String.format("%6s", s);
	}

	public static String left(String s, int len) {
		return s.substring(0, len);
	}

	public static String right(String s, int len) {
		int length = s.length();
		return s.substring(length - len, length);
	}

	public static String mid(String s, int begin, int end){
		return s.substring(begin, end);
	}
	
	public static boolean isByte(String s) {
		try {
			Byte.parseByte(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isShort(String s) {
		try {
			Short.parseShort(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isLong(String s) {
		try {
			Long.parseLong(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isFloat(String s) {
		try {
			Float.parseFloat(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isDouble(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static String f(String s, Object... args){
		return String.format(s, args);
	}
	
	public static String upperFirst(String s) {
		int len = s.length();
		if (len <= 0)
			return "";

		StringBuffer sb = new StringBuffer();
		sb.append(s.substring(0, 1).toUpperCase());
		sb.append(s.substring(1, len));

		return sb.toString();
	}

	public static String package2Path(String pkg){
		return pkg.replaceAll("\\.", "/");
	}
	
	public static String mapToString(Map<?, ?> m) {
		StringBuffer sb = new StringBuffer();
		Iterator<?> it = m.keySet().iterator();
		while (it.hasNext()) {
			Object k = it.next();
			Object v = m.get(k);
//			String key = String.valueOf(k);
//			String var = String.valueOf(v);
			sb.append(k).append("=").append(v).append("\n");
		}
		return sb.toString();
	}

	public static String toString(List<?> l) {
		StringBuffer sb = new StringBuffer();
		Iterator<?> it = l.iterator();
		while (it.hasNext()) {
			Object v = it.next();
			String var = String.valueOf(v);
			sb.append(var).append("\n");
		}
		return sb.toString();
	}

	public static byte[] toByteArray(String s, String charset)
			throws UnsupportedEncodingException {
		return s.getBytes(charset);
	}

	public static String createString(byte[] b, String charset)
			throws UnsupportedEncodingException {
		return new String(b, charset);
	}

	public static List<String> toLines(String s) throws IOException {
		List<String> ret = new Vector<String>();
		StringReader sr = new StringReader(s);
		BufferedReader br = new BufferedReader(sr);
		while (true) {
			String line = br.readLine();
			if (line == null)
				break;
			ret.add(line);
		}
		return ret;
	}

	// 全角字符
	public static String toW(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == ' ') {
				c[i] = '\u3000';
			} else if (c[i] < '\177') {
				c[i] = (char) (c[i] + 65248);
			}
		}
		return new String(c);
	}

	// 半角字符
	public static String toC(String input){
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == '\u3000') {
				c[i] = ' ';
			} else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
				c[i] = (char) (c[i] - 65248);
			}
		}
		return new String(c);
	}
	

	public static void main(String[] args){
		String s = "abcef中文E文 空格 Space!";
		String s2 = toW(s);
		System.out.println(s2);
		String s3 = toC(s2);
		System.out.println(s3);
	}
}
