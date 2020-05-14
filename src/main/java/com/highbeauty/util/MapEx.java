package com.highbeauty.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.highbeauty.lang.NumEx;

@SuppressWarnings({ "unchecked"})
public class MapEx {
	public static Map newMap() {
		return new Hashtable();
	}

	public static Map newHashMap() {
		return new HashMap();
	}

	public static Hashtable newHashtable() {
		return new Hashtable();
	}

	public static ConcurrentHashMap newConcurrentHashMap() {
		return new ConcurrentHashMap();
	}

	public static <T> T copyValue(Map from, Map to, Object key){
		T v = get(from, key);
		if(v == null)
			return null;
		to.put(key, v);
		return v;
	}
	
	public static <T> T get(Map map, Object key) {
		return (T) map.get(key);
	}

	public static boolean getBoolean(Map map, Object key) {
		Boolean v = (Boolean) map.get(key);
		if(v == null)
			return false;
		return v.booleanValue();
	}

	public static byte getByte(Map map, Object key) {
		Byte v = (Byte) map.get(key);
		if(v == null)
			return 0;
		return v.byteValue();
	}

	public static short getShort(Map map, Object key) {
		Short v = (Short) map.get(key);
		if(v == null)
			return 0;
		return v.shortValue();
	}

	public static int getInt(Map map, Object key) {
		Integer v = (Integer) map.get(key);
		if(v == null)
			return 0;
		return v.intValue();
	}

	public static long getLong(Map map, Object key) {
		Long v = (Long) map.get(key);
		if(v == null)
			return 0;
		return v.longValue();
	}

	public static float getFloat(Map map, Object key) {
		Float v = (Float) map.get(key);
		if(v == null)
			return (float) 0.0;
		return v.floatValue();
	}

	public static double getDouble(Map map, Object key) {
		Double v = (Double) map.get(key);
		if(v == null)
			return 0.0;
		return v.doubleValue();
	}

	public static BigInteger getBigInteger(Map map, Object key) {
		return (BigInteger) map.get(key);
	}

	public static BigDecimal getBigDecimal(Map map, Object key) {
		return (BigDecimal) map.get(key);
	}

	public static String getString(Map map, Object key) {
		return (String) map.get(key);
	}

	public static Date getDate(Map map, Object key) {
		return (Date) map.get(key);
	}

	public static byte[] getByteArray(Map map, Object key) {
		return (byte[]) map.get(key);
	}

	public static Map toMap(List l){
		Map ret = newMap();
		if(l == null || l.isEmpty())
			return ret;
		for (int i = 0; i < l.size(); i++) {
			Object o = l.get(i);
			ret.put(i, o);
		}
		return ret;
	}
	
	public static Map toHashMap(Map map) {
		Map ret = newHashMap();
		Iterator it = map.keySet().iterator();
		while(it.hasNext()){
			Object key = it.next();
			Object var = map.get(key);
			ret.put(key, var);
		}
		return ret;
	}

	public static Map toHashtable(Map map) {
		Map ret = newHashtable();
		Iterator it = map.keySet().iterator();
		while(it.hasNext()){
			Object key = it.next();
			Object var = map.get(key);
			ret.put(key, var);
		}
		return ret;
	}
	
