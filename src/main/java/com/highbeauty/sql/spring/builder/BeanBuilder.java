package com.highbeauty.sql.spring.builder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.highbeauty.pinyin.PinYin;

public class BeanBuilder {

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws Exception {
/*
		String sql = "SELECT * FROM `����` LIMIT 1";
		String host = "192.168.2.241";
		String db = "fych";
		Connection conn = SqlEx.newMysqlConnection(host, db);
		
		ResultSet rs = SqlEx.executeQuery(conn, sql);

		BeanBuilder builder = new BeanBuilder();
		String xml = builder.build(rs, "co.test.bean", true);
		System.out.println(xml);*/
	}

	public String build(ResultSet rs, String pkg, boolean gs,Map<String,String> map)
			throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		String tableName = rsmd.getTableName(1);

		Map<String, String> mExist = new Hashtable<String, String>();

		StringBuffer sb = new StringBuffer();
		if (pkg != null && pkg.length() > 0) {
			sb.append("package " + pkg + ";");
			sb.append("\r\n");
			sb.append("\r\n");
		}
		sb.append("import java.io.*;");
		sb.append("\r\n");
		sb.append("import java.util.*;");
		sb.append("\r\n");
		sb.append("\r\n");

		sb.append("//" + tableName + "\r\n");
//		sb.append("@SuppressWarnings({\"rawtypes\",  \"unchecked\", \"serial\" })");
		sb.append("@SuppressWarnings({\"serial\"})");
		sb.append("\r\n");
		sb.append("public class ");
		sb.append(StrEx.upperFirst(PinYin.getShortPinYin(tableName)));
		sb.append(" implements Cloneable , Serializable{\r\n");
		sb.append("\r\n");

		String fieldArrays = getFieldArrayString(rsmd, "id");
		sb.append("    //public static String[] carrays ="+fieldArrays + ";\r\n");
		sb.append("\r\n");

		int count = rsmd.getColumnCount();
		for (int i = 1; i <= count; i++) {
			String columnName = rsmd.getColumnName(i);
			String javaType = JavaType.getType(rsmd, i);
			sb.append("    public ");
			sb.append(javaType);
			sb.append(" ");
			sb.append(columnName);
			if(javaType.contains("String")){
				sb.append("=\"\"");
			}
			if(javaType.contains("Date")){
				sb.append("=new java.util.Date()");
			}
			sb.append(";");
			sb.append(map.get(columnName));
			sb.append("\r\n");
		}

		sb.append("\r\n");
		//sb.append("    public Map extension = new HashMap();");
		sb.append("\r\n");

//		if (gs)
//			for (int i = 1; i <= count; i++) {
//				String columnName = rsmd.getColumnName(i);
//				String javaType = JavaType.getType(rsmd, i);
//
//				String mName = StrEx.upperFirst(columnName);
//				if (!mExist.containsKey(mName)) {
//				sb.append("\r\n");
//				sb.append("    ");
//				sb.append("public ");
//				sb.append(javaType);
//				sb.append(" ");
//				sb.append("get");
//					mExist.put(mName, mName);
//					sb.append(mName);
//					sb.append("(");
//					sb.append("){");
//					sb.append("\r\n");
//					sb.append("    ");
//					sb.append("    ");
//					sb.append("return ");
//					sb.append(columnName);
//					sb.append(";");
//					sb.append("\r\n");
//					sb.append("    }\r\n");
//
//					sb.append("\r\n");
//					sb.append("    ");
//					sb.append("public void");
//					// sb.append(javaType);
//					sb.append(" ");
//					sb.append("set");
//					sb.append(mName);
//					sb.append("(" + javaType + " value");
//					sb.append("){");
//					sb.append("\r\n");
//					sb.append("    ");
//					sb.append("    ");
//					sb.append("this.");
//					sb.append(columnName);
//					sb.append("= value;");
//					sb.append("\r\n");
//					sb.append("    }\r\n");
//				}
//			}
//
//		sb.append("\r\n");

