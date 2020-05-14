package com.highbeauty.util;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class MyLRUCache extends Hashtable {

	public MyLRUCache(int maxCacheSize) {
		super(maxCacheSize);
		this.maxCacheSize = Math.max(0, maxCacheSize);
	}

	@Override
	public void clear() {
		super.clear();
		list.clear();
	}

	public <T> T putAt(Object key, Object value) {
		return (T) put(key, value);
	}

	@Override
	public Object put(Object key, Object value) {
		if (maxCacheSize == 0)
			return null;
		if (!super.containsKey(key) && !list.isEmpty()
				&& list.size() + 1 > maxCacheSize) {
			Object deadKey = list.remove(list.size() - 1);
			super.remove(deadKey);
		}
		freshenKey(key);
		return super.put(key, value);
	}

	public <T> T getAt(Object key) {
		return (T) get(key);
	}

	@Override
	public Object get(Object key) {
		Object value = super.get(key);
		if (value != null)
			freshenKey(key);
		return value;
	}

	public <T> T removeAt(Object key) {
		return (T) remove(key);
	}

	@Override
	public Object remove(Object key) {
		list.remove(key);
		return super.remove(key);
	}

	private void freshenKey(Object key) {
		list.remove(key);
		list.add(0, key);
	}

	private final int maxCacheSize;
	private final List<Object> list = new Vector<Object>();
}