	public static Map toConcurrentHashMap(Map map){
		Map ret = newConcurrentHashMap();
		Iterator it = map.keySet().iterator();
		while(it.hasNext()){
			Object key = it.next();
			Object var = map.get(key);
			ret.put(key, var);
		}
		return ret;
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
	
	public static Map toMap(Object[] array) {
		if (array == null) {
			return null;
		}
		final Map map = new HashMap((int) (array.length * 1.5));
		for (int i = 0; i < array.length; i++) {
			Object object = array[i];
			if (object instanceof Map.Entry) {
				Map.Entry entry = (Map.Entry) object;
				map.put(entry.getKey(), entry.getValue());
			} else if (object instanceof Object[]) {
				Object[] entry = (Object[]) object;
				if (entry.length < 2) {
					throw new IllegalArgumentException("Array element " + i
							+ ", '" + object + "', has a length less than 2");
				}
				map.put(entry[0], entry[1]);
			} else {
				throw new IllegalArgumentException("Array element " + i + ", '"
						+ object
						+ "', is neither of type Map.Entry nor an Array");
			}
		}
		return map;
	}

	public static Map toHashMap(Object[] array) {
		Map m = toMap(array);
		return toHashMap(m);
	}

	public static Map toHashtable(Object[] array) {
		Map m = toMap(array);
		return toHashtable(m);
	}

	public static Map toConcurrentHashMap(Object[] array) {
		Map m = toMap(array);
		return toConcurrentHashMap(m);
	}

	public static Map propertiesToMap(String s){
		Properties p = new Properties();
		try {
			StringReader sr = new StringReader(s);
			BufferedReader br = new BufferedReader(sr);
			p.load(br);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return p;
	}

	// ///////////////////////////////////////////////////
	public static String getString(String smap, Object key){
		try {
			smap = smap == null ? "" : smap;
			Map m = propertiesToMap(smap);
			String var = (String) m.get(key);
			return var;
		} catch (Exception e) {
			return "";
		}
	}

	public static String[] getString(String smap, Object key, String split){
		String svar = getString(smap, key);
		return svar.split(split);
	}
	
	public static String setString(String smap, String key, String var){
		StringBuffer sb = new StringBuffer();
		try {
			smap = smap == null ? "" : smap;
			Map m = propertiesToMap(smap);
			m.put(key, var);
			
			int size = m.size();
			int p = 0;
			Iterator keys = m.keySet().iterator();
			while(keys.hasNext()){
				p ++;
				Object k = keys.next();
				Object v = m.get(k);
				sb.append(k).append("=").append(v);
				if(p < size)
					sb.append("\n");
			}
			return sb.toString();
		} catch (Exception e) {
			return "";
		}
	}

	public static String setString(String smap, String key, String[] vars, String split){
		StringBuffer sb = new StringBuffer();
		int length = vars.length;
		int p = 0;
		for (String v : vars) {
			p++;
			sb.append(v);
			if(p < length){
				sb.append(split);
			}
		}
		return setString(smap, key, sb.toString());
	}
	
	public static String setString(String smap, int key, String var){
		String skey = String.valueOf(key);
		return setString(smap, skey, var);
	}

	public static String setString(String smap, int key, String[] vars, String split){
		String skey = String.valueOf(key);
		return setString(smap, skey, vars, split);
	}

	public static int getInt(String smap, String key){
		String var = getString(smap, key);
		return NumEx.stringToInt(var);
	}
	
	public static int[] getInt(String smap, String key, String split){
		String var = getString(smap, key);
		String[] vars = var.split(split);
		int length = vars.length;
		if(length <= 0)
			return new int[0];
		int[] ret = new int[length];
		int p = 0;
		for (String string : vars) {
			int i = NumEx.stringToInt(string);
			ret[p] = i;
			p++;
		}
		return ret;
	}
	
	public static int getInt(String smap, int key){
		String skey = String.valueOf(key);
		return getInt(smap, skey);
	}

	public static int[] getInt(String smap, int key, String split){
		String skey = String.valueOf(key);
		return getInt(smap, skey, split);
	}
	
	public static String setInt(String smap, String key, int i){
		String var = String.valueOf(i);
		return setString(smap, key, var);
	}
	
	public static String setInt(String smap, String key, int[] is, String split){
		String[] vars = new String[is.length];
		int p = 0;
		for (int i : is) {
			String var = String.valueOf(i);
			vars[p] = var;
			p++;
		}
		return setString(smap, key, vars, split);
	}
	
	public static String setInt(String smap, int key, int i){
		String skey = String.valueOf(key);
		return setInt(smap, skey, i);
	}

	public static String setInt(String smap, int key, int[] is, String split){
		String skey = String.valueOf(key);
		return setInt(smap, skey, is, split);
	}
	
	public static final Iterator iterator(String smap){
		try {
			smap = smap == null ? "" : smap;
			Map m = propertiesToMap(smap);
			return m.keySet().iterator();
		} catch (Exception e) {
			return null;
		}
		
	}
	
	public static void main(String[] args) {
		Map colorMap = toMap(new Object[][] { { "RED", 0xFF0000 },
				{ "GREEN", 0x00FF00 }, { "BLUE", 0x0000FF } });

		System.out.println(colorMap);
		
		int[] vars = {1,2,3,4, 5,6};
		
		String s = "";
		s = setInt(s, "1", 111);
		s = setString(s, "2", "222");
		s = setString(s, 3, "333");
		s = setString(s, "4", "444");
		s = setInt(s, 5, 555);
		s = setInt(s, 6, vars, ",");
		System.out.println(s);
		
		System.out.println(getInt(s, "1"));
		System.out.println(getInt(s, "2"));
		System.out.println(getInt(s, 3));
		System.out.println(getInt(s, "4"));
		System.out.println(getInt(s, "5"));
		int[] vars2 = getInt(s, 6, ",");
		for (int i : vars2) {
			System.out.print(i + ",");
		}
		
		System.out.println("-------------------");
		Iterator it = iterator(s);
		while(it.hasNext()){
			String key = (String) it.next();
			String var = getString(s, key);
			System.out.println(key + " = " + var);
		}
		
	}
}
