package com.highbeauty.sql.spring.builder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import com.highbeauty.pinyin.PinYin;

public class InternalBuilder {
	public static void main(String[] args) throws Exception {

		String sql = "SELECT * FROM `����` LIMIT 1";
		String host = "192.168.2.241";
		String db = "fych";
		Connection conn = SqlEx.newMysqlConnection(host, db);
		
		ResultSet rs = SqlEx.executeQuery(conn, sql);

		boolean immediately = true;
		String appcontext = "";
		String pkg = "test.dao.";

		InternalBuilder builder = new InternalBuilder();
		String xml = builder.build(conn, rs, pkg + "internal", pkg + "bean",
				pkg + "dao", pkg + "entity", appcontext, immediately);
		System.out.println(xml);
	}

	public static void InternalBuild(Connection conn, String tablename,
			String pkg, boolean src, String appcontext, boolean immediately)
			throws Exception {

		String sql = String.format("SELECT * FROM `%s` LIMIT 1", tablename);

		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		InternalBuilder builder = new InternalBuilder();
		String xml = builder.build(conn, rs, pkg + "internal", pkg + "bean",
				pkg + "dao", pkg + "entity", appcontext, immediately);
		System.out.println(xml);
		conn.close();
	}

	public String build(Connection conn, ResultSet rs, String pkg,
			String beanPkg, String daoPkg, String entityPkg, String appcontext,
			boolean immediately) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		String tableName = rsmd.getTableName(1);

		String db = rsmd.getCatalogName(1);
		List<MyIndex> indexs = MyIndex.Indexs(conn, db, tableName);
		System.out.println("-----------------------");
//		System.out.println(indexs);

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
		// sb.append("import com.bowlong.lang.*;\r\n");
		// sb.append("import org.springframework.jdbc.core.*;");
		// sb.append("\r\n");
		// sb.append("import org.springframework.jdbc.core.namedparam.*;");
		// sb.append("\r\n");
		// sb.append("import org.springframework.jdbc.support.*;");
		// sb.append("\r\n");
		sb.append("import ").append(beanPkg).append(".*;\r\n");
		sb.append("import ").append(daoPkg).append(".*;\r\n");
		sb.append("import ").append(entityPkg).append(".*;\r\n");

		if (appcontext != null && !appcontext.isEmpty()) {
			sb.append("import ").append(appcontext).append(";\r\n");
		}
		sb.append("\r\n");

		String shortName = PinYin.getShortPinYin(tableName);
		String UShortName = StrEx.upperFirst(shortName);
		// class
		sb.append("//" + tableName + "\r\n");
		sb.append("@SuppressWarnings({\"rawtypes\", \"unchecked\", \"static-access\"})");
		sb.append("\r\n");
		sb.append("public class " + UShortName + "Internal{\r\n");
		sb.append("\r\n");
		sb.append("    // trueֱ�Ӳ�����ݿ�, false�����ڴ滺��\r\n");
		sb.append("    public static boolean immediately = " + immediately
				+ ";\r\n");
		sb.append("\r\n");
		sb.append("    // ��ʱʱ��(����ʱ���������ݿ����¼������)\r\n");
		sb.append("    public static long LASTTIME = 0;\r\n");
		sb.append("    public static long TIMEOUT = " + UShortName
				+ "Entity.TIMEOUT();\r\n");
		sb.append("\r\n");
		// construct
		sb.append("    public " + UShortName + "Internal(){}\r\n");
		sb.append("\r\n");

		// dao
		sb.append(generateDAO(rsmd, tableName));

		// default
		sb.append(generateDef(rsmd, tableName, indexs));

		// insert
		sb.append(generateInsert(rsmd, tableName));

		// batch insert
		sb.append(generateBatchInsert(rsmd, tableName));

		// delete
		sb.append(generateDelete(rsmd, tableName));

		// Select
		sb.append(generateSelect(rsmd, tableName));

		// selectAll
		sb.append(generateSelectAll(rsmd, tableName));

		// SelectByPage
		sb.append(generateSelectByPage(rsmd, tableName));

		// page count
		sb.append(generatePageCount(rsmd, tableName));

		// count
		// List<MyIndex> myi = MyIndex.Indexs(conn, db, tableName);
		// Map<String, List<String>> indexs2 =
		// com.bowlong.sql.mysql.spring.builder.IndexBuilder
		// .getIndex(conn, rsmd);
		// List<String> ikeys = new Vector<String>();
		// ikeys.addAll(indexs2.keySet());
		// for (String ikey : ikeys) {
		// List<String> idxs = indexs2.get(ikey);
		// System.out.println(ikey);
		// MyIndex mi = null;
		// for (MyIndex i : myi) {
		// if (i.mz.equals(ikey)) {
		// mi = i;
		// break;
		// }
		// }
		// if (!ikey.equals("PRIMARY") && mi != null)
		// if (mi.wy)
		// sb.append(generateIndexCount(rsmd, tableName, idxs, mi));
		// }

		// index
		sb.append(generateIndex(rsmd, tableName, indexs));

		// Update
		sb.append(generateUpdate(rsmd, tableName));

		sb.append("}\r\n");

