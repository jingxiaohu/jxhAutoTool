package com.highbeauty.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({ "unchecked"})
public class ResultMap {

	// /////////////////////////////////////////////////////////////////

	/**
	 * ResultSet 
	 */

	public static Map<String,Object> newResultMap(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		return newResultMap(rs, rsmd);
	}

	public static Map<String,Object> newResultMap(ResultSet rs, ResultSetMetaData rsmd)
			throws SQLException {
		Map<String,Object> rMap = new HashMap<String,Object>();
		int count = rsmd.getColumnCount();
		for (int i = 1; i <= count; i++) {
			String key = rsmd.getColumnName(i);
			Object value = null;

			int columnType = rsmd.getColumnType(i);
			switch (columnType) {
			case java.sql.Types.ARRAY:
				value = rs.getArray(key);
				break;
			case java.sql.Types.BIGINT:
				value = rs.getLong(key);
				break;
			case java.sql.Types.BINARY:
				value = rs.getBytes(key);
				break;
			case java.sql.Types.BIT:
				value = rs.getBoolean(key);
				break;
			case java.sql.Types.BLOB:
				value = rs.getBlob(key);
				break;
			case java.sql.Types.BOOLEAN:
				value = rs.getBoolean(key);
				break;
			case java.sql.Types.CHAR:
				value = rs.getString(key);
				break;
			case java.sql.Types.CLOB:
				value = rs.getClob(key);
				break;
			case java.sql.Types.DATE:
				value = rs.getDate(key);
				break;
			case java.sql.Types.DECIMAL:
				value = rs.getBigDecimal(key);
				break;
			case java.sql.Types.DISTINCT:
				// ////////////////////
				break;
			case java.sql.Types.DOUBLE:
				value = rs.getDouble(key);
				break;
			case java.sql.Types.FLOAT:
				value = rs.getFloat(key);
				break;
			case java.sql.Types.INTEGER:
				value = rs.getInt(key);
				break;
			case java.sql.Types.JAVA_OBJECT:
				value = rs.getObject(key);
				break;
			case java.sql.Types.LONGVARCHAR:
				value = rs.getString(key);
				break;
			case java.sql.Types.LONGNVARCHAR:
				value = rs.getString(key);
				break;
			case java.sql.Types.LONGVARBINARY:
				value = rs.getBytes(key);
				break;
			case java.sql.Types.NCHAR:
				value = rs.getString(key);
				break;
			case java.sql.Types.NCLOB:
				value = rs.getNClob(key);
				break;
			case java.sql.Types.NULL:
				value = null;
				break;
			case java.sql.Types.NUMERIC:
				value = rs.getBigDecimal(key);
				break;
			case java.sql.Types.NVARCHAR:
				value = rs.getString(key);
				break;
			case java.sql.Types.OTHER:
				value = rs.getObject(key);
				break;
			case java.sql.Types.REAL:
				value = rs.getFloat(key);
				break;
			case java.sql.Types.REF:
				break;
			case java.sql.Types.ROWID:
				value = rs.getRowId(key);
				break;
			case java.sql.Types.SMALLINT:
				value = rs.getShort(key);
				break;
			case java.sql.Types.SQLXML:
				value = rs.getSQLXML(key);
				break;
			case java.sql.Types.STRUCT:
				break;
			case java.sql.Types.TIME:
				value = rs.getTime(key);
				break;
			case java.sql.Types.TIMESTAMP:
				value = rs.getTimestamp(key);
				break;
			case java.sql.Types.TINYINT:
				value = rs.getByte(key);
				break;
			case java.sql.Types.VARBINARY:
				value = rs.getBytes(key);
				break;
			case java.sql.Types.VARCHAR:
				value = rs.getString(key);
				break;
			default:
				break;
			}
			// if (value != null)
			rMap.put(key, value);
		}

		return rMap;
	}
}
