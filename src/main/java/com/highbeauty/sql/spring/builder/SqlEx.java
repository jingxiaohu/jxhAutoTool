package com.highbeauty.sql.spring.builder;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import com.highbeauty.sql.ResultMap;
import com.highbeauty.util.MapEx;

@SuppressWarnings({ "unchecked"})
public class SqlEx {
	// ///////////////////////////////////////////////////////////////////////
	// Access
	public static Connection newOdbcMsAccessConnectiion(String filename)
			throws SQLException, ClassNotFoundException {
		String driver = ("sun.jdbc.odbc.JdbcOdbcDriver");
		String s = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=%s";
		String url = String.format(s, filename);
		Class.forName(driver);
		return DriverManager.getConnection(url);
	}

	public static Connection newOdbcMsAccessConnectiion(String filename,
			String user, String password) throws SQLException,
			ClassNotFoundException {
		String driver = ("sun.jdbc.odbc.JdbcOdbcDriver");
		String s = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=%s";
		String url = String.format(s, filename);
		Class.forName(driver);
		return DriverManager.getConnection(url, user, password);
	}

	// ///////////////////////////////////////////////////////////////////////
	// Excel
	public static Connection newOdbcMsExcelConnectiion(String filename)
			throws SQLException, ClassNotFoundException {
		String driver = ("sun.jdbc.odbc.JdbcOdbcDriver");
		String s = "jdbc:odbc:Driver={Microsoft Excel Driver (*.xls)};DBQ=%s";
		String url = String.format(s, filename);
		Class.forName(driver);
		return DriverManager.getConnection(url);
	}

	// ///////////////////////////////////////////////////////////////////////
	// Csv
	public static Connection newOdbcCsvConnectiion(String filename)
			throws SQLException, ClassNotFoundException {
		String driver = ("sun.jdbc.odbc.JdbcOdbcDriver");
		String s = "jdbc:odbc:Driver={Microsoft Access Text Driver (*.txt,*.csv)};DBQ=%s";
		String url = String.format(s, filename);
		Class.forName(driver);
		return DriverManager.getConnection(url);
	}

	// ///////////////////////////////////////////////////////////////////////
	// Odbc Ms Sql
	public static Connection newOdbcMsSqlConnectiion(String db)
			throws SQLException, ClassNotFoundException {
		String host = "127.0.0.1";
		return newOdbcMsSqlConnectiion(host, db);
	}

	public static Connection newOdbcMsSqlConnectiion(String host, String db)
			throws SQLException, ClassNotFoundException {
		String user = "sa";
		String password = "";
		return newOdbcMsSqlConnectiion(host, db, user, password);
	}

	public static Connection newOdbcMsSqlConnectiion(String host, String db,
			String user, String password) throws SQLException,
			ClassNotFoundException {
		String driver = ("sun.jdbc.odbc.JdbcOdbcDriver");
		String s = "jdbc:odbc:Driver={SQL Server};Server=%s;Database=%s";
		String url = String.format(s, host, db);
		Class.forName(driver);
		return DriverManager.getConnection(url, user, password);
	}

	// ///////////////////////////////////////////////////////////////////////
	// Jtds
	public static Connection newJtdsSqlserverConnection(String db)
			throws ClassNotFoundException, SQLException {
		String host = "127.0.0.1";
		return newJtdsSqlserverConnection(host, db);
	}

	public static Connection newJtdsSqlserverConnection(String host, String db)
			throws ClassNotFoundException, SQLException {
		int port = 1433;
		return newJtdsSqlserverConnection(host, port, db);
	}

	public static Connection newJtdsSqlserverConnection(String host, int port,
			String db) throws ClassNotFoundException, SQLException {
		String user = "sa";
		String password = "";
		return newJtdsSqlserverConnection(host, port, db, user, password);
	}

	public static Connection newJtdsSqlserverConnection(String host, int port,
			String db, String user, String password)
			throws ClassNotFoundException, SQLException {
		String driver = ("net.sourceforge.jtds.jdbc.Driver");
		String s = "jdbc:jtds:sqlserver://%s:%d/%s";
		String url = String.format(s, host, port, db);
		Class.forName(driver);
		return DriverManager.getConnection(url, user, password);
	}

	// ///////////////////////////////////////////////////////////////////////
	// Mysql
	public static Connection newMysqlConnection(String db)
			throws ClassNotFoundException, SQLException {
		String host = "127.0.0.1";
		return newMysqlConnection(host, db);
	}

