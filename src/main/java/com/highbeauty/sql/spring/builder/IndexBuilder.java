package com.highbeauty.sql.spring.builder;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexBuilder {
	public static Map<String, List<String>> getIndex(Connection conn, ResultSetMetaData rsmd) throws SQLException{
		//List<List<String>> ret = new ArrayList<List<String>>();
		
		Map<String, List<String>> m = new HashMap<String, List<String>>();
		DatabaseMetaData dmd = conn.getMetaData();
		String tableName = rsmd.getTableName(1);
		ResultSet rs = dmd.getIndexInfo(null, null, tableName, false, true);
		while (rs.next()) {
			String indexName = rs.getString("INDEX_NAME");
			String columnName = rs.getString("COLUMN_NAME");
			
			List<String> l = m.get(indexName);
			if(l == null){
				l = new ArrayList<String>();
				l.add(columnName);
				m.put(indexName, l);
			}else{
				l.add(columnName);
			}
		}

//		Iterator<List<String>> it = m.values().iterator();
//		while (it.hasNext()) {
//			ret.add(it.next());			
//		}
		return m;
	}



	public static Map<String, List<String>> getIndex2(Connection conn, ResultSetMetaData rsmd) throws SQLException{
		String tableName = rsmd.getTableName(1);

		String sql = "SHOW INDEX FROM %s ;";
		sql = String.format(sql,tableName);
		Statement statement = conn.createStatement();

		Map<String, List<String>> m = new HashMap<String, List<String>>();

		ResultSet rs = statement.executeQuery(sql);
		while (rs.next()) {
			String Key_name = rs.getString("Key_name");
			String columnName = rs.getString("Column_name");
			int Seq_in_index = rs.getInt("Seq_in_index");

			String printstr = "Key_name=%s,columnName=%s,Seq_in_index=%s";
			System.out.println(String.format(printstr,Key_name,columnName,Seq_in_index));

			List<String> l = m.get(Key_name);
			if(l == null){
				l = new ArrayList<String>();
				l.add(Seq_in_index -1,columnName);
				m.put(Key_name, l);
			}else{
				l.add(columnName);
			}
		}
		return m;
	}


}