		return sb.toString();
	}

	static String generateDef(ResultSetMetaData rsmd, String tableName,
			List<MyIndex> indexs) throws SQLException {
		String beanName = StrEx.upperFirst(PinYin.getShortPinYin(tableName));
		String beanNameLower = beanName.toLowerCase();
		String key = AutoIncrement.getAutoIncrement(rsmd);
		if (key == null)
			key = "";
		String keyType = JavaType.getType(rsmd, key);
		StringBuffer sb = new StringBuffer();
		sb.append("    public static final " + beanName + "Internal my = new "
				+ beanName + "Internal();\r\n");
		sb.append("\r\n");
		sb.append("    public static final Map<" + keyType + ", ")
				.append(beanName).append("> vars = newMap();");
		sb.append("\r\n");
		sb.append("\r\n");

		for (MyIndex index : indexs) {
//			System.out.println(index);
			if (!isOnly(indexs, index))
				continue;

			// String ix_mz = index.mz;
			String ix_field = index.field;

			String mz = StrEx.upperFirst(PinYin.getShortPinYin(ix_field));

			String jtype = index.getType(rsmd);

			if (jtype.equals("java.util.Date"))
				continue;

			if (index.wy) {
				String s = "    public static final Map<" + jtype + ", "
						+ keyType + "> varsBy" + mz + " = newMap();";
				sb.append(s);
			} else {

				String s = "    public static final Map<" + jtype + ", Map<"
						+ keyType + ", " + keyType + ">> varsBy" + mz
						+ " = newMap();";
				sb.append(s);
			}

			sb.append("\r\n");
			sb.append("\r\n");
		}

		sb.append("    private static void put(").append(beanName)
				.append(" " + beanNameLower + "){\r\n");
		sb.append(
				"        " + keyType + " " + key + " = " + beanNameLower + ".")
				.append(key).append(";\r\n");
		sb.append("        vars.put(" + key + ", " + beanNameLower + ");\r\n");
		sb.append("\r\n");

		int m1 = 1;
		for (MyIndex index : indexs) {
			if (!isOnly(indexs, index))
				continue;

			// if (!index.wy)
			// continue;
			String mz = PinYin.getShortPinYin(index.field);
			String jtype = index.getType(rsmd);
			if (jtype.equals("Byte"))
				jtype = "byte";
			else if (jtype.equals("Short"))
				jtype = "short";
			else if (jtype.equals("Integer"))
				jtype = "int";
			else if (jtype.equals("Long"))
				jtype = "long";
			else if (jtype.equals("Float"))
				jtype = "float";
			else if (jtype.equals("Double"))
				jtype = "double";

			if (jtype.equals("java.util.Date"))
				continue;

			String mz2 = StrEx.upperFirst(mz);
			sb.append("        " + jtype + " " + mz + " = " + beanNameLower
					+ ".get" + mz2 + "();\r\n");

			if (index.wy) {
				String s = "        varsBy" + mz2 + ".put(" + mz + ", " + key
						+ ");\r\n";
				sb.append(s);
			} else {
				String m = "m" + m1;
				String vbm = "varsBy" + mz2;
				String s = "        Map " + m + " = " + vbm + ".get(" + mz
						+ ");\r\n";
				sb.append(s);
				String s2 = "        if (" + m + " == null) {\r\n";
				sb.append(s2);
				String s3 = "            " + m + " = newMap();\r\n";
				sb.append(s3);
				String s4 = "            " + vbm + ".put(" + mz + ", " + m
						+ ");\r\n";
				sb.append(s4);
				sb.append("        }\r\n");
				sb.append("        " + m + ".put(" + key + ", " + key
						+ ");\r\n");
				m1++;
			}
			sb.append("\r\n");
		}
		sb.append("    }\r\n");

		sb.append("\r\n");
		sb.append("    public static void clear(){\r\n");
		sb.append("        vars.clear();\r\n");
		for (MyIndex index : indexs) {
//			if (index.wy) {
			String jtype = index.getType(rsmd);

			if (jtype.equals("java.util.Date"))
				continue;
			String mz = StrEx
					.upperFirst(PinYin.getShortPinYin(index.field));
			String s = "        varsBy" + mz + ".clear();\r\n";
			sb.append(s);
//			}
		}
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static int count(){\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return count(DAO, DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static int count(String TABLENAME2){\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return count(DAO, TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static int count(" + beanName + "DAO DAO){\r\n");
		sb.append("        return count(DAO, DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static int count(" + beanName + "DAO DAO, String TABLENAME2){\r\n");
		sb.append("        if(immediately){\r\n");
		sb.append("            return DAO.count(TABLENAME2);\r\n");
		sb.append("        }else{\r\n");
		sb.append("            if(isTimeout()){ reloadAll(DAO, TABLENAME2); }\r\n");
		sb.append("            return vars.size();\r\n");
		sb.append("        }\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static void relocate(String TABLENAME2) {\r\n");
		sb.append("        DAO().TABLENAME = TABLENAME2;\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static void relocate(" + beanName + "DAO DAO, String TABLENAME2) {\r\n");
		sb.append("        DAO.TABLENAME = TABLENAME2;\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static String createTableMm() {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return createTableMm(DAO);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static String createTableMm(" + beanName + "DAO DAO) {\r\n");
		sb.append("        String TABLENAME2 = DAO.TABLEMM();\r\n");
		sb.append("        createTable(DAO, TABLENAME2);\r\n");
		sb.append("        return TABLENAME2;\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static String createTableDd() {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return createTableDd(DAO);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static String createTableDd(" + beanName + "DAO DAO) {\r\n");
		sb.append("        String TABLENAME2 = DAO.TABLEDD();\r\n");
		sb.append("        createTable(DAO, TABLENAME2);\r\n");
		sb.append("        return TABLENAME2;\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static void createTable(String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        DAO.createTable(TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static void createTable(" + beanName + "DAO DAO) {\r\n");
		sb.append("        DAO.createTable(DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static void createTable(" + beanName + "DAO DAO, String TABLENAME2) {\r\n");
		sb.append("        DAO.createTable(TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static void reloadAll(String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        relocate(DAO, TABLENAME2);\r\n");
		sb.append("        loadAll(DAO);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static void reloadAll(" + beanName + "DAO DAO) {\r\n");
		sb.append("        relocate(DAO, DAO.TABLENAME);\r\n");
		sb.append("        loadAll(DAO);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");


		sb.append("    public static void reloadAll(" + beanName + "DAO DAO, String TABLENAME2) {\r\n");
		sb.append("        relocate(DAO, TABLENAME2);\r\n");
		sb.append("        loadAll(DAO);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static void loadAll() {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        loadAll(DAO);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");
		
		sb.append("    public static void loadAll(" + beanName + "DAO DAO) {\r\n");
		sb.append("        if(immediately)\r\n");
		sb.append("            return;\r\n");
		sb.append("        clear();\r\n");
		sb.append("        List<" + beanName + "> " + beanNameLower + "s = DAO.selectAll();\r\n");
		sb.append("        for (" + beanName + " " + beanNameLower + " : "
				+ beanNameLower + "s) {\r\n");
		sb.append("            put(" + beanNameLower + ");\r\n");
		sb.append("        }\r\n");
		sb.append("        LASTTIME = System.currentTimeMillis();\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static Map toMap(" + beanName + " " + beanNameLower + "){\r\n");
		sb.append("        Map ret = " + beanNameLower + ".toMap();\r\n");
		sb.append("        return ret;\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("\r\n");
		sb.append("    public static List<Map> toMap(List<" + beanName + "> " + beanNameLower + "s){\r\n");
		sb.append("        List<Map> ret = new Vector<Map>();\r\n");
		sb.append("        for (" + beanName + " " + beanNameLower + " : " + beanNameLower + "s){\r\n");
		sb.append("            Map e = toMap(" + beanNameLower + ");\r\n");
		sb.append("            ret.add(e);\r\n");
		sb.append("        }\r\n");
		sb.append("        return ret;\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		for (MyIndex index : indexs) { // ����
			if (!isOnly(indexs, index))
				continue;
			String ix_field = index.field;
			String mz = StrEx.upperFirst(PinYin.getShortPinYin(ix_field));
			String jtype = index.getType(rsmd);
			if (!jtype.equals("Integer"))
				continue;

			sb.append("    public static List<" + beanName + "> sort"+mz+"(List<"
					+ beanName + "> " + beanNameLower + "s){\r\n");
			sb.append("        Collections.sort(" + beanNameLower
					+ "s, new Comparator<" + beanName + ">(){\r\n");
			sb.append("            public int compare(" + beanName + " o1, "
					+ beanName + " o2) {\r\n");
			sb.append("                int i1 = o1." + key + ".intValue();\r\n");
			sb.append("                int i2 = o2." + key + ".intValue();\r\n");
			sb.append("                return i1 - i2;\r\n");
			sb.append("            }\r\n");
			sb.append("        });\r\n");
			sb.append("        return " + beanNameLower + "s;\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");

			sb.append("    public static List<" + beanName + "> sort"+mz+"Ro(List<"
					+ beanName + "> " + beanNameLower + "s){\r\n");
			sb.append("        Collections.sort(" + beanNameLower
					+ "s, new Comparator<" + beanName + ">(){\r\n");
			sb.append("            public int compare(" + beanName + " o1, "
					+ beanName + " o2) {\r\n");
			sb.append("                int i1 = o1." + key + ".intValue();\r\n");
			sb.append("                int i2 = o2." + key + ".intValue();\r\n");
			sb.append("                return i2 - i1;\r\n");
			sb.append("            }\r\n");
			sb.append("        });\r\n");
			sb.append("        return " + beanNameLower + "s;\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");
		}
		
		for (MyIndex index : indexs) { // ��������
			if (!isOnly(indexs, index))
				continue;
			String ix_field = index.field;
			String mz = StrEx.upperFirst(PinYin.getShortPinYin(ix_field));
			String jtype = index.getType(rsmd);
			if (!jtype.equals("java.util.Date"))
				continue;

			sb.append("    public static List<" + beanName + "> sort"+mz+"(List<"
					+ beanName + "> " + beanNameLower + "s){\r\n");
			sb.append("        Collections.sort(" + beanNameLower
					+ "s, new Comparator<" + beanName + ">(){\r\n");
			sb.append("            public int compare(" + beanName + " o1, "
					+ beanName + " o2) {\r\n");
			sb.append("                "+jtype+" i1 = o1.get" + mz + "();\r\n");
			sb.append("                "+jtype+" i2 = o2.get" + mz + "();\r\n");
			sb.append("                return i2.before(i1) ? 1 : -1;\r\n");
			sb.append("            }\r\n");
			sb.append("        });\r\n");
			sb.append("        return " + beanNameLower + "s;\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");

			sb.append("    public static List<" + beanName + "> sort"+mz+"2(List<"
					+ beanName + "> " + beanNameLower + "s){\r\n");
			sb.append("        Collections.sort(" + beanNameLower
					+ "s, new Comparator<" + beanName + ">(){\r\n");
			sb.append("            public int compare(" + beanName + " o1, "
					+ beanName + " o2) {\r\n");
			sb.append("                "+jtype+" i1 = o1.get" + mz + "();\r\n");
			sb.append("                "+jtype+" i2 = o2.get" + mz + "();\r\n");
			sb.append("                return i1.before(i2) ? 1 : -1;\r\n");
			sb.append("            }\r\n");
			sb.append("        });\r\n");
			sb.append("        return " + beanNameLower + "s;\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");

		}
		
		sb.append("    public static List<" + beanName + "> sort(List<"
				+ beanName + "> " + beanNameLower + "s){\r\n");
		sb.append("        Collections.sort(" + beanNameLower
				+ "s, new Comparator<" + beanName + ">(){\r\n");
		sb.append("            public int compare(" + beanName + " o1, "
				+ beanName + " o2) {\r\n");
		sb.append("                int i1 = o1." + key + ".intValue();\r\n");
		sb.append("                int i2 = o2." + key + ".intValue();\r\n");
		sb.append("                return i1 - i2;\r\n");
		sb.append("            }\r\n");
		sb.append("        });\r\n");
		sb.append("        return " + beanNameLower + "s;\r\n");
		sb.append("    }\r\n");

		sb.append("    public static List<" + beanName + "> sortReverse(List<"
				+ beanName + "> " + beanNameLower + "s){\r\n");
		sb.append("        Collections.sort(" + beanNameLower
				+ "s, new Comparator<" + beanName + ">(){\r\n");
		sb.append("            public int compare(" + beanName + " o1, "
				+ beanName + " o2) {\r\n");
		sb.append("                int i1 = o1." + key + ".intValue();\r\n");
		sb.append("                int i2 = o2." + key + ".intValue();\r\n");
		sb.append("                return i2 - i1;\r\n");
		sb.append("            }\r\n");
		sb.append("        });\r\n");
		sb.append("        return " + beanNameLower + "s;\r\n");
		sb.append("    }\r\n");

		sb.append("\r\n");

		return sb.toString();
	}

	static String generateDAO(ResultSetMetaData rsmd, String tableName) {
		String beanName = StrEx.upperFirst(PinYin.getShortPinYin(tableName));

		StringBuffer sb = new StringBuffer();

		// default
		sb.append("    public static ").append(beanName).append("DAO ")
				.append("DAO");
		sb.append("(){\r\n");
		sb.append("        return AppContext." + beanName + "DAO(); \r\n");
		sb.append("        //return new " + (beanName)
				+ "DAO(AppContext.ds()); \r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		return sb.toString();
	}

	private static String generateInsert(ResultSetMetaData rsmd,
			String tableName) throws SQLException {
		StringBuffer sb = new StringBuffer();

		String key = AutoIncrement.getAutoIncrement(rsmd);
		if (key == null)
			key = "";
		String javaType = JavaType.getType(rsmd, key);
		String beanName = StrEx.upperFirst(PinYin.getShortPinYin(tableName));
		String beanNameLower = beanName.toLowerCase();
		// insert
		sb.append("    public static " + beanName + " insert(" + beanName + " "
				+ beanNameLower + ") {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return insert(DAO, " + beanNameLower + ", DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// insert
		sb.append("    public static " + beanName + " insert(" + beanName + "DAO DAO, " + beanName + " "
				+ beanNameLower + ") {\r\n");
		sb.append("        return insert(DAO, " + beanNameLower + ", DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// insert
		sb.append("    public static " + beanName + " insert(" + beanName + " "
				+ beanNameLower + ", String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return insert(DAO, " + beanNameLower + ", TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// insert
		sb.append("    public static " + beanName + " insert(" + beanName + "DAO DAO, " + beanName
				+ " " + beanNameLower + ", String TABLENAME2) {\r\n");
		sb.append("        int n = DAO.insert(" + beanNameLower + ", TABLENAME2);\r\n");
		sb.append("        if(n <= 0)\r\n");
		sb.append("            return null;\r\n");
		sb.append("\r\n");
		sb.append("        " + beanNameLower + "." + key + " = new " + javaType + "(n);\r\n");
		sb.append("\r\n");
		sb.append("        if(!immediately)\r\n");
		sb.append("            put(" + beanNameLower + ");\r\n");
		sb.append("\r\n");
		sb.append("        return " + beanNameLower + ";\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");
		///////////////////////////////////////////////////////////////////////
		// insert2
		sb.append("    public static " + beanName + " insert2(" + beanName + " " + beanNameLower + ") {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return insert2(DAO, " + beanNameLower + ", DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// insert2
		sb.append("    public static " + beanName + " insert2(" + beanName + "DAO DAO, " + beanName
				+ " " + beanNameLower + ") {\r\n");
		sb.append("        return insert2(DAO, " + beanNameLower + ", DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// insert2
		sb.append("    public static " + beanName + " insert2(" + beanName
				+ " " + beanNameLower + ", String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return insert2(DAO, " + beanNameLower + ", TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// insert2
		sb.append("    public static " + beanName + " insert2(" + beanName + "DAO DAO, " + beanName
				+ " " + beanNameLower + ", String TABLENAME2) {\r\n");
		sb.append("        int n = DAO.insert2(" + beanNameLower + ", TABLENAME2);\r\n");
		sb.append("        if(n <= 0)\r\n");
		sb.append("            return null;\r\n");
		sb.append("\r\n");
		sb.append("        if(!immediately)\r\n");
		sb.append("            put(" + beanNameLower + ");\r\n");
		sb.append("\r\n");
		sb.append("        return " + beanNameLower + ";\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		return sb.toString();
	}

	///////////////////////////////////////////////////////////////////////
	private static String generateBatchInsert(ResultSetMetaData rsmd,
			String tableName) throws SQLException {
		StringBuffer sb = new StringBuffer();

		String key = AutoIncrement.getAutoIncrement(rsmd);
		if (key == null)
			key = "";
		// String javaType = JavaType.getType(rsmd, key);
		String beanName = StrEx.upperFirst(PinYin.getShortPinYin(tableName));
		String beanNameLower = beanName.toLowerCase();
		// insert
		sb.append("    public static int[] insert(List<" + beanName + "> " + beanNameLower + "s) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return insert(DAO, " + beanNameLower + "s, DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// insert
		sb.append("    public static int[] insert(" + beanName + "DAO DAO, List<" + beanName + "> "
				+ beanNameLower + "s) {\r\n");
		sb.append("        return insert(DAO, " + beanNameLower + "s, DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// insert
		sb.append("    public static int[] insert(List<" + beanName + "> "
				+ beanNameLower + "s, String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return insert(DAO, " + beanNameLower + "s, TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// insert
		sb.append("    public static int[] insert(" + beanName + "DAO DAO, List<" + beanName + "> "
				+ beanNameLower + "s, String TABLENAME2) {\r\n");
		sb.append("        if(immediately){\r\n");
		sb.append("            return DAO.insert(" + beanNameLower + "s, TABLENAME2);\r\n");
		sb.append("        }else{\r\n");
		sb.append("            int[] ret = new int[" + beanNameLower + "s.size()];\r\n");
		sb.append("            int n=0;\r\n");
		sb.append("            for(" + beanName + " " + beanNameLower + " : " + beanNameLower + "s){\r\n");
		sb.append("                " + beanNameLower + " = insert(DAO, " + beanNameLower + ", TABLENAME2);\r\n");
		sb.append("                ret[n++] = (" + beanNameLower + "!=null) ? 1 : 0;\r\n");
		sb.append("            }\r\n");
		sb.append("            return ret;\r\n");
		sb.append("        }\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		return sb.toString();
	}
	///////////////////////////////////////////////////////////////////////

	private static String generateDelete(ResultSetMetaData rsmd,
			String tableName) throws SQLException {
		StringBuffer sb = new StringBuffer();
		String key = AutoIncrement.getAutoIncrement(rsmd);
		if (key == null)
			return "";
		 String beanName = StrEx.upperFirst(PinYin.getShortPinYin(tableName));

		// delete
		String javaType = JavaType.getType(rsmd, key);
		sb.append("    public static int delete(" + javaType + " " + key + ") {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return delete(DAO, " + key + ", DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static int delete(" + beanName + "DAO DAO, " + javaType + " " + key
				+ ") {\r\n");
		sb.append("        return delete(DAO, " + key + ", DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static int delete(" + javaType + " " + key
				+ ", String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return delete(DAO, " + key + ", TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static int delete(" + beanName + "DAO DAO, " + javaType + " " + key
				+ ", String TABLENAME2) {\r\n");
		sb.append("        int n = DAO.deleteByKey(" + key + ", TABLENAME2);\r\n");
		sb.append("        if(n <= 0)\r\n");
		sb.append("            return 0;\r\n");
		sb.append("        if(!immediately)\r\n");
		sb.append("            vars.remove(" + key + ");\r\n");
		sb.append("        return n;\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		///////////////////////////////////////////////////////////////////////
		// sb.append("    public static int[] delete(" + javaType + "[] " + key
		// + "s) {\r\n");
		// sb.append("        return delete("+key+"s, DAO().TABLENAME);\r\n");
		// sb.append("    }\r\n");
		// sb.append("\r\n");
		sb.append("    public static int[] delete(" + javaType + "[] " + key
				+ "s) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return delete(DAO, " + key + "s, DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static int[] delete(" + beanName + "DAO DAO, " + javaType + "[] " + key
				+ "s) {\r\n");
		sb.append("        return delete(DAO, " + key + "s, DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static int[] delete(" + javaType + "[] " + key
				+ "s,String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return delete(DAO, " + key + "s, TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static int[] delete(" + beanName + "DAO DAO, " + javaType + "[] " + key
				+ "s,String TABLENAME2) {\r\n");
		sb.append("        if(immediately){\r\n");
		sb.append("            return DAO.deleteByKey(" + key + "s, TABLENAME2);\r\n");
		sb.append("        }else{\r\n");
		sb.append("            int[] ret = new int[" + key + "s.length];\r\n");
		sb.append("            int n=0;\r\n");
		sb.append("            for(" + javaType + " " + key + " : " + key + "s){\r\n");
		sb.append("                ret[n++] = delete(DAO, " + key + ", TABLENAME2);\r\n");
		sb.append("            }\r\n");
		sb.append("            return ret;\r\n");
		sb.append("        }\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		///////////////////////////////////////////////////////////////////////
		
		sb.append("    public static int delete2(" + javaType + " " + key + ") {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return delete2(DAO, " + key + ", DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static int delete2(" + beanName + "DAO DAO, " + javaType + " " + key
				+ ") {\r\n");
		sb.append("        return delete2(DAO, " + key + ", DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static int delete2(" + javaType + " " + key
				+ ", String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return delete2(DAO, " + key + ", TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static int delete2(final " + beanName + "DAO DAO, final " + javaType + " " + key
				+ ",final String TABLENAME2) {\r\n");
		sb.append("        SqlEx.execute4Fixed(new Runnable() {\r\n");
		sb.append("            public void run() {\r\n");
		sb.append("                try {\r\n");
		sb.append("                    DAO.deleteByKey(" + key + ", TABLENAME2);\r\n");
		sb.append("                } catch (Exception e) {\r\n");
		sb.append("                    e.printStackTrace();\r\n");
		sb.append("                }\r\n");
		sb.append("            }\r\n");
		sb.append("        });\r\n");
		sb.append("        if(!immediately)\r\n");
		sb.append("            vars.remove(" + key + ");\r\n");
		sb.append("        return 1;\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		///////////////////////////////////////////////////////////////////////

		sb.append("    public static int[] delete2(" + javaType + "[] "
				+ key + "s) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return delete2(DAO, " + key + "s, DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static int[] delete2(" + beanName + "DAO DAO, " + javaType + "[] "
				+ key + "s) {\r\n");
		sb.append("        return delete2(DAO, " + key + "s, DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static int[] delete2(" + javaType + "[] "
				+ key + "s, String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return delete2(DAO, " + key + "s, TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static int[] delete2(final " + beanName + "DAO DAO, final " + javaType + "[] "
				+ key + "s,final String TABLENAME2) {\r\n");
		sb.append("        if(immediately){\r\n");
		sb.append("            int[] ret = new int[" + key + "s.length];\r\n");
		sb.append("            SqlEx.execute4Fixed(new Runnable() {\r\n");
		sb.append("                public void run() {\r\n");
		sb.append("                    try {\r\n");
		sb.append("                        DAO.deleteByKey(" + key
				+ "s, TABLENAME2);\r\n");
		sb.append("                    } catch (Exception e) {\r\n");
		sb.append("                        e.printStackTrace();\r\n");
		sb.append("                    }\r\n");
		sb.append("                }\r\n");
		sb.append("            });\r\n");
		sb.append("            for(int i=0;i<ret.length;i++)\r\n");
		sb.append("                ret[i] = 1;;\r\n");
		sb.append("            return ret;\r\n");
		sb.append("        }else{\r\n");
		sb.append("            int[] ret = new int[" + key + "s.length];\r\n");
		sb.append("            int i = 0;\r\n");
		sb.append("            for(" + javaType + " " + key + " : " + key
				+ "s){\r\n");
		sb.append("                ret[i] = delete2(DAO, " + key
				+ ", TABLENAME2);\r\n");
		sb.append("            }\r\n");
		sb.append("            return ret;\r\n");
		sb.append("        }\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		return sb.toString();
	}
	///////////////////////////////////////////////////////////////////////

	static String generateSelectAll(ResultSetMetaData rsmd, String tableName)
			throws SQLException {
		StringBuffer sb = new StringBuffer();
		String key = AutoIncrement.getAutoIncrement(rsmd);
		if (key == null)
			key = "";
		String shortName = PinYin.getShortPinYin(tableName);
		String beanName = StrEx.upperFirst(shortName);

		sb.append("    public static List<" + beanName + "> getAll() {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return getAll(DAO, DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static List<" + beanName + "> getAll(" + beanName + "DAO DAO) {\r\n");
		sb.append("        return getAll(DAO, DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static List<" + beanName + "> getAll(String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return getAll(DAO, TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static List<" + beanName + "> getAll(" + beanName + "DAO DAO, String TABLENAME2) {\r\n");
		sb.append("        List<" + beanName + "> ret = new Vector<" + beanName
				+ ">();\r\n");
		sb.append("        if(immediately){\r\n");
		sb.append("            ret = DAO.selectAll(TABLENAME2);\r\n");
		sb.append("        }else{\r\n");
		sb.append("            if(isTimeout()){ reloadAll(DAO, TABLENAME2); }\r\n");
		sb.append("            ret.addAll(vars.values());\r\n");
		sb.append("        }\r\n");
		sb.append("        return sort(ret);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");
		///////////////////////////////////////////////////////////////////////

		sb.append("    public static List<" + beanName + "> getLast(int num) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return getLast(DAO, num, DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static List<" + beanName + "> getLast(" + beanName + "DAO DAO, int num) {\r\n");
		sb.append("        return getLast(DAO, num, DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static List<" + beanName + "> getLast(int num, String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return getLast(DAO, num, TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static List<" + beanName + "> getLast(" + beanName + "DAO DAO, int num, String TABLENAME2) {\r\n");
		sb.append("        List<" + beanName + "> ret = new Vector<" + beanName + ">();\r\n");
		sb.append("        if(immediately){\r\n");
		sb.append("            ret = DAO.selectLast(num, TABLENAME2);\r\n");
		sb.append("        }else{\r\n");
		sb.append("            if(isTimeout()){ reloadAll(DAO, TABLENAME2); }\r\n");
		sb.append("            ret.addAll(vars.values());\r\n");
		sb.append("            ret = sortReverse(ret);\r\n");
		sb.append("        }\r\n");
		sb.append("        if(ret.size() > num){\r\n");
		sb.append("            ret = ret.subList(0, num);\r\n");
		sb.append("        }\r\n");
		sb.append("        return ret;\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		return sb.toString();
	}
	///////////////////////////////////////////////////////////////////////

	static String generateSelectByPage(ResultSetMetaData rsmd, String tableName)
			throws SQLException {
		StringBuffer sb = new StringBuffer();
		String key = AutoIncrement.getAutoIncrement(rsmd);
		if (key == null)
			key = "";
		String shortName = PinYin.getShortPinYin(tableName);
		String beanName = StrEx.upperFirst(shortName);
		sb.append("    public static List<" + beanName + "> getByPage(int page, int size) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return getByPage(DAO, page, size, DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static List<" + beanName
				+ "> getByPage(" + beanName + "DAO DAO, int page, int size) {\r\n");
		sb.append("        return getByPage(DAO, page, size, DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static List<" + beanName
				+ "> getByPage(int page, int size, String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return getByPage(DAO, page, size, TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static List<" + beanName
				+ "> getByPage(" + beanName + "DAO DAO, int page, int size, String TABLENAME2) {\r\n");
		sb.append("        if(immediately){\r\n");
		sb.append("            int begin = page * size;\r\n");
		sb.append("            int num = size;\r\n");
		sb.append("            return DAO.selectByPage(begin, num, TABLENAME2);\r\n");
		sb.append("        }else{\r\n");
		sb.append("            if(isTimeout()){ reloadAll(DAO, TABLENAME2); }\r\n");
		sb.append("            List<" + beanName + "> v = getAll(DAO, TABLENAME2);\r\n");
		sb.append("            return SqlEx.selectByPage(v, page, size);\r\n");
		sb.append("        }\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");
		
		return sb.toString();
	}
	///////////////////////////////////////////////////////////////////////

	static String generatePageCount(ResultSetMetaData rsmd, String tableName)
			throws SQLException {
		StringBuffer sb = new StringBuffer();
		String key = AutoIncrement.getAutoIncrement(rsmd);
		if (key == null)
			key = "";
		 String beanName = StrEx.upperFirst(PinYin.getShortPinYin(tableName));
		sb.append("    public static int pageCount(int size) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return pageCount(DAO, size, DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static int pageCount(" + beanName + "DAO DAO, int size) {\r\n");
		sb.append("        return pageCount(DAO, size, DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");

		sb.append("    public static int pageCount(int size, String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return pageCount(DAO, size, TABLENAME2);\r\n");
		sb.append("    }\r\n");

		sb.append("    public static int pageCount(" + beanName + "DAO DAO, int size, String TABLENAME2) {\r\n");
		sb.append("        int v = 0;\r\n");
		sb.append("        if(immediately){\r\n");
		sb.append("            v = DAO.count(TABLENAME2);\r\n");
		sb.append("        }else{\r\n");
		sb.append("            if(isTimeout()){ reloadAll(DAO, TABLENAME2); }\r\n");
		sb.append("            v = count(DAO, TABLENAME2);\r\n");
		sb.append("        }\r\n");
		sb.append("        return SqlEx.pageCount(v, size);\r\n");
		sb.append("    }\r\n");
		
		sb.append("\r\n");
		return sb.toString();
	}
	///////////////////////////////////////////////////////////////////////

	// static String generateIndexCount(ResultSetMetaData rsmd, String
	// tableName,
	// List<String> indexs, MyIndex mi) throws SQLException {
	// if (indexs.size() < 1)
	// return "";
	// // String beanName = StrEx.upperFirst(PinYin.getShortPinYin(tableName));
	// StringBuffer sb = new StringBuffer();
	// String ikey = AutoIncrement.getAutoIncrement(rsmd);
	// if (ikey == null)
	// ikey = "";
	//
	// if (mi.wy)
	// return sb.toString();
	//
	// StringBuffer ukey = new StringBuffer();
	// int ii = 0;
	// for (String key : indexs) {
	// key = indexs.get(ii++);
	// ukey.append(StrEx.upperFirst(PinYin.getShortPinYin(key)));
	// }
	//
	// // String shortName = PinYin.getShortPinYin(tableName);
	// // String beanName = StrEx.upperFirst(shortName);
	// sb.append("    public static int countBy" + ukey + "(");
	// ii = 0;
	// for (String key : indexs) {
	// key = indexs.get(ii++);
	// String shortKey = PinYin.getShortPinYin(key);
	// String keyJavaType = JavaType.getType(rsmd, key);
	// if (ii <= 1) {
	// sb.append("");
	// } else {
	// sb.append(", ");
	// }
	// sb.append(keyJavaType);
	// sb.append(" ");
	// sb.append(shortKey);
	// }
	// sb.append(") {\r\n");
	// sb.append("    return countBy" + ukey + "(");
	// ii = 0;
	// for (String key : indexs) {
	// key = indexs.get(ii++);
	// String shortKey = PinYin.getShortPinYin(key);
	// if (ii <= 1) {
	// sb.append("");
	// } else {
	// sb.append(", ");
	// }
	// sb.append(shortKey);
	// }
	// sb.append(", DAO().TABLENAME);\r\n");
	// sb.append("    }\r\n");
	// sb.append("\r\n");
	//
	// sb.append("    public static int countBy" + ukey + "(");
	// ii = 0;
	// for (String key : indexs) {
	// key = indexs.get(ii++);
	// String shortKey = PinYin.getShortPinYin(key);
	// String keyJavaType = JavaType.getType(rsmd, key);
	// if (ii <= 1) {
	// sb.append("");
	// } else {
	// sb.append(", ");
	// }
	// sb.append(keyJavaType);
	// sb.append(" ");
	// sb.append(shortKey);
	// }
	// sb.append(", String TABLENAME2) {\r\n");
	// sb.append("        if(immediately){\r\n");
	// sb.append("            return DAO().countBy" + ukey + "(");
	// ii = 0;
	// for (String key : indexs) {
	// key = indexs.get(ii++);
	// String shortKey = PinYin.getShortPinYin(key);
	// if (ii <= 1) {
	// sb.append("");
	// } else {
	// sb.append(", ");
	// }
	// sb.append(shortKey);
	// }
	// sb.append(", TABLENAME2);\r\n");
	// sb.append("        }else{\r\n");
	// sb.append("            if(isTimeout()){ reloadAll(TABLENAME2); }\r\n");
	// sb.append("            List l1 = getBy" + ukey + "(");
	// ii = 0;
	// for (String key : indexs) {
	// key = indexs.get(ii++);
	// String shortKey = PinYin.getShortPinYin(key);
	// if (ii <= 1) {
	// sb.append("");
	// } else {
	// sb.append(", ");
	// }
	// sb.append(shortKey);
	// }
	// sb.append(");\r\n");
	//
	// sb.append("            return l1.size();\r\n");
	// sb.append("        }\r\n");
	// sb.append("    }\r\n");
	// sb.append("\r\n");
	// return sb.toString();
	// }

	static String generateSelect(ResultSetMetaData rsmd, String tableName)
			throws SQLException {
		StringBuffer sb = new StringBuffer();

		String key = AutoIncrement.getAutoIncrement(rsmd);
		if (key == null)
			return "";

		String shortName = PinYin.getShortPinYin(tableName);
		String beanName = StrEx.upperFirst(shortName);

		String keyJavaType = JavaType.getType(rsmd, key);

		// Connection
		sb.append("    public static " + beanName + " getByKey(" + keyJavaType
				+ " " + key + ") {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return getByKey(DAO, " + key + ", DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static " + beanName + " getByKey(" + beanName + "DAO DAO, " + keyJavaType
				+ " " + key + ") {\r\n");
		sb.append("        return getByKey(DAO, " + key + ", DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static " + beanName + " getByKey(" + keyJavaType
				+ " " + key + ", String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return getByKey(DAO, " + key + ", TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static " + beanName + " getByKey(" + beanName + "DAO DAO, " + keyJavaType
				+ " " + key + ", String TABLENAME2) {\r\n");
		sb.append("        if(immediately)\r\n");
		sb.append("            return DAO.selectByKey(" + key
				+ ", TABLENAME2);\r\n");
		sb.append("        else{\r\n");
		sb.append("            if(isTimeout()){ reloadAll(DAO, TABLENAME2); }\r\n");
		sb.append("            return vars.get(" + key + ");\r\n");
		sb.append("        }\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		///////////////////////////////////////////////////////////////////////
		String jtype = keyJavaType;
		if (keyJavaType.equals("Byte"))
			jtype = "byte";
		else if (jtype.equals("Short"))
			jtype = "short";
		else if (jtype.equals("Integer"))
			jtype = "int";
		else if (jtype.equals("Long"))
			jtype = "long";
		else if (jtype.equals("Float"))
			jtype = "float";
		else if (jtype.equals("Double"))
			jtype = "double";

		sb.append("    public static List<" + beanName + "> getGtKey(" + jtype
				+ " " + key + ") {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return getGtKey(DAO, " + key + ", DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static List<" + beanName + "> getGtKey(" + beanName + "DAO DAO, " + jtype
				+ " " + key + ") {\r\n");
		sb.append("        return getGtKey(DAO, " + key + ", DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public static List<" + beanName + "> getGtKey(" + jtype
				+ " " + key + ", String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return getGtKey(DAO, " + key + ", TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");
		
		sb.append("    public static List<" + beanName + "> getGtKey(" + beanName + "DAO DAO, " + jtype
				+ " " + key + ", String TABLENAME2) {\r\n");
		sb.append("        if(immediately)\r\n");
		sb.append("            return DAO.selectGtKey(" + key
				+ ", TABLENAME2);\r\n");
		sb.append("        else{\r\n");
		sb.append("            if(isTimeout()){ reloadAll(DAO, TABLENAME2); }\r\n");
		sb.append("            List<" + beanName + "> ret = newList();\r\n");
		sb.append("            List<" + beanName + "> all = getAll(DAO, TABLENAME2);\r\n");
		sb.append("            for(" + beanName + " e : all){\r\n");
		sb.append("                " + jtype + " vid = e." + key + ";\r\n");
		sb.append("                if(vid > " + key + ")\r\n");
		sb.append("                    ret.add(e);\r\n");
		sb.append("            }\r\n");
		sb.append("            return ret;\r\n");
		sb.append("        }\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		return sb.toString();
	}
	///////////////////////////////////////////////////////////////////////

	static String generateIndex(ResultSetMetaData rsmd, String tableName,
			List<MyIndex> indexs) throws SQLException {
		StringBuffer sb = new StringBuffer();

		String key = AutoIncrement.getAutoIncrement(rsmd);
		if (key == null)
			return "";

		String shortName = PinYin.getShortPinYin(tableName);
		String beanName = StrEx.upperFirst(shortName);

		String keyJavaType = JavaType.getType(rsmd, key);

		for (MyIndex i : indexs) {
			if (!isOnly(indexs, i))
				continue;
			String mz = PinYin.getShortPinYin(i.field);
			String jtype = i.getType(rsmd);
			String mz2 = StrEx.upperFirst(mz);
			String vbm = "varsBy" + mz2;

			if (jtype.equals("Byte"))
				jtype = "byte";
			else if (jtype.equals("Short"))
				jtype = "short";
			else if (jtype.equals("Integer"))
				jtype = "int";
			else if (jtype.equals("Long"))
				jtype = "long";
			else if (jtype.equals("Float"))
				jtype = "float";
			else if (jtype.equals("Double"))
				jtype = "double";

			if (jtype.equals("java.util.Date"))
				continue;

			if (!i.wy)
				continue;

			// getBy
			sb.append("    public static " + beanName + " getBy" + mz2 + "("
					+ jtype + " " + mz + ") {\r\n");
			sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
			sb.append("    return getBy" + mz2 + "(DAO, " + mz + ", DAO.TABLENAME);\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");

			// getBy
			sb.append("    public static " + beanName + " getBy" + mz2 + "(" + beanName + "DAO DAO, "
					+ jtype + " " + mz + ") {\r\n");
			sb.append("    return getBy" + mz2 + "(DAO, " + mz + ", DAO.TABLENAME);\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");

			// getBy
			sb.append("    public static " + beanName + " getBy" + mz2 + "("
					+ jtype + " " + mz + ", String TABLENAME2) {\r\n");
			sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
			sb.append("    return getBy" + mz2 + "(DAO, " + mz + ", TABLENAME2);\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");
		
			sb.append("    public static " + beanName + " getBy" + mz2 + "(" + beanName + "DAO DAO, "
					+ jtype + " " + mz + ", String TABLENAME2) {\r\n");
			sb.append("        if(immediately){\r\n");
			sb.append("            return DAO.selectBy" + mz2 + "(" + mz
					+ ", TABLENAME2);\r\n");
			sb.append("        }else{\r\n");
			sb.append("            if(isTimeout()){ reloadAll(DAO, TABLENAME2); }\r\n");
			sb.append("            " + keyJavaType + " " + key + " = " + vbm
					+ ".get(" + mz + ");\r\n");
			;
			sb.append("            if(" + key + " == null){\r\n");
			sb.append("                " + vbm + ".remove(" + mz + ");\r\n");
			sb.append("                return null;\r\n");
			sb.append("            }\r\n");
			sb.append("            return getByKey(" + key + ");\r\n");
			sb.append("        }\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");

		}

		for (MyIndex i : indexs) {
			if (!isOnly(indexs, i))
				continue;

			String mz = PinYin.getShortPinYin(i.field);
			String jtype = i.getType(rsmd);
			String mz2 = StrEx.upperFirst(mz);
			String vbm = "varsBy" + mz2;
			if (i.wy)
				continue;
			if (jtype.equals("Byte"))
				jtype = "byte";
			else if (jtype.equals("Short"))
				jtype = "short";
			else if (jtype.equals("Integer"))
				jtype = "int";
			else if (jtype.equals("Long"))
				jtype = "long";
			else if (jtype.equals("Float"))
				jtype = "float";
			else if (jtype.equals("Double"))
				jtype = "double";

			if (jtype.equals("java.util.Date"))
				continue;

			// countBy
			sb.append("    public static int countBy" + mz2 + "(" + jtype
					+ " " + mz + ") {\r\n");
			sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
			sb.append("        return countBy" + mz2 + "(DAO, " + mz+ ", DAO.TABLENAME);\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");

			// countBy
			sb.append("    public static int countBy" + mz2 + "(" + beanName + "DAO DAO, " + jtype
					+ " " + mz + ") {\r\n");
			sb.append("        return countBy" + mz2 + "(DAO, " + mz+ ", DAO.TABLENAME);\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");

			// countBy
			sb.append("    public static int countBy" + mz2 + "(" + jtype
					+ " " + mz + ", String TABLENAME2) {\r\n");
			sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
			sb.append("        return countBy" + mz2 + "(DAO, " + mz+ ", TABLENAME2);\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");

			// countBy
			sb.append("    public static int countBy" + mz2 + "(" + beanName + "DAO DAO, " + jtype
					+ " " + mz + ", String TABLENAME2) {\r\n");
			sb.append("        if(immediately){\r\n");
			sb.append("            return DAO.countBy" + mz2 + "(" + mz
					+ ", TABLENAME2);\r\n");
			sb.append("        }else{\r\n");
			sb.append("            List<" + beanName + "> vs = getBy" + mz2+ "(DAO, " + mz + ", TABLENAME2);\r\n");
			sb.append("            return vs.size();\r\n");
			sb.append("        }\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");

			///////////////////////////////////////////////////////////////////////
			
			// getBy
			sb.append("    public static List<" + beanName + "> getBy" + mz2
					+ "(" + jtype + " " + mz + ") {\r\n");
			sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
			sb.append("        return getBy" + mz2 + "(DAO, " + mz + ", DAO.TABLENAME);\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");

			// getBy
			sb.append("    public static List<" + beanName + "> getBy" + mz2
					+ "(" + beanName + "DAO DAO, " + jtype + " " + mz + ") {\r\n");
			sb.append("        return getBy" + mz2 + "(DAO, " + mz + ", DAO.TABLENAME);\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");			
			
			// getBy
			sb.append("    public static List<" + beanName + "> getBy" + mz2
					+ "(" + jtype + " " + mz + ", String TABLENAME2) {\r\n");
			sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
			sb.append("        return getBy" + mz2 + "(DAO, " + mz + ", TABLENAME2);\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");			
			
			// getBy
			sb.append("    public static List<" + beanName + "> getBy" + mz2
					+ "(" + beanName + "DAO DAO, " + jtype + " " + mz + ", String TABLENAME2) {\r\n");
			sb.append("        if(immediately){\r\n");
			sb.append("            return DAO.selectBy" + mz2 + "(" + mz
					+ ", TABLENAME2);\r\n");
			sb.append("        }else{\r\n");
			sb.append("            if(isTimeout()){ reloadAll(DAO, TABLENAME2); }\r\n");
			sb.append("            List<" + beanName + "> ret = newList();\r\n");
			sb.append("            Map m1 = " + vbm + ".get(" + mz + ");\r\n");
			sb.append("            if (m1 == null || m1.isEmpty())\r\n");
			sb.append("                return ret;\r\n");
			sb.append("            List<" + keyJavaType
					+ "> list = newList();\r\n");
			sb.append("            list.addAll(m1.values());\r\n");
			sb.append("            for (" + keyJavaType + " " + key
					+ " : list) {\r\n");
			sb.append("                " + beanName + " e = getByKey(DAO, " + key
					+ ", TABLENAME2);\r\n");
			sb.append("                if(e == null){\r\n");
			sb.append("                    m1.remove(" + key + ");\r\n");
			sb.append("                }else{\r\n");
			sb.append("                    " + jtype + " " + mz + "2 = e.get"
					+ mz2 + "(); \r\n");
			if (jtype.equals("String"))
				sb.append("                    if(!" + mz + "2.equals(" + mz
						+ ")){ \r\n");
			else
				sb.append("                    if(" + mz + "2 != " + mz
						+ "){ \r\n");
			sb.append("                        m1.remove(" + key + ");\r\n");
			sb.append("                    }else{\r\n");
			sb.append("                        ret.add(e);\r\n");
			sb.append("                    }\r\n");
			sb.append("                }\r\n");
			sb.append("            }\r\n");
			sb.append("            return ret;\r\n");
			sb.append("        }\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");
		}
		return sb.toString();
	}
	///////////////////////////////////////////////////////////////////////

	private static String generateUpdate(ResultSetMetaData rsmd,
			String tableName) throws SQLException {

		StringBuffer sb = new StringBuffer();
		String key = AutoIncrement.getAutoIncrement(rsmd);
		String beanName = StrEx.upperFirst(PinYin.getShortPinYin(tableName));
		String beanNameLower = beanName.toLowerCase();
		if (key == null)
			return "";

		// update
		sb.append("    public static " + beanName + " update" + "(" + beanName
				+ " " + beanNameLower + ") {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return update(DAO, " + beanNameLower
				+ ", DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// update
		sb.append("    public static " + beanName + " update" + "(" + beanName + "DAO DAO, " + beanName
				+ " " + beanNameLower + ") {\r\n");
		sb.append("        return update(DAO, " + beanNameLower + ", DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// update
		sb.append("    public static " + beanName + " update" + "(" + beanName
				+ " " + beanNameLower + ", String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return update(DAO, " + beanNameLower + ", TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// update
		sb.append("    public static " + beanName + " update" + "(" + beanName + "DAO DAO, " + beanName
				+ " " + beanNameLower + ", String TABLENAME2) {\r\n");
		sb.append("        int n = DAO.updateByKey(" + beanNameLower
				+ ", TABLENAME2);\r\n");
		sb.append("        if(n <= 0)\r\n");
		sb.append("            return null;\r\n");
		sb.append("        if(!immediately)\r\n");
		sb.append("            put(" + beanNameLower + ");\r\n");
		sb.append("        return " + beanNameLower + ";\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		///////////////////////////////////////////////////////////////////////
		// update
		sb.append("    public static int[] update" + "(List<" + beanName + "> "
				+ beanNameLower + "s) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return update(DAO, " + beanNameLower + "s, DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// update
		sb.append("    public static int[] update" + "(" + beanName + "DAO DAO, List<" + beanName + "> "
				+ beanNameLower + "s) {\r\n");
		sb.append("        return update(DAO, " + beanNameLower + "s, DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// update
		sb.append("    public static int[] update" + "(List<" + beanName + "> "
				+ beanNameLower + "s, String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return update(DAO, " + beanNameLower + "s, TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// update
		sb.append("    public static int[] update" + "(" + beanName + "DAO DAO, List<" + beanName + "> "
				+ beanNameLower + "s, String TABLENAME2) {\r\n");
		sb.append("        if(immediately){\r\n");
		sb.append("            return DAO.updateByKey(" + beanNameLower
				+ "s, TABLENAME2);\r\n");
		sb.append("        }else{\r\n");
		sb.append("            int[] ret = new int[" + beanNameLower
				+ "s.size()];\r\n");
		sb.append("            int i=0;\r\n");
		sb.append("            for(" + beanName + " " + beanNameLower + " : "
				+ beanNameLower + "s){\r\n");
		sb.append("                " + beanNameLower + " = update(DAO, "
				+ beanNameLower + ", TABLENAME2);\r\n");
		sb.append("                ret[i++] = (" + beanNameLower
				+ " != null) ? 1 : 0;\r\n");
		sb.append("            }\r\n");
		sb.append("            return ret;\r\n");
		sb.append("        }\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		///////////////////////////////////////////////////////////////////////
		// update2
		sb.append("    public static " + beanName + " update2" + "("
				+ beanName + " " + beanNameLower + ") {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return update2(DAO, " + beanNameLower + ", DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// update2
		sb.append("    public static " + beanName + " update2" + "(" + beanName + "DAO DAO, "
				+ beanName + " " + beanNameLower
				+ ") {\r\n");
		sb.append("        return update2(DAO, " + beanNameLower + ", DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// update2
		sb.append("    public static " + beanName + " update2" + "("
				+ beanName + " " + beanNameLower + ", String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return update2(DAO, " + beanNameLower + ", TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// update2
		sb.append("    public static " + beanName + " update2" + "(final " + beanName + "DAO DAO, final "
				+ beanName + " " + beanNameLower
				+ ",final String TABLENAME2) {\r\n");
		sb.append("        SqlEx.execute4Fixed(new Runnable() {\r\n");
		sb.append("            public void run() {\r\n");
		sb.append("                try {\r\n");
		sb.append("                    DAO.updateByKey(" + beanNameLower
				+ ", TABLENAME2);\r\n");
		sb.append("                } catch (Exception e) {\r\n");
		sb.append("                    e.printStackTrace();\r\n");
		sb.append("                }\r\n");
		sb.append("            }\r\n");
		sb.append("        });\r\n");
		sb.append("        if(!immediately)\r\n");
		sb.append("            put(" + beanNameLower + ");\r\n");
		sb.append("        return " + beanNameLower + ";\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		///////////////////////////////////////////////////////////////////////
		
		// update2
		sb.append("    public static int[] update2" + "(List<" + beanName
				+ "> " + beanNameLower + "s) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return update2(DAO, " + beanNameLower
				+ "s, DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// update2
		sb.append("    public static int[] update2" + "(" + beanName + "DAO DAO, List<" + beanName
				+ "> " + beanNameLower + "s) {\r\n");
		sb.append("        return update2(DAO, " + beanNameLower + "s, DAO.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// update2
		sb.append("    public static int[] update2" + "(List<" + beanName
				+ "> " + beanNameLower + "s, String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "DAO DAO = DAO();\r\n");
		sb.append("        return update2(DAO, " + beanNameLower + "s, TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// update2
		sb.append("    public static int[] update2" + "(final " + beanName + "DAO DAO, final List<" + beanName
				+ "> " + beanNameLower + "s, final String TABLENAME2) {\r\n");
		sb.append("        int[] ret = new int[" + beanNameLower
				+ "s.size()];\r\n");
		sb.append("        if(immediately){\r\n");
		sb.append("            SqlEx.execute4Fixed(new Runnable() {\r\n");
		sb.append("                public void run() {\r\n");
		sb.append("                    try {\r\n");
		sb.append("                        DAO.updateByKey(" + beanNameLower
				+ "s, TABLENAME2);\r\n");
		sb.append("                    } catch (Exception e) {\r\n");
		sb.append("                        e.printStackTrace();\r\n");
		sb.append("                    }\r\n");
		sb.append("                }\r\n");
		sb.append("            });\r\n");
		sb.append("            for(int i=0;i<ret.length;i++)\r\n");
		sb.append("                ret[i] = 1;\r\n");
		sb.append("        }else{\r\n");
		sb.append("            int i=0;\r\n");
		sb.append("            for(" + beanName + " " + beanNameLower + " : "
				+ beanNameLower + "s){\r\n");
		sb.append("                " + beanNameLower + " = update2(DAO, "
				+ beanNameLower + ", TABLENAME2);\r\n");
		sb.append("                ret[i++] = (" + beanNameLower
				+ " != null) ? 1 : 0;\r\n");
		sb.append("            }\r\n");
		sb.append("        }\r\n");
		sb.append("        return ret;\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		///////////////////////////////////////////////////////////////////////

		sb.append("    public static boolean isTimeout() {\r\n");
		sb.append("       if(TIMEOUT <= 0) return false;\r\n");
		sb.append("       long l2 = System.currentTimeMillis();\r\n");
		sb.append("       long t = l2 - LASTTIME;\r\n");
		sb.append("       return (t > TIMEOUT);\r\n");
		sb.append("    }\r\n\r\n");

		sb.append("    public static long difference(int l1, int l2) {\r\n");
		sb.append("        return l2 - l1;\r\n");
		sb.append("    }\r\n\r\n");

		sb.append("    public static long now() {\r\n");
		sb.append("        return System.currentTimeMillis();\r\n");
		sb.append("    }\r\n\r\n");

		sb.append("    public static Date time() {\r\n");
		sb.append("        return new java.util.Date();\r\n");
		sb.append("    }\r\n\r\n");

		sb.append("    public static Map newMap() {\r\n");
		sb.append("        return new HashMap();\r\n");
		sb.append("    }\r\n\r\n");

		sb.append("    public static List newList() {\r\n");
		sb.append("        return new Vector();\r\n");
		sb.append("    }\r\n");
		return sb.toString();
	}

	// ��ix �Ƿ���Ψһ����
	public static boolean isOnly(List<MyIndex> indexs, MyIndex i) {
		String ix_mz = i.mz;
		// String ix_f = i.field;

		int num = 0;
		for (MyIndex mi : indexs) {
			if (mi.mz.equals(ix_mz)) {
				num++;
			}
		}
		return num <= 1;
	}
}