	public static Connection newMysqlConnection(String host, String db)
			throws ClassNotFoundException, SQLException {
		String user = "root";
		String password = "root";
		return newMysqlConnection(host, db, user, password);
	}

	public static Connection newMysqlConnection(String host, String db,
			String user, String password) throws ClassNotFoundException,
			SQLException {
		int port = 3306;
		return newMysqlConnection(host, port, db, user, password);
	}

	public static Connection newMysqlConnection(String host, int port,
			String db, String user, String password)
			throws ClassNotFoundException, SQLException {
		boolean reconnect = true;
		String encoding = "utf8";
		return newMysqlConnection(host, port, db, reconnect, encoding, user,
				password);
	}

	public static Connection newMysqlConnection(String host, int port,
			String db, boolean reconnect, String encoding, String user,
			String password) throws ClassNotFoundException, SQLException {
		 String driver = ("com.mysql.jdbc.Driver");
//		 String s = "jdbc:mysql://%s:%d/%s?autoReconnect=%s&characterEncoding=%s";
		String s = "jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC&autoReconnect=%s&characterEncoding=%s";
		 String url = String.format(s, host, port, db,String.valueOf(reconnect), encoding);
		Class.forName(driver);
		return DriverManager.getConnection(url, user,password);
	}
	  //获取连接  
	public static Connection getConnections(String driver,String url,String user,String pwd) throws Exception {  
        Connection conn = null;  
        try {  
            Properties props = new Properties();  
            props.put("remarksReporting", "true");  
            props.put("user", user);  
            props.put("password", pwd);  
            Class.forName(driver);  
            conn = DriverManager.getConnection(url, props);  
        } catch (Exception e) {  
            e.printStackTrace();  
            throw e;  
        }  
        return conn;  
    }  

	// ///////////////////////////////////////////////////////////////////////
	// Oracle
	public static Connection newOracleConnection(String host, int port,
			String db, String user, String password)
			throws ClassNotFoundException, SQLException {
		String driver = ("oracle.jdbc.driver.OracleDriver");
		String s = "jdbc:oracle:thin:@%s:%d:%s";
		String url = String.format(s, host, port, db);
		Class.forName(driver);
		return DriverManager.getConnection(url, user, password);
	}

	// ///////////////////////////////////////////////////////////////////////
	// VFP
	public Connection newVFPDbcConnection(String file)
			throws ClassNotFoundException, SQLException {
		String type = "dbc";
		return newVFPConnection(type, file);
	}

	public Connection newVFPDbfConnection(String file)
			throws ClassNotFoundException, SQLException {
		String type = "dbf";
		return newVFPConnection(type, file);
	}

	public Connection newVFPConnection(String type, String file)
			throws ClassNotFoundException, SQLException {
		String driver = ("sun.jdbc.odbc.JdbcOdbcDriver");
		String s = "jdbc:odbc:Driver={Microsoft Visual FoxPro Driver};SourceType=%s;Exclusive=No;SourceDB=%s;";
		String url = String.format(s, type, file);
		Class.forName(driver);
		return DriverManager.getConnection(url);
	}

	// ///////////////////////////////////////////////////////////////////////
	// Sybase
	public Connection newJtdsSybaseConnection(String host, int port, String db,
			String charset) throws ClassNotFoundException, SQLException {
		String driver = ("net.sourceforge.jtds.jdbc.Driver");
		// jdbc:jtds:Sybase://192.168.2.200:5000/taxiupload;charset=cp936
		String s = "jdbc:jtds:Sybase://%s:%d/%s;charset=%s";
		String url = String.format(s, host, port, db, charset);
		Class.forName(driver);
		return DriverManager.getConnection(url);
	}

	public static void openCommit(Connection conn) throws SQLException {
		conn.setAutoCommit(true);
	}

	public static void closeCommit(Connection conn) throws SQLException {
		conn.setAutoCommit(false);
	}

	public static void commit(Connection conn) throws SQLException {
		conn.commit();
	}

	public static void rollback(Connection conn) throws SQLException {
		conn.rollback();
	}

	// ///////////////////////////////////////////////////////////////////////
	public static List<String> getDatabases(String driver, String url,
			String user, String password) throws Exception {
		List<String> ret = new Vector<String>();
		Class.forName(driver).newInstance();
		Connection conn = DriverManager.getConnection(url, user, password);
		ret = getDatabases(conn);
		conn.close();
		return ret;
	}

