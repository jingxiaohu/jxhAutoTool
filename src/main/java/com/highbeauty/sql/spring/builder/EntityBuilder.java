package com.highbeauty.sql.spring.builder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.highbeauty.pinyin.PinYin;

public class EntityBuilder {

	public static void main(String[] args) throws Exception {

		String sql = "SELECT * FROM `����` LIMIT 1";
		String host = "192.168.2.241";
		String db = "fych";
		Connection conn = SqlEx.newMysqlConnection(host, db);
		
		ResultSet rs = SqlEx.executeQuery(conn, sql);

		boolean immediately = true;
		String appcontext = "";
		String pkg = "test.dao.";

		EntityBuilder builder = new EntityBuilder();
		String xml = builder.build(conn, rs, pkg + "internal", pkg + "bean",
				pkg + "dao", pkg + "entity", appcontext, immediately);
		System.out.println(xml);
	}

	public static void EntityBuild(Connection conn, String tablename,
			String pkg, boolean src, String appcontext, boolean immediately)
			throws Exception {

		String sql = String.format("SELECT * FROM `%s` LIMIT 1", tablename);

		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		EntityBuilder builder = new EntityBuilder();
		String xml = builder.build(conn, rs, 
				pkg + "entity", 
				pkg + "bean", 
				pkg + "dao", 
				pkg + "internal",
				appcontext, immediately);
		System.out.println(xml);
		conn.close();
	}

	public String build(Connection conn, ResultSet rs, String pkg,
			String beanPkg, String daoPkg, String internalPkg,
			String appcontext, boolean immediately) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		String tableName = rsmd.getTableName(1);

		System.out.println("-----------------------");

		StringBuffer sb = new StringBuffer();
		// import
		// sb.append("import java.io.*;");
		// sb.append("\r\n");
		if (pkg != null && pkg.length() > 0) {
			sb.append("package " + pkg + ";");
			sb.append("\r\n");
			sb.append("\r\n");
		}
		sb.append("import java.util.*;\r\n");
		sb.append("import com.bowlong.sql.*;\r\n");
		sb.append("import com.bowlong.lang.*;\r\n");
		sb.append("import org.springframework.jdbc.core.*;\r\n");
		sb.append("import org.apache.commons.logging.*;\r\n");
		sb.append("import org.springframework.jdbc.core.namedparam.*;\r\n");
		sb.append("import org.springframework.jdbc.support.*;\r\n");
		sb.append("import ").append(beanPkg).append(".*;\r\n");
		sb.append("import ").append(daoPkg).append(".*;\r\n");
		sb.append("import ").append(internalPkg).append(".*;\r\n");
		if (appcontext != null && !appcontext.isEmpty()) {
			sb.append("import ").append(appcontext).append(";\r\n");
		}
		sb.append("\r\n");

		String shortName = PinYin.getShortPinYin(tableName);
		String UShortName = StrEx.upperFirst(shortName);
		// class
		sb.append("//" + tableName + "\r\n");
		sb.append("@SuppressWarnings({ \"unchecked\", \"unused\" })\r\n");
		sb.append("public class " + UShortName + "Entity extends " + UShortName
				+ "Internal{\r\n");
		sb.append("    static Log log = LogFactory.getLog("+UShortName+"Entity.class);\r\n");
		sb.append("\r\n");
		sb.append("    public static final " + UShortName + "Entity my = new "
				+ UShortName + "Entity();\r\n");
		sb.append("\r\n");

		sb.append("    public static long TIMEOUT(){\r\n");
		sb.append("        return 0;\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");
		sb.append("}\r\n");
		sb.append("\r\n");

		
		return sb.toString();
	}
}
