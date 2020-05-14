package com.highbeauty.sql.spring.builder;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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
}
