package com.highbeauty.text;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class EasyTemplate {
	private class Cache {
		public byte[] b;
		public long lastModified;
	}

	public static final Map<String, Cache> caches = new Hashtable<String, Cache>();

	public static EasyTemplate _template = new EasyTemplate();

	public Cache newCache() {
		return new Cache();
	}

	public static final String make(File file, Map<String, String> params, String encode)
			throws Exception {
		byte[] b = readFully(file);
		String s = new String(b, encode);
		return make(s, params);
	}

	public static final String make2(File file, Map<String, String> params, String encode)
			throws Exception {
		String fname = file.getPath();

		byte[] b;
		if (caches.containsKey(fname)) {
			Cache c = caches.get(fname);
			if (c == null || c.lastModified < file.lastModified()) {
				c = _template.newCache();
				b = readFully(file);
				c.b = b;
				c.lastModified = file.lastModified();
				caches.put(fname, c);
			} else {
				b = c.b;
			}
		} else {
			Cache c = _template.newCache();
			b = readFully(file);
			c.b = b;
			c.lastModified = file.lastModified();
			caches.put(fname, c);
		}

		String s = new String(b, encode);
		return make(s, params);
	}

	public static final String make(String s, Map<String, String> params)
			throws Exception {
		Iterator<String> it = params.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String v = params.get(key);
			String k = String.format("${%s}", key);
			while (s.contains(k))
				s = s.replace(k, v);
		}
		return s;
	}

	public static final byte[] readFully(File f) throws Exception {
		if (f == null || !f.exists()) {
			throw new IOException("file no exists");
		}
		int len = (int) f.length();
		byte[] b = new byte[len];
		FileInputStream fis = new FileInputStream(f);
		DataInputStream dis = new DataInputStream(fis);
		dis.readFully(b);
		fis.close();

		return b;
	}

	public static final Map<String, String> newMap() {
		return new HashMap<String, String>();
	}

	public static void main(String[] args) throws Exception {
		String s = "C:/Java/WTK2.5.2_01/docs/api/midp/index.html";
		String str = EasyTemplate.make(new File(s), EasyTemplate.newMap(), "GBK");
		System.out.println(str);
		String str2 = EasyTemplate.make(new File(s), EasyTemplate.newMap(), "GBK");
		System.out.println(str2);
		String str3 = EasyTemplate.make(new File(s), EasyTemplate.newMap(), "GBK");
		System.out.println(str3);
	}
}