	public static List<String> getDatabases(Connection conn) throws Exception {
		List<String> ret = new Vector<String>();
		DatabaseMetaData dme = conn.getMetaData();
		ResultSet rs = dme.getCatalogs();
		while (rs.next()) {
			String d = rs.getString(1);
			ret.add(d);
		}
		return ret;
	}

	// ///////////////////////////////////////////////////////////////////////
	public static List<Map> getPrimaryKeys(Connection conn,
			String table) throws Exception {
		DatabaseMetaData dmd = conn.getMetaData();
		ResultSet rs = dmd.getPrimaryKeys(null, null, table);

		List<Map> ret = toMaps(rs);
		return ret;
	}

	public static List<Map> getImportedKeys(Connection conn,
			String table) throws Exception {
		DatabaseMetaData dmd = conn.getMetaData();
		ResultSet rs = dmd.getImportedKeys(null, null, table);

		List<Map> ret = toMaps(rs);
		return ret;
	}

	public static List<Map> getExportedKeys(Connection conn,
			String table) throws Exception {
		DatabaseMetaData dmd = conn.getMetaData();
		ResultSet rs = dmd.getExportedKeys(null, null, table);

		List<Map> ret = toMaps(rs);
		return ret;
	}
	
	public static Map<String, List<Map<String, Object>>> getIndexs(
			Connection conn, String table) throws Exception {
		Map ret = new HashMap();
		boolean unique = false;
		List<Map<String, Object>> indexs = getIndexInfo(conn, table, unique);
		for (Map<String, Object> m : indexs) {
			String INDEX_NAME = (String) m.get("INDEX_NAME");
			List<Map<String, Object>> es = (List<Map<String, Object>>) ret
					.get(INDEX_NAME);
			if (es == null) {
				es = new ArrayList<Map<String, Object>>();
				ret.put(INDEX_NAME, es);
			}
			es.add(m);
		}
		return ret;
	}

	public static List<Map<String, Object>> getIndexInfo(Connection conn,
			String table, boolean unique) throws Exception {
		boolean approximate = true;
		DatabaseMetaData dmd = conn.getMetaData();
		ResultSet rs = dmd.getIndexInfo(null, null, table, unique, approximate);

		List<Map<String, Object>> ret = new Vector<Map<String, Object>>();
		while (rs.next()) {
			// Map<String, Object> e = new HashMap<String, Object>();
			// e.put("TABLE_CAT", rs.getString("TABLE_CAT"));
			// e.put("TABLE_SCHEM", rs.getString("TABLE_SCHEM"));
			// e.put("TABLE_NAME", rs.getString("TABLE_NAME"));
			// e.put("NON_UNIQUE", rs.getString("NON_UNIQUE"));
			// e.put("INDEX_QUALIFIER", rs.getString("INDEX_QUALIFIER"));
			// e.put("INDEX_NAME", rs.getString("INDEX_NAME"));
			// e.put("TYPE", rs.getString("TYPE"));
			// e.put("ORDINAL_POSITION", rs.getString("ORDINAL_POSITION"));
			// e.put("COLUMN_NAME", rs.getString("COLUMN_NAME"));
			// e.put("ASC_OR_DESC", rs.getString("ASC_OR_DESC"));
			// e.put("CARDINALITY", rs.getString("CARDINALITY"));
			// e.put("PAGES", rs.getString("PAGES"));
			// e.put("FILTER_CONDITION", rs.getString("FILTER_CONDITION"));
			Map<String,Object> e = ResultMap.newResultMap(rs);
			ret.add(e);
		}
		return ret;
	}

	// ///////////////////////////////////////////////////////////////////////
	public static List<String> getTables(String driver, String url,
			String user, String password) throws Exception {
		List<String> ret = new Vector<String>();
		Class.forName(driver).newInstance();
		Connection conn = DriverManager.getConnection(url, user, password);
		ret = getTables(conn);
		conn.close();
		return ret;
	}

	public static List<String> getTables(DataSource ds) throws Exception {
		return getTables(ds.getConnection());
	}

	public static List<String> getTables(Connection conn) throws Exception {
		List<String> ret = new Vector<String>();
		DatabaseMetaData dme = conn.getMetaData();
		ResultSet rs = dme.getTables(null, null, null, null);
		while (rs.next()) {
			String t = rs.getString("TABLE_NAME");
			String type = rs.getString(4);
			if (t == null || type == null)
				continue;
			if (!type.equals("TABLE"))
				continue;
			ret.add(t);
		}
		return ret;
	}

	public static boolean isTableExist(DataSource ds, String table) {
		try {
			return isTableExist(ds.getConnection(), table);
		} catch (SQLException e) {
		}
		return false;
	}

