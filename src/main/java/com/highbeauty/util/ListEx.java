package com.highbeauty.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import com.highbeauty.lang.NumEx;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ListEx {
	private static Random rnd;
	static {
		rnd = new Random(System.currentTimeMillis());
	}

	public static final List newList(){
		return new Vector();
	}
	
	public static final List newArrayList() {
		return new ArrayList();
	}

	public static final List newLinkedList() {
		return new LinkedList();
	}

	public static final List newVector() {
		return new Vector();
	}

	public static final <T> T get(List list, int index) {
		return (T) list.get(index);
	}

	public static final List toList(String s) {
		List l = new Vector();
		StringReader sr = new StringReader(s);
		BufferedReader br = new BufferedReader(sr);
		try {
			while (true) {
				String v = br.readLine();
				if (v == null)
					break;
				l.add(v);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return l;
	}

	public static final List toArrayList(List list) {
		List ret = newArrayList();
		for (Object e : list) {
			ret.add(e);
		}
		return ret;
	}

	public static final List toLinkedList(List list) {
		List ret = newLinkedList();
		for (Object e : list) {
			ret.add(e);
		}
		return ret;
	}

	public static final List toVector(List list) {
		List ret = newVector();
		for (Object e : list) {
			ret.add(e);
		}
		return ret;
	}

	public static final List toArrayList(Object[] array) {
		if (array == null)
			return null;
		List list = newArrayList();
		for (Object e : array)
			list.add(e);
		return list;
	}

	public static final List toLinkedList(Object[] array) {
		if (array == null)
			return null;
		List list = newLinkedList();
		for (Object e : array)
			list.add(e);
		return list;
	}

	public static List keyToList(Map map){
		List list = ListEx.newList();
		list.addAll(map.keySet());
		return list;
	}

	public static List valueToList(Map map){
		List list = ListEx.newList();
		list.addAll(map.values());
		return list;
	}
	
	public static final List toVector(Object[] array) {
		if (array == null)
			return null;
		List list = newVector();
		for (Object e : array)
			list.add(e);
		return list;
	}

	public static final List copy(List src) {
		List dest = newVector();
		Collections.copy(dest, src);
		return dest;
	}

	public static final List reverse(List src) {
		Collections.reverse(src);
		return src;
	}

	public static final List rotate(List src, int distance) {
		Collections.rotate(src, distance);
		return src;
	}

	public static final List shuffle(List src) {
		Collections.shuffle(src);
		return src;
	}

	public static final List shuffleRnd(List src) {
		Collections.shuffle(src, rnd);
		return src;
	}

	public static final List sort(List src) {
		Collections.sort(src);
		return src;
	}

	public static final List sort2(List src, Comparator comparator) {
		Collections.sort(src, comparator);
		return src;
	}

	public static final List<Map> sortIntMap(List<Map> m1, final Object key) {
        Collections.sort(m1, new Comparator<Map>(){
            public int compare(Map o1, Map o2) {
                int i1 = (Integer)o1.get(key);
                int i2 = (Integer)o2.get(key);
                return i1 - i2;
            }
        });
		return m1;
	}

	public static final List<Map> sortLongMap(List<Map> m1, final Object key) {
        Collections.sort(m1, new Comparator<Map>(){
            public int compare(Map o1, Map o2) {
                long i1 = (Long)o1.get(key);
                long i2 = (Long)o2.get(key);
                return i1 > i2 ? 1 : -1;
                
            }
        });
		return m1;
	}

	public static final List<Byte> distinctByte(List<Byte> vars) {
		List<Byte> ret = new Vector<Byte>();
		Map<Byte, Byte> mvars = new Hashtable<Byte, Byte>();
		for (Byte v : vars) {
			mvars.put(v, v);
		}
		ret.addAll(mvars.values());
		return ret;
	}

	public static final List<Short> distinctShort(List<Short> vars) {
		List<Short> ret = new Vector<Short>();
		Map<Short, Short> mvars = new Hashtable<Short, Short>();
		for (Short v : vars) {
			mvars.put(v, v);
		}
		ret.addAll(mvars.values());
		return ret;
	}

	public static final List<Integer> distinctInteger(List<Integer> vars) {
		List<Integer> ret = new Vector<Integer>();
		Map<Integer, Integer> mvars = new Hashtable<Integer, Integer>();
		for (Integer v : vars) {
			mvars.put(v, v);
		}
		ret.addAll(mvars.values());
		return ret;
	}

	public static final List<Long> distinctLong(List<Long> vars) {
		List<Long> ret = new Vector<Long>();
		Map<Long, Long> mvars = new Hashtable<Long, Long>();
		for (Long v : vars) {
			mvars.put(v, v);
		}
		ret.addAll(mvars.values());
		return ret;
	}

	public static final List<String> distinctString(List<String> vars) {
		List<String> ret = new Vector<String>();
		Map<String, String> mvars = new Hashtable<String, String>();
		for (String v : vars) {
			mvars.put(v, v);
		}
		ret.addAll(mvars.values());
		return ret;
	}
	
	
	////////////////////////////////////////
	public static final String add(String list, String v, String split){
		list = list == null ? "" : list;
		if(v == null || v.trim().isEmpty())
			return list;
		v = v.trim();
		
		StringBuffer sb = new StringBuffer();
		String[] lists = list.split(split);
		
		for (String s : lists) {
			s = s.trim();
			if(s.isEmpty())
				continue;
			sb.append(s);
			sb.append(split);
		}
		sb.append(v);
		sb.append(split);
		return sb.toString();
	}

	public static final String add(String list, int n, String split){
		String v = String.valueOf(n);
		return add(list, v, split);
	}
	public static final String set(String list, int i, String v, String split){
		list = list == null ? "" : list;
		if(v == null || v.trim().isEmpty())
			return list;
		if(i <= 0)
			return list;

		v = v.trim();
		StringBuffer sb = new StringBuffer();
		String[] lists = list.split(split);
		
		int length = lists.length;
		if(i >= length)
			return list;

		int p = 0;
		for (String s : lists) {
			p ++;
			if(p == i){
				s = v;
			}
			s = s.trim();
			if(s.isEmpty())
				continue;
			sb.append(s);
			sb.append(split);
		}
		return sb.toString();
	}

	public static final String set(String list, int i, int n, String split){
		String v = String.valueOf(n);
		return set(list, i, v, split);
	}
	
	public static final String insert(String list, int i, String v, String split){
		list = list == null ? "" : list;
		if(v == null || v.trim().isEmpty())
			return list;
		v = v.trim();
		StringBuffer sb = new StringBuffer();
		String[] lists = list.split(split);
		int length = lists.length;

		if(i <= 0){ //插入到前面
			sb.append(v);
			sb.append(split);
			for (String s : lists) {
				s = s.trim();
				if(s.isEmpty())
					continue;
				sb.append(s);
				sb.append(split);
			}
		}else if (i >= length){ // 添加到后面
			return add(list, v, split);
		}else{ // 添加到中间
			int p = 0;
			for (String s : lists) {
				s = s.trim();
				if(s.isEmpty())
					continue;
				sb.append(s);
				sb.append(split);
				p ++;
				if(p == i){
					sb.append(v);
					sb.append(split);
				}
			}
		}
		return sb.toString();
	}

	public static final String insert(String list, int i, int n, String split){
		String v = String.valueOf(n);
		return insert(list, i, v, split);
	}
	
	public static final String remove(String list, int i, String split){
		list = list == null ? "" : list;
		if(i <= 0)
			return list;

		StringBuffer sb = new StringBuffer();
		String[] lists = list.split(split);
		
		int length = lists.length;
		if(i >= length)
			return list;

		int p = 0;
		for (String s : lists) {
			p ++;
			if(p == i){
				continue;
			}
			s = s.trim();
			if(s.isEmpty())
				continue;
			sb.append(s);
			sb.append(split);
		}
		return sb.toString();
	}
	
	public static final String getString(String list, int i, String split){
		list = list == null ? "" : list;
		if(i <= 0)
			return "";
		String[] lists = list.split(split);
		if(i >= lists.length)
			return "";
		return lists[i];
	}

	public static final int getInt(String list, int i, String split){
		String v = getString(list, i, split);
		return NumEx.stringToInt(v);
	}

	public static final int indexOf(String list, String v, String split){
		list = list == null ? "" : list;
		String[] lists = list.split(split);
		int p = 0;
		for (String s : lists) {
			if(s.equals(v))
				return p;
			p ++;
		}
		return -1;
	}

	public static final int indexOf(String list, int n, String split){
		String v = String.valueOf(n);
		return indexOf(list, v, split);
	}
	
	public static final Iterator iterator(String list, String split){
		List l = newList();
		list = list == null ? "" : list;
		String[] lists = list.split(split);
		for (String s : lists) {
			l.add(s);
		}
		return l.iterator();
	}
	////////////////////////////////////////////////////
	
	public static void main(String[] args) {
		String list = "";
		list = add(list, "111", ",");
		list = add(list, "222", ",");
		list = add(list, "333", ",");
		list = add(list, "555", ",");
		list = remove(list, 2, ",");
		list = set(list, 3, "aaa", ",");
		list = insert(list, 1, "000", ",");
		
		System.out.println(list);
		
		int n1 = getInt(list, 2, ",");
		System.out.println(n1);
		System.out.println(indexOf(list, 111, ","));
		
		System.out.println("------------------");
		
		Iterator it = iterator(list, ",");
		while(it.hasNext()){
			System.out.println(it.next());
		}
	}
}
