package com.highbeauty.util;

public class ProfileTimer {
	public void start() {
		start_ = System.currentTimeMillis();
	}

	public void stop() {
		stop_ = System.currentTimeMillis();
	}

	public long elapsed() {
		return stop_ - start_;
	}

	public long stopElapsed() {
		stop();
		return stop_ - start_;
	}

	private long start_;
	private long stop_;
}