	public static boolean isTableExist(Connection conn, String table) {
		try {
			DatabaseMetaData dme = conn.getMetaData();
			ResultSet rs = dme.getTables(null, null, table, null);
			boolean ret = false;
			while (rs.next()) {
				ret = true;
			}
			return ret;
		} catch (SQLException e) {
		}
		return false;
	}

	// ///////////////////////////////////////////////////////////////////////
	public static List<String> getViews(String driver, String url, String user,
			String password) throws Exception {
		List<String> ret = new Vector<String>();
		Class.forName(driver).newInstance();
		Connection conn = DriverManager.getConnection(url, user, password);
		ret = getTables(conn);
		conn.close();
		return ret;
	}

	public static List<String> getViews(DataSource ds) throws Exception {
		return getViews(ds.getConnection());
	}

	public static List<String> getViews(Connection conn) throws Exception {
		List<String> ret = new Vector<String>();
		DatabaseMetaData dme = conn.getMetaData();
		ResultSet rs = dme.getTables(null, null, null, null);
		while (rs.next()) {
			String t = rs.getString("TABLE_NAME"); // 3
			String type = rs.getString(4);
			if (t == null || type == null)
				continue;
			if (!type.equals("VIEW"))
				continue;
			ret.add(t);
		}
		return ret;
	}

	// ///////////////////////////////////////////////////////////////////////
	public static List<Map<String, Object>> getColumns(DataSource ds, String sql)
			throws Exception {
		return getColumns(ds.getConnection(), sql);
	}

	public static List<Map<String, Object>> getColumns(Connection conn,
			String sql) throws Exception {
		ResultSet rs = executeQuery(conn, sql);
		return getColumns(rs);
	}

	public static List<Map<String, Object>> getColumns(ResultSet rs)
			throws Exception {
		List<Map<String, Object>> ret = new Vector<Map<String, Object>>();
		ResultSetMetaData rsmd = rs.getMetaData();
		int count = rsmd.getColumnCount();
		for (int i = 1; i <= count; i++) {
			String columnName = rsmd.getColumnName(i);
			int columnType = rsmd.getColumnType(i);
			String columnLabel = rsmd.getColumnLabel(i);
			String columnTypeName = rsmd.getColumnTypeName(i);
			String catalogName = rsmd.getCatalogName(i);
			String columnClassName = rsmd.getColumnClassName(i);
			int precision = rsmd.getPrecision(i);
			int scale = rsmd.getScale(i);
			String schemaName = rsmd.getSchemaName(i);
			String tableName = rsmd.getTableName(i);
			int columnDisplaySize = rsmd.getColumnDisplaySize(i);
			boolean isAutoIncrement = rsmd.isAutoIncrement(i);
			boolean isCaseSensitive = rsmd.isCaseSensitive(i);
			boolean isCurrency = rsmd.isCurrency(i);
			boolean isDefinitelyWritable = rsmd.isDefinitelyWritable(i);
			int isNullable = rsmd.isNullable(i);
			boolean isReadOnly = rsmd.isReadOnly(i);
			boolean isSearchable = rsmd.isSearchable(i);
			boolean isSigned = rsmd.isSigned(i);
			boolean isWritable = rsmd.isWritable(i);

			Map e = new HashMap();
			e.put("i", i);
			e.put("columnName", columnName);
			e.put("columnType", columnType);
			e.put("columnLabel", columnLabel);
			e.put("columnTypeName", columnTypeName);
			e.put("catalogName", catalogName);
			e.put("columnClassName", columnClassName);
			e.put("precision", precision);
			e.put("scale", scale);
			e.put("schemaName", schemaName);
			e.put("tableName", tableName);
			e.put("columnDisplaySize", columnDisplaySize);
			e.put("isAutoIncrement", isAutoIncrement);
			e.put("isCaseSensitive", isCaseSensitive);
			e.put("isCurrency", isCurrency);
			e.put("isDefinitelyWritable", isDefinitelyWritable);
			e.put("isNullable", isNullable);
			e.put("isReadOnly", isReadOnly);
			e.put("isSearchable", isSearchable);
			e.put("isSigned", isSigned);
			e.put("isWritable", isWritable);
			ret.add(e);
		}
		return ret;
	}

	public static Map<String, Object> getColumn(DataSource ds, String sql,
			String columnName) throws Exception {
		return getColumn(ds.getConnection(), sql, columnName);
	}

