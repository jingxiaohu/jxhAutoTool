package com.highbeauty.sql.spring.builder;

import java.sql.Connection;
import java.sql.ResultSet;

public class TableSql {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String host = "192.168.2.241";
		String db = "fych";
		Connection conn = SqlEx.newMysqlConnection(host, db);
		// List<String> tables = SqlEx.getTables(conn);
		// for (String t : tables) {
		// System.out.println(t);
		// }

		String tableName = "�û���ɫ";
		String tableName2 = "�û���ɫ123";
		String sql = "select * from `" + tableName + "` LIMIT 1";
		ResultSet rs = SqlEx.executeQuery(conn, sql);
		String s = SqlEx.createMysqlTable(conn, rs, tableName);
		System.out.println(s);
	}



}