		if (gs)
			for (int i = 1; i <= count; i++) {
				String columnName = rsmd.getColumnName(i);
				String javaType = JavaType.getType(rsmd, i);

				String mName = StrEx.upperFirst(PinYin
						.getShortPinYin(columnName));
				if (!mExist.containsKey(mName)) {
					sb.append("\r\n");
					sb.append("    ");
					sb.append("public ");
					sb.append(javaType);
					sb.append(" ");
					sb.append("get");
					mExist.put(mName, mName);
					sb.append(mName);
					sb.append("(");
					sb.append("){");
					sb.append("\r\n");
					sb.append("    ");
					sb.append("    ");
					sb.append("return ");
					sb.append(columnName);
					sb.append(";");
					sb.append("\r\n");
					sb.append("    }\r\n");

					sb.append("\r\n");
					sb.append("    ");
					sb.append("public void");
					// sb.append(javaType);
					sb.append(" ");
					sb.append("set");
					sb.append(mName);
					sb.append("(" + javaType + " value");
					sb.append("){");
					sb.append("\r\n");
					if(javaType.contains("String")){
						sb.append("    	if(value == null){");
						sb.append("\r\n");
						sb.append("           value = \"\";");
						sb.append("\r\n");
						sb.append("        }\r\n");
					}
					if(javaType.contains("Date")){
						sb.append("    	if(value == null){");
						sb.append("\r\n");
						sb.append("           value = new java.util.Date();");
						sb.append("\r\n");
						sb.append("        }\r\n");
					}
					sb.append("        ");
					sb.append("this.");
					sb.append(columnName);
					sb.append("= value;");
					sb.append("\r\n");
					sb.append("    }\r\n");
				}
			}

		sb.append("\r\n");
		sb.append("\r\n");
		// ///////////////////////////////////
		String clazzName = StrEx.upperFirst(PinYin.getShortPinYin(tableName));
		sb.append("\r\n");
		sb.append("    ");
		sb.append("public static ");
		sb.append(clazzName);
		sb.append(" new");
		sb.append(clazzName);
		sb.append("(");
		for (int i = 1; i <= count; i++) {
			String columnName = rsmd.getColumnName(i);
			String javaType = JavaType.getType(rsmd, i);

			sb.append(javaType);
			sb.append(" ");
			sb.append(PinYin.getShortPinYin(columnName));
			if (i + 1 <= count)
				sb.append(", ");
		}
		sb.append(") {");
		sb.append("\r\n");
		sb.append("        ");
		sb.append(clazzName);
		sb.append(" ret = new ");
		sb.append(clazzName);
		sb.append("();\r\n");
		sb.append("    ");
		for (int i = 1; i <= count; i++) {
			String columnName = rsmd.getColumnName(i);
			sb.append("    ret.set");
			sb.append(StrEx.upperFirst(columnName));
			sb.append("(" + PinYin.getShortPinYin(columnName) + ");");
			sb.append("\r\n");
			sb.append("    ");
		}
		sb.append("    return ret;");
		sb.append("    \r\n");

		sb.append("    }");
		// ///////////////////////////////////
		// assignment 
		//String clazzName = StrEx.upperFirst(PinYin.getShortPinYin(tableName));
		sb.append("\r\n");
		sb.append("\r\n");
		String shortclazzName = PinYin.getShortPinYin(clazzName);
		sb.append("    public void assignment").append("(").append(clazzName).append(" "+shortclazzName.toLowerCase()+") {\r\n");
		for (int i = 1; i <= count; i++) {
			String columnName = rsmd.getColumnName(i);
			String shortcolumnName = PinYin.getShortPinYin(columnName);
			String javaType = JavaType.getType(rsmd, i);

			sb.append("        ").append(javaType).append(" ").append(shortcolumnName);
			sb.append(" = ");
			// sb.append(StrEx.upperFirst(tableName) );
			sb.append("");
			sb.append(clazzName.toLowerCase());
			sb.append(".get");
			sb.append(StrEx.upperFirst(columnName));
			sb.append("();");
			sb.append("\r\n");
		}
		sb.append("\r\n");
		
		for (int i = 1; i <= count; i++) {
			String columnName = rsmd.getColumnName(i);
			sb.append("        this.set");
			sb.append(StrEx.upperFirst(columnName));
			sb.append("(" + PinYin.getShortPinYin(columnName) + ");");
			sb.append("\r\n");
		}
		sb.append("\r\n");
		sb.append("    }\r\n");
		// ///////////////////////////////////		
		sb.append("\r\n");
		sb.append("    ");
		sb.append("@SuppressWarnings(\"unused\")");
		sb.append("\r\n");
		sb.append("    ");
		sb.append("public static void ");
		sb.append("get");
		sb.append(StrEx.upperFirst(clazzName));
		sb.append("(");
		sb.append(StrEx.upperFirst(clazzName));
		sb.append(" ");
		sb.append(clazzName.toLowerCase());
		sb.append(" ){");
		sb.append("\r\n");
		sb.append("    ");
		for (int i = 1; i <= count; i++) {
			String columnName = rsmd.getColumnName(i);
			String javaType = JavaType.getType(rsmd, i);

			sb.append("    ");
			sb.append(javaType);
			sb.append(" ");
			sb.append(PinYin.getShortPinYin(columnName));
			sb.append(" = ");
			// sb.append(StrEx.upperFirst(tableName) );
			sb.append("");
			sb.append(clazzName.toLowerCase());
			sb.append(".get");
			sb.append(StrEx.upperFirst(columnName));
			sb.append("();");
			sb.append("\r\n");
			sb.append("    ");
		}

