package com.highbeauty.lang;

import java.io.IOException;


public class ByteWriter {
	private static final int INCREMENTAL = 8 * 1024;
	private byte[] buff = new byte[INCREMENTAL];
	private int postion = 0;

	public void put(byte b) {
		if (postion + 1 > buff.length) {
			byte[] temp = new byte[buff.length + INCREMENTAL];
			System.arraycopy(buff, 0, temp, 0, buff.length);
			buff = temp;
		}
		buff[postion] = b;
		postion++;
	}

	public void put(byte[] b) {
		for (int n = 0; n < b.length; n++)
			put(b[n]);
	}

	public void put(ByteWriter bb) {
		byte[] b = bb.toByteArray();
		put(b);
	}

	public void set(byte b, int offset) throws IOException {
		if (offset + 1 >= buff.length)
			throw new IOException("offset > limit");

		buff[offset] = b;
	}

	public void set(byte[] b, int offset) throws IOException {
		for (int n = 0; n < b.length; n++)
			set(b[n], offset + n);
	}

	public void set(ByteWriter bb, int offset) throws IOException {
		byte[] b = bb.toByteArray();
		set(b, offset);
	}

	public int length() {
		return postion;
	}

	public void clear() {
		postion = 0;
	}

	public byte[] toByteArray() {
		byte[] r = new byte[postion];
		System.arraycopy(buff, 0, r, 0, postion);
		return r;
	}

	public void putBoolean(boolean v) {
		put(ByteEx.putBoolean(v));
	}

	public void putByte(byte v) {
		put(v);
	}

	public void putChar(char v) {
		put(ByteEx.putChar(v));
	}

	public void putShort(short v) {
		put(ByteEx.putShort(v));
	}

	public void putInt(int v) {
		put(ByteEx.putInt(v));
	}

	public void putFloat(float v) {
		put(ByteEx.putFloat(v));
	}

	public void putLong(long v) {
		put(ByteEx.putLong(v));
	}

	public void putDouble(double v) {
		put(ByteEx.putDouble(v));
	}

	public void setBoolean(boolean v, int offset) throws IOException {
		set(ByteEx.putBoolean(v), offset);
	}

	public void setByte(byte v, int offset) throws IOException {
		set(v, offset);
	}

	public void setChar(char v, int offset) throws IOException {
		set(ByteEx.putChar(v), offset);
	}

	public void setShort(short v, int offset) throws IOException {
		set(ByteEx.putShort(v), offset);
	}

	public void setInt(int v, int offset) throws IOException {
		set(ByteEx.putInt(v), offset);
	}

	public void setFloat(float v, int offset) throws IOException {
		set(ByteEx.putFloat(v), offset);
	}

	public void setLong(long v, int offset) throws IOException {
		set(ByteEx.putLong(v), offset);
	}

	public void setDouble(double v, int offset) throws IOException {
		set(ByteEx.putDouble(v), offset);
	}

}
