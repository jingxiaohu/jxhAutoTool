package com.highbeauty.sql.spring.builder;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class AutoIncrement {
	public static String getAutoIncrement(ResultSetMetaData rsmd)
			throws SQLException {
		int count = rsmd.getColumnCount();
		for (int i = 1; i <= count; i++) {
			String columnName = rsmd.getColumnName(i);
			boolean ai = rsmd.isAutoIncrement(i);
			if (ai)
				return columnName;
		}

		return null;
	}
}