		sb.append("}");
		sb.append("\r\n");
		// ///////////////////////////////////
		sb.append("\r\n");
		sb.append("    public Map<String,Object> toMap(){\r\n");
		sb.append("        return toEnMap(this);\r\n");
		sb.append("    }\r\n");
		// ///////////////////////////////////
		sb.append("\r\n");
		sb.append("    ");
		sb.append("public static Map<String,Object> ");
		sb.append("toEnMap");
		sb.append("(");
		sb.append(clazzName);
		sb.append(" ");
		sb.append(clazzName.toLowerCase());
		sb.append(")");
		sb.append("{");
		sb.append("\r\n");
		sb.append("    ");
		for (int i = 1; i <= count; i++) {
			String columnName = rsmd.getColumnName(i);
			String javaType = JavaType.getType(rsmd, i);

			sb.append("    ");
			sb.append(javaType);
			sb.append(" ");
			sb.append(PinYin.getShortPinYin(columnName));
			sb.append(" = ");
			// sb.append(StrEx.upperFirst(tableName) );
			sb.append("");
			sb.append(clazzName.toLowerCase());
			sb.append(".get");
			sb.append(StrEx.upperFirst(columnName));
			sb.append("();");
			sb.append("\r\n");
			sb.append("    ");
		}
		sb.append("\r\n");
		sb.append("    ");
		sb.append("    Map<String,Object>  _ret = new HashMap<String,Object>();");
		sb.append("\r\n");
		sb.append("    ");
		for (int i = 1; i <= count; i++) {
			String columnName = rsmd.getColumnName(i);
			sb.append("    ");
			sb.append("_ret.put");
			sb.append("(\"");
			sb.append(PinYin.getShortPinYin(columnName));
			sb.append("\"");
			// sb.append(StrEx.upperFirst(tableName) );
			sb.append(",");
			sb.append(PinYin.getShortPinYin(columnName));
			sb.append(");");
			sb.append("\r\n");
			sb.append("    ");
		}
		sb.append("    ");
		sb.append("return _ret;");
		sb.append("\r\n");
		sb.append("    ");
		sb.append("}");
		sb.append("\r\n");
		// ///////////////////////////////////
		sb.append("\r\n");
		sb.append("    ");
		sb.append("public Object clone() throws CloneNotSupportedException{");
		sb.append("\r\n");
		sb.append("        return super.clone();");
		sb.append("\r\n");
		sb.append("    ");
		sb.append("}");
		sb.append("\r\n");
		// ///////////////////////////////////
		sb.append("\r\n");
		sb.append("    ");
		sb.append("public ").append(clazzName).append(" clone2(){");
		sb.append("\r\n");
		sb.append("        try{");
		sb.append("\r\n");
		sb.append("            return (").append(clazzName).append(
				") this.clone();");
		sb.append("\r\n");
		sb.append("        } catch (Exception e) {");
		sb.append("\r\n");
		sb.append("            e.printStackTrace();");
		sb.append("\r\n");
		sb.append("        }");
		sb.append("\r\n");
		sb.append("        return null;");
		sb.append("\r\n");
		sb.append("    ");
		sb.append("}");
		// ///////////////////////////////////

		sb.append("\r\n");
		sb.append("}\r\n");

		return sb.toString();
	}

	
	public static String getFieldArrayString(ResultSetMetaData rsmd, String key) throws SQLException {
		StringBuffer fields = new StringBuffer();
		int count = rsmd.getColumnCount();
		fields.append("{");
		for (int i = 1; i <= count; i++) {
			String columnName = rsmd.getColumnName(i);
			fields.append("\"");
			fields.append(columnName);
			fields.append("\"");
			if (i < count) {
				fields.append(",");
			}
		}
		fields.append("}");
		return fields.toString();
	}
    private  String changeDbType(String dbType) {  
        dbType = dbType.toUpperCase();  
        switch(dbType){  
            case "VARCHAR":  
            case "VARCHAR2":  
            case "CHAR":  
                return "1";  
            case "NUMBER":  
            case "DECIMAL":  
                return "4";  
            case "INT":  
            case "SMALLINT":  
            case "INTEGER":  
                return "2";  
            case "BIGINT":  
                return "6";  
            case "DATETIME":  
            case "TIMESTAMP":  
            case "DATE":  
                return "7";  
            default:  
                return "1";  
        }  
    } 
}