	public static Map<String, Object> getColumn(Connection conn, String sql,
			String columnName) throws Exception {
		ResultSet rs = executeQuery(conn, sql);
		return getColumn(rs, columnName);
	}

	public static Map<String, Object> getColumn(ResultSet rs, String columnName)
			throws Exception {
		List<Map<String, Object>> columns = getColumns(rs);
		return getColumn(columns, columnName);
	}

	public static Map<String, Object> getColumn(
			List<Map<String, Object>> columns, String columnName)
			throws Exception {
		for (Map<String, Object> e : columns) {
			String _columnName = (String) e.get("columnName");
			if (columnName.equals(_columnName))
				return e;
		}
		return null;
	}

	public static Map<String, Object> getColumn(DataSource ds, String sql, int i)
			throws Exception {
		return getColumn(ds.getConnection(), sql, i);
	}

	public static Map<String, Object> getColumn(Connection conn, String sql,
			int i) throws Exception {
		ResultSet rs = executeQuery(conn, sql);
		return getColumn(rs, i);
	}

	public static Map<String, Object> getColumn(ResultSet rs, int i)
			throws Exception {
		List<Map<String, Object>> columns = getColumns(rs);
		return getColumn(columns, i);
	}

	public static Map<String, Object> getColumn(
			List<Map<String, Object>> columns, int i) throws Exception {
		for (Map<String, Object> e : columns) {
			int _i = (Integer) e.get("i");
			if (i == _i)
				return e;
		}
		return null;
	}

	// ///////////////////////////////////////////////////////////////////////
	public static PreparedStatement prepareStatement(DataSource ds, String sql)
			throws SQLException {
		return prepareStatement(ds.getConnection(), sql);
	}

	public static PreparedStatement prepareStatement(Connection conn, String sql)
			throws SQLException {
		return conn.prepareStatement(sql);
	}

	public static boolean execute(DataSource ds, String sql)
			throws SQLException {
		return execute(ds.getConnection(), sql);
	}

	public static boolean execute(Connection conn, String sql)
			throws SQLException {
		PreparedStatement ps = prepareStatement(conn, sql);
		return ps.execute();
	}

	public static ResultSet executeQuery(DataSource ds, String sql)
			throws SQLException {
		return executeQuery(ds.getConnection(), sql);
	}

	public static ResultSet executeQuery(Connection conn, String sql)
			throws SQLException {
		PreparedStatement ps = prepareStatement(conn, sql);
		return ps.executeQuery();
	}

	public static int executeUpdate(DataSource ds, String sql)
			throws SQLException {
		return executeUpdate(ds.getConnection(), sql);
	}

	public static int executeUpdate(Connection conn, String sql)
			throws SQLException {
		PreparedStatement ps = prepareStatement(conn, sql);
		return ps.executeUpdate();
	}

	// ///////////////////////////////////////////////////////////////////////
	public static int pageCount(int count, int pageSize) {
		int page = count / pageSize;

		page = count == page * pageSize ? page : page + 1;
		return page;
	}

	public static List selectByPage(List v, int page, int pageSize) {
		int count = v.size();
		int begin = page * pageSize;
		int end = begin + pageSize;
		if (begin > count || begin < 0 || end < 0)
			return new ArrayList();
		end = count < end ? count : end;
		if (end <= begin)
			new ArrayList();
		return v.subList(begin, end);
		// List ret = new ArrayList();
		// for (int i = begin; i < end; i++) {
		// Object e = v.get(i);
		// ret.add(e);
		// }
		// return ret;
	}

	public static List getPage(List v, int page, int pageSize) {
		return selectByPage(v, page, pageSize);
	}

	// //////////////////////////////////
	public static long pageCount(long count, long pageSize) {
		long page = count / pageSize;

		page = count == page * pageSize ? page : page + 1;
		return page;
	}

	public static List selectByPage(List v, long page, long pageSize) {
		int count = v.size();
		int begin = (int) (page * pageSize);
		int end = (int) (begin + pageSize);
		if (begin > count || begin < 0 || end < 0)
			return new ArrayList();
		end = count < end ? count : end;
		if (end <= begin)
			new ArrayList();
		return v.subList(begin, end);
		// List ret = new ArrayList();
		// for (long i = begin; i < end; i++) {
		// Object e = v.get((int) i);
		// ret.add(e);
		// }
		// return ret;
	}

	public static List getPage(List v, long page, long pageSize) {
		return selectByPage(v, page, pageSize);
	}

