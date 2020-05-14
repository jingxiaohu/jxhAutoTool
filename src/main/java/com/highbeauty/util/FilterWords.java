package com.highbeauty.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class FilterWords {
	public Vector<Map<String, String>> words = new Vector<Map<String, String>>();

	private String getKey(Map<String, String> m) {
		return m.get("key");
	}

	private String getReplace(Map<String, String> m) {
		return m.get("replace");
	}

	public FilterWords(List<Map<String, String>> words) {
		Vector<Map<String, String>> ws = new Vector<Map<String,String>>();
		ws.addAll(words);
		this.words = ws;
	}

	public FilterWords(Vector<Map<String, String>> words) {
		this.words = words;
	}

	public boolean check(String s) {
		Iterator<Map<String, String>> it = words.iterator();
		while (it.hasNext()) {
			Map<String, String> m = it.next();
			String key = getKey(m);
			if (s.indexOf(key) >= 0) {
				return false;
			}
		}
		return true;
	}

	public String process(String s) {
		Iterator<Map<String, String>> it = words.iterator();
		while (it.hasNext()) {
			Map<String, String> m = it.next();
			String key = getKey(m);
			String r = getReplace(m);
			s = s.replaceAll(key, r);
		}
		return s;
	}
}
