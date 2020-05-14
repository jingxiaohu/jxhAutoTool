package com.highbeauty.log;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.Date;

import com.highbeauty.util.AppUtils;
import com.highbeauty.util.DateEx;

public class Logger {
	public static PrintStream out = System.out;

	public static void log(Object o) {
		out.println(o);
	}

	private static File file = null;
	private static FileWriter writer = null;

	public static void write(Integer i) {
		write(Integer.toString(i));
	}

	public static void write(String s) {
		try {
			if (file == null) {
				String approot = AppUtils.getAppRoot();
				String dat = DateEx.formatString(new Date(), "yyyyMMdd_HHmmss");
				String logfile = String
						.format("%s/debug_%s.log", approot, dat);
				file = new File(logfile);
				writer = new FileWriter(file);
			}
			if (writer != null) {
				String dat = DateEx.formatString(new Date(), DateEx.fmt_yyyy_MM_dd_HH_mm_ss);
				writer.write(dat);
				writer.write(":\r\n");
				writer.write(s);
				writer.write("\r\n");
				writer.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		Logger.write(123);
		Logger.write("abcd");
	}

}
