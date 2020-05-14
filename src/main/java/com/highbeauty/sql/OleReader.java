package com.highbeauty.sql;

import com.highbeauty.lang.ByteEx;

public class OleReader {
	// public static void main(String[] args) throws IOException {
	// String f = "D:/3.ole.gif";
	// byte[] b = FileEx.readFully(f);
	// System.out.println(f);
	// System.out.println(System.currentTimeMillis());
	// System.out.println(isOle(b));
	// if (isJpg(b)) {
	// System.out.println("isJpg");
	// byte[] b2 = decodeJpg(b);
	// FileEx.write("d:/01.jpg", b2);
	// }
	// if (isPng(b)) {
	// System.out.println("isPng");
	// byte[] b2 = decodePng(b);
	// FileEx.write("d:/02.png", b2);
	// }
	// if (isGif(b)) {
	// System.out.println("isGif");
	// byte[] b2 = decodeGif(b);
	// FileEx.write("d:/03.gif", b2);
	// }
	// System.out.println(isGif(b));
	// System.out.println(System.currentTimeMillis());
	// }

	public static boolean isOle(byte[] b) {
		if (b == null || b.length < 128)
			return false;

		byte[] sp = { 0x15, 0x1C, 0x1F, 0x00, 0x02, 0x00, 0x00, 0x00, 0x03,
				0x00, 0x08, 0x00, 0x14, 0x00, 0x17, 0x00, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xB0,
				(byte) 0xFC, 0x00, 0x50, 0x61, 0x63, 0x6B, 0x61, 0x67, 0x65 };
		int p = ByteEx.indexOf(b, sp, 0, sp.length);
		return p >= 0;
	}

	public static boolean isJpg(byte[] b) {
		if (b == null || b.length < 128)
			return false;
		byte[] sp = { 0x2E, 0x6A, 0x70, 0x67, 0x00 };
		int p = ByteEx.indexOf(b, sp, 64, 128);
		return p >= 0;
	}

	public static boolean isPng(byte[] b) {
		if (b == null || b.length < 128)
			return false;
		byte[] sp = { 0x2E, 0x70, 0x6E, 0x67, 0x00 };
		int p = ByteEx.indexOf(b, sp, 64, 128);
		return p >= 0;
	}

	public static boolean isGif(byte[] b) {
		if (b == null || b.length < 128)
			return false;
		byte[] sp = { 0x2E, 0x67, 0x69, 0x66, 0x00 };
		int p = ByteEx.indexOf(b, sp, 64, 128);
		return p >= 0;
	}

	public static String getExt(byte[] b) {
		if (isOle(b)) {
			if (isJpg(b))
				return "jpg";

			if (isPng(b))
				return "png";
			if (isGif(b))
				return "gif";
		}
		return "";
	}

	public static byte[] decodeOle(byte[] b) {
		if (!isOle(b))
			return null;
		if (isJpg(b))
			return decodeJpg(b);
		if (isPng(b))
			return decodePng(b);
		if (isGif(b))
			return decodeGif(b);
		return null;
	}

	public static byte[] decodeJpg(byte[] b) {
		if (b == null || b.length < 128)
			return null;
		if (!isJpg(b))
			return null;
		byte[] begin = { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0,
				0x00, 0x10, 0x4A, 0x46, 0x49, 0x46 };
		byte[] end = { (byte) 0xFF, (byte) 0xD9 };

		int len = b.length;
		int max = len > 160 ? 160 : len;
		int p1 = ByteEx.indexOf(b, begin, 64, max);
		if (p1 < 0)
			return null;
		int m2 = len - 1024;
		m2 = m2 <= 0 ? 0 : m2;
		int p2 = ByteEx.indexOf(b, end, m2);
		if (p2 < 0 || p2 > b.length)
			return null;
		int length = p2 - p1 + end.length;
		if (length <= 0)
			return null;

		byte[] r = new byte[length];
		System.arraycopy(b, p1, r, 0, length);
		return r;
	}

	public static byte[] decodePng(byte[] b) {
		if (b == null || b.length < 128)
			return null;
		if (!isPng(b))
			return null;
		byte[] begin = { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
				0x00 };
		byte[] end = { 0x49, 0x45, 0x4E, 0x44, (byte) 0xAE, 0x42, 0x60,
				(byte) 0x82 };

		int len = b.length;
		int max = len > 160 ? 160 : len;
		int p1 = ByteEx.indexOf(b, begin, 64, max);
		if (p1 < 0)
			return null;
		int m2 = len - 1024;
		m2 = m2 <= 0 ? 0 : m2;
		int p2 = ByteEx.indexOf(b, end, m2);
		if (p2 < 0 || p2 > b.length)
			return null;
		int length = p2 - p1 + end.length;
		if (length <= 0)
			return null;

		byte[] r = new byte[length];
		System.arraycopy(b, p1, r, 0, length);
		return r;
	}

	public static byte[] decodeGif(byte[] b) {
		if (b == null || b.length < 128)
			return null;
		if (!isGif(b))
			return null;
		byte[] begin = { 0x47, 0x49, 0x46, 0x38, 0x39, 0x61 };
		byte[] end = { 0x00, 0x3B };

		int len = b.length;
		int max = len > 160 ? 160 : len;
		int p1 = ByteEx.indexOf(b, begin, 64, max);
		if (p1 < 0)
			return null;
		int m2 = len - 1024;
		m2 = m2 <= 0 ? 0 : m2;
		int p2 = ByteEx.indexOf(b, end, m2);
		if (p2 < 0 || p2 > b.length)
			return null;
		int length = p2 - p1 + end.length;
		if (length <= 0)
			return null;

		byte[] r = new byte[length];
		System.arraycopy(b, p1, r, 0, length);
		return r;
	}
}
