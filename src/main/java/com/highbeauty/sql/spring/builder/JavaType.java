package com.highbeauty.sql.spring.builder;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;

public class JavaType {

	public static String getType(ResultSetMetaData rsmd, String columnName)
			throws SQLException {
		int count = rsmd.getColumnCount();
		for (int i = 1; i <= count; i++) {
			String key = rsmd.getColumnName(i);
			if (!key.equals(columnName))
				continue;

			return getType(rsmd, i);
		}
		return "";
	}
	
	public static String getType(ResultSetMetaData rsmd, String columnName,boolean flag)
	throws SQLException {
		int count = rsmd.getColumnCount();
		for (int i = 1; i <= count; i++) {
			String key = rsmd.getColumnName(i);
			if (!key.equals(columnName))
				continue;
			if(flag){
				return getTypeOld(rsmd, i);
			}else{
				return getType(rsmd, i);	
			}
			
		}
		return "";
}
	
	public static String getTypeOld(ResultSetMetaData rsmd, int i) throws SQLException {
		int count = rsmd.getColumnCount();
		if (i > count)
			return "";

		int columnType = rsmd.getColumnType(i);
		switch (columnType) {
		case java.sql.Types.ARRAY:
			return Array.class.getSimpleName();
		case java.sql.Types.BIGINT:
			return Long.class.getSimpleName();
		case java.sql.Types.BINARY:
			return "byte[]";
		case java.sql.Types.BIT:
			return Boolean.class.getSimpleName();
		case java.sql.Types.BLOB:
			return Blob.class.getName();
		case java.sql.Types.BOOLEAN:
			return Boolean.class.getSimpleName();
		case java.sql.Types.CHAR:
			return String.class.getSimpleName();
		case java.sql.Types.CLOB:
			return Clob.class.getName();
		case java.sql.Types.DATE:
			return java.util.Date.class.getName();
		case java.sql.Types.DECIMAL:
			return BigDecimal.class.getName();
		case java.sql.Types.DISTINCT:
			break;
		case java.sql.Types.DOUBLE:
			return Double.class.getSimpleName();
		case java.sql.Types.FLOAT:
			return Float.class.getSimpleName();
		case java.sql.Types.INTEGER:
			return Integer.class.getSimpleName();
		case java.sql.Types.JAVA_OBJECT:
			return Object.class.getSimpleName();
		case java.sql.Types.LONGVARCHAR:
			return String.class.getSimpleName();
		case java.sql.Types.LONGNVARCHAR:
			return String.class.getSimpleName();
		case java.sql.Types.LONGVARBINARY:
			return "byte[]";
		case java.sql.Types.NCHAR:
			return String.class.getName();
		case java.sql.Types.NCLOB:
			return NClob.class.getName();
		case java.sql.Types.NULL:
			break;
		case java.sql.Types.NUMERIC:
			return BigDecimal.class.getName();
		case java.sql.Types.NVARCHAR:
			return String.class.getSimpleName();
		case java.sql.Types.OTHER:
			return Object.class.getSimpleName();
		case java.sql.Types.REAL:
			return Double.class.getSimpleName();
		case java.sql.Types.REF:
			break;
		case java.sql.Types.ROWID:
			return RowId.class.getName();
		case java.sql.Types.SMALLINT:
			return Short.class.getSimpleName();
		case java.sql.Types.SQLXML:
			return SQLXML.class.getName();
		case java.sql.Types.STRUCT:
			break;
		case java.sql.Types.TIME:
			return Time.class.getName();
		case java.sql.Types.TIMESTAMP:
			return java.util.Date.class.getName();
		case java.sql.Types.TINYINT:
			return Byte.class.getSimpleName();
		case java.sql.Types.VARBINARY:
			return "byte[]";
		case java.sql.Types.VARCHAR:
			return String.class.getSimpleName();
		default:
			break;
		}
		return "";
	}
	
	
	

