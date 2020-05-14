package com.highbeauty.lang;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ByteEx {
	private static final char[] DIGIT = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static char[] encodeHex(byte[] b) {
		int l = b.length;
		char[] out = new char[l << 1];
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = DIGIT[(0xF0 & b[i]) >>> 4];
			out[j++] = DIGIT[0x0F & b[i]];
		}
		return out;
	}

	private static int toDigit(char ch, int index) throws IOException {
		int digit = Character.digit(ch, 16);
		if (digit == -1)
			throw new IOException("Illegal hexadecimal charcter " + ch
					+ " at index " + index);
		return digit;
	}

	private static byte[] decodeHex(char[] ch) throws IOException {
		int len = ch.length;
		if ((len & 0x01) != 0)
			throw new IOException("Odd number of characters.");
		byte[] out = new byte[len >> 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; j < len; i++) {
			int f = toDigit(ch[j], j) << 4;
			j++;
			f = f | toDigit(ch[j], j);
			j++;
			out[i] = (byte) (f & 0xFF);
		}
		return out;
	}

	public static byte[] stringToByte(String s) throws IOException {
		char[] c = s.toCharArray();
		return decodeHex(c);
	}

	public static String byteToString(byte[] b) {
		return new String(encodeHex(b));
	}

	public static String bytesToString(byte[] b) {
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			int v = b[i] & 0xff;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();
	}

	public static byte[] stringToBytes(String s) {
		byte[] b = new byte[s.length() / 2];
		for (int i = 0; i < b.length; i++) {
			int index = i * 2;
			int v = Integer.parseInt(s.substring(index, index + 2), 16);
			b[i] = (byte) v;
		}
		return b;
	}

	public static int indexOf(final byte[] b, final byte[] sp, int start) {
		if (b == null || sp == null)
			return -1;
		int len = b.length;
		return indexOf(b, sp, start, len);
	}

	public static int indexOf(final byte[] b, final byte[] sp, int start,
							  int end) {
		if (b == null || sp == null)
			return -1;
		int len = b.length;
		int size = sp.length;
		int count = 0;
		if (len <= 0 || size <= 0 || len < size)
			return -1;

		start = start < 0 ? 0 : start;
		end = end > len - size ? len - size : end;
		for (int i = start; i < end; i++) {
			count = 0;
			for (int j = 0; j < size; j++)
				if (b[i + j] == sp[j])
					count++;

			if (count >= size)
				return i;
		}

		return -1;
	}

	public static byte[] between(byte[] src, byte[] begin, byte[] end) {
		if (src == null || begin == null || end == null)
			return null;

		int b = -1, e = -1;
		b = indexOf(src, begin, -1);
		if (b < 0)
			return null;

		e = indexOf(src, end, b + begin.length);
		if (b < 0)
			return null;

		b = b + begin.length;

		byte[] result = new byte[e - b];
		System.arraycopy(src, b, result, 0, result.length);

		return result;
	}

	public static List split(byte[] b, byte[] spline) {
		List ret = new ArrayList();

		int old = 0;
		int pos = -1;

		while (true) {
			pos = indexOf(b, spline, old);
			if ((pos < 0) || (pos < old))
				break;

			if (pos == 0) {
				old = pos + spline.length;
				continue;
			}

			byte[] dest = new byte[pos - old];
			System.arraycopy(b, old, dest, 0, dest.length);
			ret.add(dest);

			old = pos + spline.length;
		}

		return ret;
	}

	public static int indexOf2(byte[] source, byte[] target, int fromIndex) {
		if (source == null || target == null || fromIndex < 0)
			return -1;
		int sourceOffset = 0;
		int sourceCount = source.length;
		int targetOffset = 0;
		int targetCount = target.length;
		if (fromIndex >= sourceCount) {
			return (targetCount == 0 ? sourceCount : -1);
		}
		if (fromIndex < 0) {
			fromIndex = 0;
		}
		if (targetCount == 0) {
			return fromIndex;
		}

		byte first = target[targetOffset];
		int max = sourceOffset + (sourceCount - targetCount);

		for (int i = sourceOffset + fromIndex; i <= max; i++) {
			/* Look for first character. */
			if (source[i] != first) {
				while (++i <= max && source[i] != first)
					;
			}

			/* Found first character, now look at the rest of v2 */
			if (i <= max) {
				int j = i + 1;
				int end = j + targetCount - 1;
				for (int k = targetOffset + 1; j < end
						&& source[j] == target[k]; j++, k++)
					;

				if (j == end) {
					/* Found whole string. */
					return i - sourceOffset;
				}
			}
		}
		return -1;
	}

	public static int lastIndexOf(byte[] source, byte[] target, int fromIndex) {
		/*
		 * Check arguments; return immediately where possible. For consistency,
		 * don't check for null str.
		 */
		int sourceOffset = 0;
		int sourceCount = source.length;
		int targetOffset = 0;
		int targetCount = target.length;
		int rightIndex = sourceCount - targetCount;
		if (fromIndex < 0) {
			return -1;
		}
		if (fromIndex > rightIndex) {
			fromIndex = rightIndex;
		}
		/* Empty string always matches. */
		if (targetCount == 0) {
			return fromIndex;
		}

		int strLastIndex = targetOffset + targetCount - 1;
		byte strLastChar = target[strLastIndex];
		int min = sourceOffset + targetCount - 1;
		int i = min + fromIndex;

		startSearchForLastChar: while (true) {
			while (i >= min && source[i] != strLastChar) {
				i--;
			}
			if (i < min) {
				return -1;
			}
			int j = i - 1;
			int start = j - (targetCount - 1);
			int k = strLastIndex - 1;

			while (j > start) {
				if (source[j--] != target[k--]) {
					i--;
					continue startSearchForLastChar;
				}
			}
			return start - sourceOffset + 1;
		}
	}

	public static boolean equals(final byte[] b1, final byte[] b2) {
		int n = b1.length;
		if (n == b2.length) {
			int i = 0;
			int j = 0;
			while (n-- != 0) {
				if (b1[i++] != b2[j++])
					return false;
			}
			return true;
		}
		return false;
	}

	public static boolean isEmpty(final byte[] b) {
		return (b == null || b.length == 0);
	}

	public static byte[] subBytes(byte[] b, int begin, int length) {
		if (b == null || b.length <= 0 || begin <= 0 || length <= 0
				|| b.length < begin || b.length < begin + length)
			return null;

		byte[] r = new byte[length];
		System.arraycopy(b, begin, r, 0, length);
		return r;
	}

	public static byte[] replace(byte[] b, byte old, byte newb) {
		if (old != newb) {
			int len = b.length;
			int i = -1;
			byte[] val = b; /* avoid getfield opcode */
			int off = 0; /* avoid getfield opcode */

			while (++i < len) {
				if (val[off + i] == old) {
					break;
				}
			}
			if (i < len) {
				byte buf[] = new byte[len];
				for (int j = 0; j < i; j++) {
					buf[j] = val[off + j];
				}
				while (i < len) {
					byte c = val[off + i];
					buf[i] = (c == old) ? newb : c;
					i++;
				}
				return buf;
			}
		}
		return b;
	}

	public static boolean getBoolean(byte[] b, int off) {
		return b[off] != 0;
	}

	public static char getChar(byte[] b, int off) {
		return (char) (((b[off + 1] & 0xFF) << 0) + ((b[off + 0]) << 8));
	}

	public static short getShort(byte[] b, int off) {
		return (short) (((b[off + 1] & 0xFF) << 0) + ((b[off + 0]) << 8));
	}

	public static int getInt(byte[] b, int off) {
		return ((b[off + 3] & 0xFF) << 0) + ((b[off + 2] & 0xFF) << 8)
				+ ((b[off + 1] & 0xFF) << 16) + ((b[off + 0]) << 24);
	}

	public static float getFloat(byte[] b, int off) {
		int i = ((b[off + 3] & 0xFF) << 0) + ((b[off + 2] & 0xFF) << 8)
				+ ((b[off + 1] & 0xFF) << 16) + ((b[off + 0]) << 24);
		return Float.intBitsToFloat(i);
	}

	public static long getLong(byte[] b, int off) {
		return ((b[off + 7] & 0xFFL) << 0) + ((b[off + 6] & 0xFFL) << 8)
				+ ((b[off + 5] & 0xFFL) << 16) + ((b[off + 4] & 0xFFL) << 24)
				+ ((b[off + 3] & 0xFFL) << 32) + ((b[off + 2] & 0xFFL) << 40)
				+ ((b[off + 1] & 0xFFL) << 48) + (((long) b[off + 0]) << 56);
	}

	public static double getDouble(byte[] b, int off) {
		long j = ((b[off + 7] & 0xFFL) << 0) + ((b[off + 6] & 0xFFL) << 8)
				+ ((b[off + 5] & 0xFFL) << 16) + ((b[off + 4] & 0xFFL) << 24)
				+ ((b[off + 3] & 0xFFL) << 32) + ((b[off + 2] & 0xFFL) << 40)
				+ ((b[off + 1] & 0xFFL) << 48) + (((long) b[off + 0]) << 56);
		return Double.longBitsToDouble(j);
	}

	/*
	 * Methods for packing primitive values into byte arrays starting at given
	 * offsets.
	 */

	public static void putBoolean(byte[] b, int off, boolean val) {
		b[off] = (byte) (val ? 1 : 0);
	}

	public static byte[] putBoolean(boolean val) {
		byte[] b = new byte[1];
		putBoolean(b, 0, val);
		return b;
	}

	public static void putChar(byte[] b, int off, char val) {
		b[off + 1] = (byte) (val >>> 0);
		b[off + 0] = (byte) (val >>> 8);
	}

	public static byte[] putChar(char val) {
		byte[] b = new byte[2];
		putChar(b, 0, val);
		return b;
	}

	public static void putShort(byte[] b, int off, short val) {
		b[off + 1] = (byte) (val >>> 0);
		b[off + 0] = (byte) (val >>> 8);
	}

	public static byte[] putShort(short val) {
		byte[] b = new byte[2];
		putShort(b, 0, val);
		return b;
	}

	public static void putInt(byte[] b, int off, int val) {
		b[off + 3] = (byte) (val >>> 0);
		b[off + 2] = (byte) (val >>> 8);
		b[off + 1] = (byte) (val >>> 16);
		b[off + 0] = (byte) (val >>> 24);
	}

	public static byte[] putInt(int val) {
		byte[] b = new byte[4];
		putInt(b, 0, val);
		return b;
	}

	public static void putFloat(byte[] b, int off, float val) {
		int i = Float.floatToIntBits(val);
		b[off + 3] = (byte) (i >>> 0);
		b[off + 2] = (byte) (i >>> 8);
		b[off + 1] = (byte) (i >>> 16);
		b[off + 0] = (byte) (i >>> 24);
	}

	public static byte[] putFloat(float val) {
		byte[] b = new byte[4];
		putFloat(b, 0, val);
		return b;
	}

	public static void putLong(byte[] b, int off, long val) {
		b[off + 7] = (byte) (val >>> 0);
		b[off + 6] = (byte) (val >>> 8);
		b[off + 5] = (byte) (val >>> 16);
		b[off + 4] = (byte) (val >>> 24);
		b[off + 3] = (byte) (val >>> 32);
		b[off + 2] = (byte) (val >>> 40);
		b[off + 1] = (byte) (val >>> 48);
		b[off + 0] = (byte) (val >>> 56);
	}

	public static byte[] putLong(long val) {
		byte[] b = new byte[8];
		putLong(b, 0, val);
		return b;
	}

	public static void putDouble(byte[] b, int off, double val) {
		long j = Double.doubleToLongBits(val);
		b[off + 7] = (byte) (j >>> 0);
		b[off + 6] = (byte) (j >>> 8);
		b[off + 5] = (byte) (j >>> 16);
		b[off + 4] = (byte) (j >>> 24);
		b[off + 3] = (byte) (j >>> 32);
		b[off + 2] = (byte) (j >>> 40);
		b[off + 1] = (byte) (j >>> 48);
		b[off + 0] = (byte) (j >>> 56);
	}

	public static byte[] putDouble(double val) {
		byte[] b = new byte[8];
		putDouble(b, 0, val);
		return b;
	}

	public static byte[] append(final byte[] b1, final byte[] b2) {
		return append(b1, b2, 0, b2.length);
	}

	public static byte[] append(final byte[] b1, final byte[] b2, int offset,
								int length) {
		if (b1 == null || b2 == null)
			return null;
		if (b2.length <= 0 || b2.length <= offset
				|| b2.length < offset + length)
			return b1;
		byte[] r = new byte[b1.length + length];
		System.arraycopy(b1, 0, r, 0, b1.length);
		System.arraycopy(b2, offset, r, b1.length, length);
		return r;
	}

	public static byte[] appendBoolean(final byte[] b1, final boolean val) {
		byte[] b2 = putBoolean(val);
		return append(b1, b2);
	}

	public static byte[] appendByte(final byte[] b1, final byte val) {
		byte[] b2 = new byte[1];
		b2[0] = val;
		return append(b1, b2);
	}

	public static byte[] appendChar(final byte[] b1, final char val) {
		byte[] b2 = putChar(val);
		return append(b1, b2);
	}

	public static byte[] appendShort(final byte[] b1, final short val) {
		byte[] b2 = putShort(val);
		return append(b1, b2);
	}

	public static byte[] appendInt(final byte[] b1, final int val) {
		byte[] b2 = putInt(val);
		return append(b1, b2);
	}

	public static byte[] appendFloat(final byte[] b1, final float val) {
		byte[] b2 = putFloat(val);
		return append(b1, b2);
	}

	public static byte[] appendLong(final byte[] b1, final long val) {
		byte[] b2 = putLong(val);
		return append(b1, b2);
	}

	public static byte[] appendDouble(final byte[] b1, final double val) {
		byte[] b2 = putDouble(val);
		return append(b1, b2);
	}

	public static void main(String[] args) {
		byte[] b = "你好，xxx".getBytes();
		String s = bytesToString(b);
		System.out.println(s);

		byte[] b2 = stringToBytes(s);
		System.out.println(new String(b2));

	}
}
