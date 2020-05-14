package com.highbeauty.util;

import java.util.Hashtable;

import com.highbeauty.lang.ThreadEx;

@SuppressWarnings("serial")
public class TimerEmptyMap extends Hashtable<Object, Object> implements
		Runnable {
	private boolean isRun = false;
	private long millis = 60 * 60 * 1000;

	public TimerEmptyMap() {
		go();
	}

	public TimerEmptyMap(long timeout) {
		this.millis = timeout;
		go();
	}

	@SuppressWarnings("unchecked")
	public <T> T getAt(Object key) {
		return (T) this.get(key);
	}

	public void go() {
		isRun = true;
		ThreadEx.execute(this);
	}

	public void stop() {
		isRun = false;
	}

	public void run() {
		try {
			while (isRun) {
				Thread.sleep(millis);
				// System.out.println("v");
				this.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// public static void main(String[] args) throws Exception {
	// TimerEmptyMap t = new TimerEmptyMap(1000);
	// Thread.sleep(10 * 1000);
	// t.stop();
	// Thread.sleep(10 * 1000);
	// t.go();
	// Thread.sleep(10 * 1000);
	// }
}