	public static String getType(ResultSetMetaData rsmd, int i) throws SQLException {
		int count = rsmd.getColumnCount();
		if (i > count)
			return "";

		int columnType = rsmd.getColumnType(i);
		switch (columnType) {
		case java.sql.Types.ARRAY:
			return getBasicType(Array.class.getSimpleName());
		case java.sql.Types.BIGINT:
			return getBasicType(Long.class.getSimpleName());
		case java.sql.Types.BINARY:
			return getBasicType("byte[]");
		case java.sql.Types.BIT:
			return getBasicType(Boolean.class.getSimpleName());
		case java.sql.Types.BLOB:
			return getBasicType(Blob.class.getName());
		case java.sql.Types.BOOLEAN:
			return getBasicType(Boolean.class.getSimpleName());
		case java.sql.Types.CHAR:
			return getBasicType(String.class.getSimpleName());
		case java.sql.Types.CLOB:
			return getBasicType(Clob.class.getName());
		case java.sql.Types.DATE:
			return getBasicType(java.util.Date.class.getName());
		case java.sql.Types.DECIMAL:
			return getBasicType(BigDecimal.class.getName());
		case java.sql.Types.DISTINCT:
			break;
		case java.sql.Types.DOUBLE:
			return getBasicType(Double.class.getSimpleName());
		case java.sql.Types.FLOAT:
			return getBasicType(Float.class.getSimpleName());
		case java.sql.Types.INTEGER:
			return getBasicType(Integer.class.getSimpleName());
		case java.sql.Types.JAVA_OBJECT:
			return getBasicType(Object.class.getSimpleName());
		case java.sql.Types.LONGVARCHAR:
			return getBasicType(String.class.getSimpleName());
		case java.sql.Types.LONGNVARCHAR:
			return getBasicType(String.class.getSimpleName());
		case java.sql.Types.LONGVARBINARY:
			return getBasicType("byte[]");
		case java.sql.Types.NCHAR:
			return getBasicType(String.class.getName());
		case java.sql.Types.NCLOB:
			return getBasicType(NClob.class.getName());
		case java.sql.Types.NULL:
			break;
		case java.sql.Types.NUMERIC:
			return getBasicType(BigDecimal.class.getName());
		case java.sql.Types.NVARCHAR:
			return getBasicType(String.class.getSimpleName());
		case java.sql.Types.OTHER:
			return getBasicType(Object.class.getSimpleName());
		case java.sql.Types.REAL:
			return getBasicType(Double.class.getSimpleName());
		case java.sql.Types.REF:
			break;
		case java.sql.Types.ROWID:
			return getBasicType(RowId.class.getName());
		case java.sql.Types.SMALLINT:
			return getBasicType(Short.class.getSimpleName());
		case java.sql.Types.SQLXML:
			return getBasicType(SQLXML.class.getName());
		case java.sql.Types.STRUCT:
			break;
		case java.sql.Types.TIME:
			return getBasicType(Time.class.getName());
		case java.sql.Types.TIMESTAMP:
			return getBasicType(java.util.Date.class.getName());
		case java.sql.Types.TINYINT:
			return getBasicType(Byte.class.getSimpleName());
		case java.sql.Types.VARBINARY:
			return getBasicType("byte[]");
		case java.sql.Types.VARCHAR:
			return getBasicType(String.class.getSimpleName());
		default:
			break;
		}
		return "";
	}
	public static void main(String[] args) {
		System.out.println(Byte.class.getSimpleName());
	}
	public static String getBasicType(String type){
		if (type.equals("Boolean"))
			type = "boolean";
		if (type.equals("Byte"))
			type = "byte";
		else if (type.equals("Short"))
			type = "short";
		else if (type.equals("Integer"))
			type = "int";
		else if (type.equals("Long"))
			type = "long";
		else if (type.equals("Float"))
			type = "float";
		else if (type.equals("Double"))
			type = "double";

		return type;
	}
	
}
