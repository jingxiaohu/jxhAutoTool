package com.highbeauty.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class LRUListMap extends Hashtable {
	private final List<Object> list = new Vector<Object>();
	private final int maxCacheSize;

	public LRUListMap(int maxCacheSize) {
		super(maxCacheSize);
		this.maxCacheSize = Math.max(0, maxCacheSize);
	}

	@Override
	public void clear() {
		super.clear();
		list.clear();
	}

	public <T> T insert(int index, Object key, Object value) {
		if (maxCacheSize == 0)
			return null;
		if (!super.containsKey(key) && !list.isEmpty()
				&& list.size() + 1 > maxCacheSize) {
			Object deadKey = list.remove(list.size() - 1);
			super.remove(deadKey);
		}
		freshenKey(index, key);
		return (T) super.put(key, value);
	}

	public <T> T putAt(Object key, Object value) {
		return (T) put(key, value);
	}

	public <T> T put(int index, Object key, Object value) {
		return insert(list.size(), key, value);
	}

	public <T> T getAt(Object key) {
		return (T) get(key);
	}

	public <T> T getAt(int index) {
		Object key = list.get(index);
		return (T) get(key);
	}

	@Override
	public Object get(Object key) {
		Object value = super.get(key);
		if (value != null)
			freshenKey(list.size(), key);
		return value;
	}

	public <T> T removeAt(Object key) {
		return (T) remove(key);
	}

	public <T> T removeAt(int index) {
		Object key = list.get(index);
		return (T) remove(key);
	}

	@Override
	public Object remove(Object key) {
		list.remove(key);
		return super.remove(key);
	}

	private void freshenKey(int index, Object key) {
		list.remove(key);
		list.add(index, key);
	}

	public void sort(Comparator cmp) {
		Collections.sort(list, cmp);
	}

	@Override
	public int size(){
		return list.size();
	}
	
	public int size2(){
		return this.size();
	}	
}