	public static List<Object[]> toArrays(ResultSet rs) throws SQLException {
		List<Object[]> result = new Vector();
		while (rs.next()) {
			Object[] m = toArray(rs);
			result.add(m);
		}
		return result;
	}

	public static Object[] toArray(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		int cols = meta.getColumnCount();
		Object result[] = new Object[cols];
		for (int i = 0; i < cols; i++)
			result[i] = rs.getObject(i + 1);

		return result;
	}

	public static List<Map> toMaps(ResultSet rs) throws SQLException {
		List<Map> result = new Vector();
		while (rs.next()) {
			Map m = toMap(rs);
			result.add(m);
		}
		return result;
	}

	public static Map toMap(ResultSet rs) throws SQLException {
		Map result = new HashMap();
		ResultSetMetaData rsmd = rs.getMetaData();
		int cols = rsmd.getColumnCount();
		for (int i = 1; i <= cols; i++)
			result.put(rsmd.getColumnName(i), rs.getObject(i));

		return result;
	}

	// ///////////////////////////////////////////////////////////////////////
	public static String createMysqlTable(Connection conn, ResultSet rs,
			String tableName) throws Exception {
		try {
			List<Map<String, Object>> columns = SqlEx.getColumns(rs);
			StringBuffer sb = new StringBuffer();
			sb.append("CREATE TABLE IF NOT EXISTS `${TABLENAME}` (\n");
			for (Map<String, Object> map : columns) {
				String columnName = MapEx.get(map, "columnName");
				// int columnType = MapEx.get(map, "columnType");
				// String columnLabel = MapEx.get(map, "columnLabel");
				String columnTypeName = MapEx.get(map, "columnTypeName");
				// String catalogName = MapEx.get(map, "catalogName");
				// String columnClassName = MapEx.get(map, "columnClassName");
				int precision = MapEx.get(map, "precision");
				int scale = MapEx.get(map, "scale");
				// String schemaName = MapEx.get(map, "schemaName");
				// String tableName = MapEx.get(map, "tableName");
				// int columnDisplaySize = MapEx.get(map, "columnDisplaySize");
				boolean isAutoIncrement = MapEx.get(map, "isAutoIncrement");
				// boolean isCaseSensitive = MapEx.get(map, "isCaseSensitive");
				// boolean isCurrency = MapEx.get(map, "isCurrency");
				// boolean isDefinitelyWritable =
				// MapEx.get(map,"isDefinitelyWritable");
				int isNullable = MapEx.get(map, "isNullable");
				// boolean isReadOnly = MapEx.get(map, "isReadOnly");
				// boolean isSearchable = MapEx.get(map, "isSearchable");
				// boolean isSigned = MapEx.get(map, "isSigned");
				// boolean isWritable = MapEx.get(map, "isWritable");

				// System.out.println(columnTypeName + "/" + precision + "/" +
				// scale);
				sb.append("\t");
				sb.append("`" + columnName + "`");
				sb.append("  ");
				if (columnTypeName.equals("VARCHAR") && precision >= 715827882) {
					sb.append("LONGTEXT");
				} else if (columnTypeName.equals("VARCHAR") && precision >= 5592405) {
					sb.append("MEDIUMTEXT");
				} else if (columnTypeName.equals("VARCHAR") && precision >= 21845) {
					sb.append("TEXT");
				} else if (columnTypeName.equals("VARCHAR") && precision >= 255) {
					sb.append("TINYTEXT");
				} else if (columnTypeName.equals("MEDIUMBLOB")
						|| columnTypeName.equals("LONGBLOB")
						|| columnTypeName.equals("BLOB")
						|| columnTypeName.equals("TINYBLOB")) {
					sb.append(columnTypeName);
				} else if (columnTypeName.equals("DATETIME")
						|| columnTypeName.equals("DATE")
						|| columnTypeName.equals("TIME")
						|| columnTypeName.equals("TIMESTAMP")) {
					sb.append(columnTypeName);
				} else if (columnTypeName.equals("DOUBLE")) {
					sb.append(columnTypeName);
				} else if (columnTypeName.equals("DECIMAL")) {
					sb.append(columnTypeName);
					sb.append("(");
					sb.append(precision);
					sb.append(",");
					sb.append(scale);
					sb.append(")");
				} else {
					sb.append(columnTypeName);
					sb.append("(");
					sb.append(precision);
					sb.append(")");
				}
				if (isNullable == 0) {
					sb.append(" NOT NULL");
				}

				if (isAutoIncrement) {
					sb.append(" AUTO_INCREMENT");
				}
				sb.append(",\n");
				// System.out.println(map);
			}
			// List<Map<String, Object>> pks = SqlEx.getPrimaryKeys(conn, t);
			// for (Map<String, Object> map : pks) {
			// System.out.println(map);
			// }
			List<Map<String, Object>> nouniques = SqlEx.getIndexInfo(conn,
					tableName, false);
			int nouniquesLength = nouniques.size();
			int i = 0;
			for (Map<String, Object> map : nouniques) {
				String COLUMN_NAME = MapEx.get(map, "COLUMN_NAME");
				String INDEX_NAME = MapEx.get(map, "INDEX_NAME");
				boolean NON_UNIQUE = Boolean.parseBoolean(MapEx.get(map, "NON_UNIQUE").toString());
				sb.append("\t");
				if (INDEX_NAME.equals("PRIMARY")) {
					sb.append("PRIMARY KEY (`" + COLUMN_NAME + "`)");
				} else if (!NON_UNIQUE) {
					INDEX_NAME = INDEX_NAME.replace(tableName, "${TABLENAME}");
					sb.append("UNIQUE KEY `" + INDEX_NAME + "` (`" + COLUMN_NAME
							+ "`)");
				} else {
					INDEX_NAME = INDEX_NAME.replace(tableName, "${TABLENAME}");
					sb.append("KEY `" + INDEX_NAME + "` (`" + COLUMN_NAME + "`)");
				}
				i++;
				if (i < nouniquesLength) {
					sb.append(",");
				}
				sb.append("\n");
			}

			sb.append(") ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;\n");
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	
	/**
	 * 
	 * @param conn
	 * @param rs
	 * @param tableName
	 * @param map_comment
	 * @param charset_type 0:utf-8 1:utf8mb4
	 * @param ENGINE_NAME_TYPE 0：MyISAM  1：InnoDB
	 * @return
	 * @throws Exception
	 */
	public static String createMysqlTable_Comment(Connection conn, ResultSet rs,
			String tableName,Map<String,String>  map_comment,int charset_type,int ENGINE_NAME_TYPE) throws Exception {
		try {
			String ENGINE_NAME = "InnoDB";
			if(ENGINE_NAME_TYPE == 0){
				 ENGINE_NAME = "MyISAM";
			}
			
			List<Map<String, Object>> columns = SqlEx.getColumns(rs);
			StringBuffer sb = new StringBuffer();
			sb.append("CREATE TABLE IF NOT EXISTS `${TABLENAME}` (\n");
			for (Map<String, Object> map : columns) {
				//列名称
				String columnName = MapEx.get(map, "columnName");
				//注解名称
				String comment = map_comment.get(columnName);
				if( null != comment){
					comment = comment.replaceAll("\"", "'");
				}else{
					comment = "";
				}


				//TODO 这里添加默认值 `id_flag` int(1) DEFAULT '0' COMMENT '身份证标记 0：否 1：是',
				String default_value = map_comment.get(columnName+"_default_value");
				if(null != default_value){
					default_value = default_value.replaceAll("\"", "'");
					if(!"\'\'".equalsIgnoreCase(default_value)){
						default_value = " DEFAULT " + "\'" + default_value + "\'"+" ";
					}else{
						default_value = " DEFAULT " +  default_value +" ";
					}
				}else{
					default_value = " DEFAULT " +  "NULL" +" ";
				}

				
				// int columnType = MapEx.get(map, "columnType");
				// String columnLabel = MapEx.get(map, "columnLabel");
				String columnTypeName = MapEx.get(map, "columnTypeName");
				// String catalogName = MapEx.get(map, "catalogName");
				// String columnClassName = MapEx.get(map, "columnClassName");
				int precision = MapEx.get(map, "precision");
				int scale = MapEx.get(map, "scale");
				// String schemaName = MapEx.get(map, "schemaName");
				// String tableName = MapEx.get(map, "tableName");
				// int columnDisplaySize = MapEx.get(map, "columnDisplaySize");
				boolean isAutoIncrement = MapEx.get(map, "isAutoIncrement");
				// boolean isCaseSensitive = MapEx.get(map, "isCaseSensitive");
				// boolean isCurrency = MapEx.get(map, "isCurrency");
				// boolean isDefinitelyWritable =
				// MapEx.get(map,"isDefinitelyWritable");
				int isNullable = MapEx.get(map, "isNullable");
				// boolean isReadOnly = MapEx.get(map, "isReadOnly");
				// boolean isSearchable = MapEx.get(map, "isSearchable");
				// boolean isSigned = MapEx.get(map, "isSigned");
				// boolean isWritable = MapEx.get(map, "isWritable");

				// System.out.println(columnTypeName + "/" + precision + "/" +
				// scale);
				sb.append("\t");
				sb.append("`" + columnName + "`");
				sb.append("  ");
				if (columnTypeName.equals("VARCHAR") && precision >= 715827882) {
					sb.append("LONGTEXT");
				} else if (columnTypeName.equals("VARCHAR") && precision >= 5592405) {
					sb.append("MEDIUMTEXT");
				} else if (columnTypeName.equals("VARCHAR") && precision >= 21845) {
					sb.append("TEXT");
				} else if (columnTypeName.equals("VARCHAR") && precision >= 255) {
					sb.append("TINYTEXT");
				} else if (columnTypeName.equals("MEDIUMBLOB")
						|| columnTypeName.equals("LONGBLOB")
						|| columnTypeName.equals("BLOB")
						|| columnTypeName.equals("TINYBLOB")) {
					sb.append(columnTypeName);
				} else if (columnTypeName.equals("DATETIME")
						|| columnTypeName.equals("DATE")
						|| columnTypeName.equals("TIME")
						|| columnTypeName.equals("TIMESTAMP")) {
					sb.append(columnTypeName);
				} else if (columnTypeName.equals("DOUBLE")) {
					sb.append(columnTypeName);
				} else if (columnTypeName.equals("DECIMAL")) {
					sb.append(columnTypeName);
					sb.append("(");
					sb.append(precision);
					sb.append(",");
					sb.append(scale);
					sb.append(")");
				} else {
					sb.append(columnTypeName);
					sb.append("(");
					sb.append(precision);
					sb.append(")");
				}
				if (isNullable == 0) {
					sb.append(" NOT NULL");
				}

				if (isAutoIncrement) {
					sb.append(" AUTO_INCREMENT");
				}
				//TODO 这里添加 默认值
				if(default_value != null){
					sb.append(default_value);
				}

				//TODO 这里添加注释
				if(comment != null){
					sb.append(" COMMENT '"+comment+"'");
				}
				sb.append(",\n");
				// System.out.println(map);
			}
			// List<Map<String, Object>> pks = SqlEx.getPrimaryKeys(conn, t);
			// for (Map<String, Object> map : pks) {
			// System.out.println(map);
			// }
			List<Map<String, Object>> nouniques = SqlEx.getIndexInfo(conn,
					tableName, false);
			int nouniquesLength = nouniques.size();
			int i = 0;
			for (Map<String, Object> map : nouniques) {
				String COLUMN_NAME = MapEx.get(map, "COLUMN_NAME");
				String INDEX_NAME = MapEx.get(map, "INDEX_NAME");
				boolean NON_UNIQUE = Boolean.parseBoolean(MapEx.get(map, "NON_UNIQUE").toString());
				sb.append("\t");
				if (INDEX_NAME.equals("PRIMARY")) {
					sb.append("PRIMARY KEY (`" + COLUMN_NAME + "`)");
				} else if (!NON_UNIQUE) {
					INDEX_NAME = INDEX_NAME.replace(tableName, "${TABLENAME}");
					sb.append("UNIQUE KEY `" + INDEX_NAME + "` (`" + COLUMN_NAME
							+ "`)");
				} else {
					INDEX_NAME = INDEX_NAME.replace(tableName, "${TABLENAME}");
					sb.append("KEY `" + INDEX_NAME + "` (`" + COLUMN_NAME + "`)");
				}
				i++;
				if (i < nouniquesLength) {
					sb.append(",");
				}
				sb.append("\n");
			}
			if(charset_type == 0){
				sb.append(") ENGINE="+ENGINE_NAME+"  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;\n");
			}else{
				sb.append(") ENGINE="+ENGINE_NAME+"  DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1 ;\n");
			}
			
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	// ///////////////////////////////////////////////////////////////////////
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

	public static String getType(ResultSetMetaData rsmd, int i)
			throws SQLException {
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

	// ///////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////
	public static Executor _singleExecutor = null;

	public static void executeSingle(Runnable r) {
		if (_singleExecutor == null)
			_singleExecutor = Executors.newSingleThreadExecutor();
		_singleExecutor.execute(r);
	}

	public static  Executor _4FixedExecutor = null;

	public static void execute4Fixed(Runnable r) {
		if (_4FixedExecutor == null)
			_4FixedExecutor = Executors.newFixedThreadPool(4);
		_4FixedExecutor.execute(r);
	}

}
