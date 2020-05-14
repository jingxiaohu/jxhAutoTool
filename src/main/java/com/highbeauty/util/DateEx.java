package com.highbeauty.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.highbeauty.lang.NumEx;

public class DateEx {
	// 字母 日期或时间元素 表示 示例
	// ------------------------------------------------------------------
	// G Era 标志符 Text AD
	// y 年 Year 1996; 96
	// M 年中的月份 Month July; Jul; 07
	// w 年中的周数 Number 27
	// W 月份中的周数 Number 2
	// D 年中的天数 Number 189
	// d 月份中的天数 Number 10
	// F 月份中的星期 Number 2
	// E 星期中的天数 Text Tuesday; Tue
	// a Am/pm 标记 Text PM
	// H 一天中的小时数（0-23） Number 0
	// k 一天中的小时数（1-24） Number 24
	// K am/pm 中的小时数（0-11） Number 0
	// h am/pm 中的小时数（1-12） Number 12
	// m 小时中的分钟数 Number 30
	// s 分钟中的秒数 Number 55
	// S 毫秒数 Number 978
	// z 时区 General time zone Pacific Standard Time;
	// PST; GMT-08:00
	// Z 时区 RFC 822 time zone -0800

	public static final String fmt_yyyy_MM_dd_HH_mm_ss_sss = "yyyy-MM-dd HH:mm:ss.SSS";

	public static final String fmt_yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";

	public static final String fmt_yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";

	public static final String fmt_yyyy_MM_dd = "yyyy-MM-dd";

	public static final String fmt_HH_mm_ss = "HH:mm:ss";

	public static final String fmt_yyyy = "yyyy";

	public static final String fmt_MM = "MM";

	public static final String fmt_dd = "dd";

	public static final String fmt_HH = "HH";

	public static final String fmt_mm = "mm";

	public static final String fmt_ss = "ss";

	public static final String fmt_SSS = "SSS";

	public static final long TIME_MILLISECOND = 1;

	public static final long TIME_SECOND = 1000 * TIME_MILLISECOND;

	public static final long TIME_MINUTE = 60 * TIME_SECOND;

	public static final long TIME_HOUR = 60 * TIME_MINUTE;

	public static final long TIME_DAY = 24 * TIME_HOUR;

	public static final long TIME_WEEK = 7 * TIME_DAY;

	public static final long TIME_YEAR = 365 * TIME_DAY;

	public static long now() {
		return System.currentTimeMillis();
	}

	public static String now2() {
		return now(fmt_yyyy_MM_dd_HH_mm_ss);
	}

	public static String now3() {
		return now(fmt_yyyy_MM_dd);
	}

	public static String now(String fmt) {
		return formatString(new Date(), fmt);
	}

	public static Date parseDate(String v, String fmt) {
		Date dat = null;
		try {
			dat = new SimpleDateFormat(fmt).parse(v);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dat;
	}

	public static String formatString(Date v, String fmt) {
		SimpleDateFormat myFmt = new SimpleDateFormat(fmt);
		return myFmt.format(v);
	}

	public static int year() {
		return year(new Date());
	}

	public static int year(Date v) {
		SimpleDateFormat myFmt = new SimpleDateFormat(fmt_yyyy);
		String str = myFmt.format(v);
		return NumEx.stringToInt(str);
	}

	public static int month() {
		return month(new Date());
	}

	public static int month(Date v) {
		SimpleDateFormat myFmt = new SimpleDateFormat(fmt_MM);
		String str = myFmt.format(v);
		return NumEx.stringToInt(str);
	}

	public static int day() {
		return day(new Date());
	}

	public static int day(Date v) {
		SimpleDateFormat myFmt = new SimpleDateFormat(fmt_dd);
		String str = myFmt.format(v);
		return NumEx.stringToInt(str);
	}

	public static int hour() {
		return hour(new Date());
	}

	public static int hour(Date v) {
		SimpleDateFormat myFmt = new SimpleDateFormat(fmt_HH);
		String str = myFmt.format(v);
		return NumEx.stringToInt(str);
	}

	public static int minute() {
		return minute(new Date());
	}

	public static int minute(Date v) {
		SimpleDateFormat myFmt = new SimpleDateFormat(fmt_mm);
		String str = myFmt.format(v);
		return NumEx.stringToInt(str);
	}

	public static int second() {
		return second(new Date());
	}

	public static int second(Date v) {
		SimpleDateFormat myFmt = new SimpleDateFormat(fmt_ss);
		String str = myFmt.format(v);
		return NumEx.stringToInt(str);
	}

	public static int ms() {
		return ms(new Date());
	}

	public static int ms(Date v) {
		SimpleDateFormat myFmt = new SimpleDateFormat(fmt_SSS);
		String str = myFmt.format(v);
		return NumEx.stringToInt(str);
	}

	public static int week() {
		return week(new Date());
	}

	public static int week(Date v) {
		Calendar cd = Calendar.getInstance();
		cd.setTimeInMillis(v.getTime());
		int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
		cd = null;
		return dayOfWeek - 1;
	}

	public static int weekInYear(Date v) {
		SimpleDateFormat myFmt = new SimpleDateFormat("w");
		return NumEx.stringToInt(myFmt.format(v));
	}

	public static int weekInYear() {
		return weekInYear(new Date());
	}

	public static int weekInMonth(Date v) {
		SimpleDateFormat myFmt = new SimpleDateFormat("W");
		return NumEx.stringToInt(myFmt.format(v));
	}

	public static int weekInMonth() {
		return weekInMonth(new Date());
	}

	public static int dayInYear(Date v) {
		SimpleDateFormat myFmt = new SimpleDateFormat("D");
		return NumEx.stringToInt(myFmt.format(v));
	}

	public static int dayInYear() {
		return dayInYear(new Date());
	}

	public static long sub(Date d1, Date d2) {
		long l1 = d1.getTime();
		long l2 = d2.getTime();
		return l1 - l2;
	}
	
	public static String toString(long ms) {// 将毫秒数换算成x天x时x分x秒x毫秒
		int ss = 1000;
		int mi = ss * 60;
		int hh = mi * 60;
		int dd = hh * 24;

		long day = ms / dd;
		long hour = (ms - day * dd) / hh;
		long minute = (ms - day * dd - hour * hh) / mi;
		long second = (ms - day * dd - hour * hh - minute * mi) / ss;
		long milliSecond = ms - day * dd - hour * hh - minute * mi - second
				* ss;

		String strDay = day < 10 ? "0" + day : "" + day;
		String strHour = hour < 10 ? "0" + hour : "" + hour;
		String strMinute = minute < 10 ? "0" + minute : "" + minute;
		String strSecond = second < 10 ? "0" + second : "" + second;
		String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : ""
				+ milliSecond;
		strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : ""
				+ strMilliSecond;
		return strDay + " " + strHour + ":" + strMinute + ":" + strSecond + " "
				+ strMilliSecond;
	}
}
