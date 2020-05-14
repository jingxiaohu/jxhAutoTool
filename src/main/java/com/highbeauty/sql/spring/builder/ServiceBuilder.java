package com.highbeauty.sql.spring.builder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import com.highbeauty.pinyin.PinYin;

public class ServiceBuilder {
	public static void main(String[] args) throws Exception {

		String sql = "SELECT * FROM `` LIMIT 1";
		String host = "192.168.2.241";
		String db = "fych";
		Connection conn = SqlEx.newMysqlConnection(host, db);
		
		ResultSet rs = SqlEx.executeQuery(conn, sql);

		boolean immediately = true;
		String appcontext = "";
		String pkg = "test.dao.";

		ServiceBuilder builder = new ServiceBuilder();
		String xml = builder.build(conn, rs, pkg + "service", pkg + "bean",
				pkg + "dao", pkg + "entity", appcontext, immediately);
		System.out.println(xml);
	}

	public static void InternalBuild(Connection conn, String tablename,
			String pkg, boolean src, String appcontext, boolean immediately)
			throws Exception {

		String sql = String.format("SELECT * FROM `%s` LIMIT 1", tablename);

		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		ServiceBuilder builder = new ServiceBuilder();
		String xml = builder.build(conn, rs, pkg + "service", pkg + "bean",
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
		StringBuffer sb = new StringBuffer();
		if (pkg != null && pkg.length() > 0) {
			sb.append("package " + pkg + ";");
			sb.append("\r\n");
			sb.append("\r\n");
		}
		sb.append("import java.util.*;\r\n");
		sb.append("import org.springframework.stereotype.Service;");
		sb.append("import org.springframework.beans.factory.annotation.Autowired;");
		sb.append("import ").append(beanPkg).append(".*;\r\n");
		sb.append("import ").append(daoPkg).append(".*;\r\n");
//		sb.append("import ").append(entityPkg).append(".*;\r\n");

		if (appcontext != null && !appcontext.isEmpty()) {
			sb.append("import ").append(appcontext).append(";\r\n");
		}
		sb.append("\r\n");
		sb.append("import "+pkg.replace("service","")+"util.SqlEx;");
		sb.append("\r\n");
		
		String shortName = PinYin.getShortPinYin(tableName);
		String UShortName = StrEx.upperFirst(shortName);
		// class
		sb.append("//" + tableName + "\r\n");
		sb.append("@SuppressWarnings({\"unchecked\", \"static-access\"})");
		sb.append("\r\n");
		sb.append("@Service(\""+shortName + "Service\")");
		sb.append("\r\n");
		sb.append("public class " + UShortName + "Service{\r\n");
		sb.append("\r\n");
		
		sb.append("@Autowired");
		sb.append("\r\n");
		sb.append("private  "+UShortName+"Dao "+shortName+"Dao;");
		
		sb.append("    // true是否立即执行 \r\n");
		sb.append("    public  boolean immediately = " + immediately
				+ ";\r\n");
		sb.append("\r\n");
		sb.append("    // 最后执行时间\r\n");
		sb.append("    public  long LASTTIME = 0;\r\n");
//		sb.append("    public  long TIMEOUT = " + UShortName
//				+ "Entity.TIMEOUT();\r\n");
		sb.append("\r\n");
		// construct
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

		// index
		sb.append(generateIndex(rsmd, tableName, indexs));

		// Update
		sb.append(generateUpdate(rsmd, tableName));

		sb.append("}\r\n");

		return sb.toString();
	}

	public  String generateDef(ResultSetMetaData rsmd, String tableName,
			List<MyIndex> indexs) throws SQLException {
		String beanName = StrEx.upperFirst(PinYin.getShortPinYin(tableName));
		String beanNameLower = beanName.toLowerCase();
		String key = AutoIncrement.getAutoIncrement(rsmd);
		if (key == null)
			key = "";
		String keyType = JavaType.getType(rsmd, key,true);
		StringBuffer sb = new StringBuffer();
		sb.append("\r\n");
		sb.append("    public  final Map<" + keyType + ", ")
				.append(beanName).append("> vars = newMap();");
		sb.append("\r\n");
		sb.append("\r\n");

		for (MyIndex index : indexs) {
			if (!isOnly(indexs, index))
				continue;

			// String ix_mz = index.mz;
			String ix_field = index.field;

			String mz = StrEx.upperFirst(PinYin.getShortPinYin(ix_field));

			String jtype = index.getType(rsmd);

			if (jtype.equals("java.util.Date"))
				continue;

			if (index.wy) {
				String s = "    public  final Map<" + jtype + ", "+ keyType + "> varsBy" + mz + " = newMap();";
				sb.append(s);
			} else {
				String s = "    public  final Map<" + jtype + ", Map<"+ keyType + ", " + keyType + ">> varsBy" + mz+ " = newMap();";
				sb.append(s);
			}
			sb.append("\r\n");
			sb.append("\r\n");
		}

		sb.append("    private  void put(").append(beanName).append(" " + beanNameLower + "){\r\n");
		sb.append("        " + keyType + " " + key + " = " + beanNameLower + ".").append(key).append(";\r\n");
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
			sb.append("        " + jtype + " " + mz + " = " + beanNameLower+ ".get" + mz2 + "();\r\n");

			if (index.wy) {
				String s = "        varsBy" + mz2 + ".put(" + mz + ", " + key+ ");\r\n";
				sb.append(s);
			} else {
				String m = "m" + m1;
				String vbm = "varsBy" + mz2;
				String s = "        Map " + m + " = " + vbm + ".get(" + mz+ ");\r\n";
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
		sb.append("    public  void clear(){\r\n");
		sb.append("        vars.clear();\r\n");
//		for (MyIndex index : indexs) {
////			if (index.wy) {
//			String jtype = index.getType(rsmd);
//
//			if (jtype.equals("java.util.Date"))
//				continue;
//			String mz = StrEx.upperFirst(PinYin.getShortPinYin(index.field));
//			String s = "        varsBy" + mz + ".clear();\r\n";
//			sb.append(s);
////			}
//		}
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  int count(){\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return count(Dao, Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  int count(String TABLENAME2){\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return count(Dao, TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  int count(" + beanName + "Dao Dao){\r\n");
		sb.append("        return count(Dao, Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  int count(" + beanName + "Dao Dao, String TABLENAME2){\r\n");
		//sb.append("        if(immediately){\r\n");
		sb.append("            return Dao.count(TABLENAME2);\r\n");
		//sb.append("        }else{\r\n");
		//sb.append("            if(isTimeout()){ reloadAll(Dao, TABLENAME2); }\r\n");
		//sb.append("            return vars.size();\r\n");
		//sb.append("        }\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  void relocate(String TABLENAME2) {\r\n");
		sb.append("        Dao().TABLENAME = TABLENAME2;\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  void relocate(" + beanName + "Dao Dao, String TABLENAME2) {\r\n");
		sb.append("        Dao.TABLENAME = TABLENAME2;\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  String createTableMm() {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return createTableMm(Dao);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  String createTableMm(" + beanName + "Dao Dao) {\r\n");
		sb.append("        String TABLENAME2 = Dao.TABLEMM();\r\n");
		sb.append("        createTable(Dao, TABLENAME2);\r\n");
		sb.append("        return TABLENAME2;\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  String createTableDd() {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return createTableDd(Dao);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  String createTableDd(" + beanName + "Dao Dao) {\r\n");
		sb.append("        String TABLENAME2 = Dao.TABLEDD();\r\n");
		sb.append("        createTable(Dao, TABLENAME2);\r\n");
		sb.append("        return TABLENAME2;\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  void createTable(String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        Dao.createTable(TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  void createTable(" + beanName + "Dao Dao) {\r\n");
		sb.append("        Dao.createTable(Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  void createTable(" + beanName + "Dao Dao, String TABLENAME2) {\r\n");
		sb.append("        Dao.createTable(TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  void reloadAll(String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        relocate(Dao, TABLENAME2);\r\n");
		sb.append("        loadAll(Dao);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  void reloadAll(" + beanName + "Dao Dao) {\r\n");
		sb.append("        relocate(Dao, Dao.TABLENAME);\r\n");
		sb.append("        loadAll(Dao);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");


		sb.append("    public  void reloadAll(" + beanName + "Dao Dao, String TABLENAME2) {\r\n");
		sb.append("        relocate(Dao, TABLENAME2);\r\n");
		sb.append("        loadAll(Dao);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  void loadAll() {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        loadAll(Dao);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");
		
		sb.append("    public  void loadAll(" + beanName + "Dao Dao) {\r\n");
		sb.append("        if(immediately)\r\n");
		sb.append("            return;\r\n");
		sb.append("        clear();\r\n");
		sb.append("        List<" + beanName + "> " + beanNameLower + "s = Dao.selectAll();\r\n");
		sb.append("        for (" + beanName + " " + beanNameLower + " : "
				+ beanNameLower + "s) {\r\n");
		sb.append("            put(" + beanNameLower + ");\r\n");
		sb.append("        }\r\n");
		sb.append("        LASTTIME = System.currentTimeMillis();\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  Map toMap(" + beanName + " " + beanNameLower + "){\r\n");
		sb.append("        Map ret = " + beanNameLower + ".toMap();\r\n");
		sb.append("        return ret;\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("\r\n");
		sb.append("    public  List<Map> toMap(List<" + beanName + "> " + beanNameLower + "s){\r\n");
		sb.append("        List<Map> ret = new Vector<Map>();\r\n");
		sb.append("        for (" + beanName + " " + beanNameLower + " : " + beanNameLower + "s){\r\n");
		sb.append("            Map e = toMap(" + beanNameLower + ");\r\n");
		sb.append("            ret.add(e);\r\n");
		sb.append("        }\r\n");
		sb.append("        return ret;\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		for (MyIndex index : indexs) { 
			if (!isOnly(indexs, index))
				continue;
			String ix_field = index.field;
			String mz = StrEx.upperFirst(PinYin.getShortPinYin(ix_field));
			String jtype = index.getType(rsmd);
			if (!jtype.equals("Integer"))
				continue;

			sb.append("    public  List<" + beanName + "> sort"+mz+"(List<"
					+ beanName + "> " + beanNameLower + "s){\r\n");
			sb.append("        Collections.sort(" + beanNameLower
					+ "s, new Comparator<" + beanName + ">(){\r\n");
			sb.append("            public int compare(" + beanName + " o1, "
					+ beanName + " o2) {\r\n");
			sb.append("                int i1 = o1." + key + ";\r\n");
			sb.append("                int i2 = o2." + key + ";\r\n");
			sb.append("                return i1 - i2;\r\n");
			sb.append("            }\r\n");
			sb.append("        });\r\n");
			sb.append("        return " + beanNameLower + "s;\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");

			sb.append("    public  List<" + beanName + "> sort"+mz+"Ro(List<"
					+ beanName + "> " + beanNameLower + "s){\r\n");
			sb.append("        Collections.sort(" + beanNameLower
					+ "s, new Comparator<" + beanName + ">(){\r\n");
			sb.append("            public int compare(" + beanName + " o1, "
					+ beanName + " o2) {\r\n");
			sb.append("                int i1 = o1." + key + ";\r\n");
			sb.append("                int i2 = o2." + key + ";\r\n");
			sb.append("                return i2 - i1;\r\n");
			sb.append("            }\r\n");
			sb.append("        });\r\n");
			sb.append("        return " + beanNameLower + "s;\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");
		}
		
		for (MyIndex index : indexs) { // 
			if (!isOnly(indexs, index))
				continue;
			String ix_field = index.field;
			String mz = StrEx.upperFirst(PinYin.getShortPinYin(ix_field));
			String jtype = index.getType(rsmd);
			if (!jtype.equals("java.util.Date"))
				continue;

			sb.append("    public  List<" + beanName + "> sort"+mz+"(List<"
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

			sb.append("    public  List<" + beanName + "> sort"+mz+"2(List<"
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
		
		sb.append("    public  List<" + beanName + "> sort(List<"
				+ beanName + "> " + beanNameLower + "s){\r\n");
		sb.append("        Collections.sort(" + beanNameLower
				+ "s, new Comparator<" + beanName + ">(){\r\n");
		sb.append("            public int compare(" + beanName + " o1, "
				+ beanName + " o2) {\r\n");
		sb.append("                int i1 = (int)o1." + key + ";\r\n");
		sb.append("                int i2 = (int)o2." + key + ";\r\n");
		sb.append("                return i1 - i2;\r\n");
		sb.append("            }\r\n");
		sb.append("        });\r\n");
		sb.append("        return " + beanNameLower + "s;\r\n");
		sb.append("    }\r\n");

		sb.append("    public  List<" + beanName + "> sortReverse(List<"
				+ beanName + "> " + beanNameLower + "s){\r\n");
		sb.append("        Collections.sort(" + beanNameLower
				+ "s, new Comparator<" + beanName + ">(){\r\n");
		sb.append("            public int compare(" + beanName + " o1, "
				+ beanName + " o2) {\r\n");
		sb.append("                int i1 = (int)o1." + key + ";\r\n");
		sb.append("                int i2 = (int)o2." + key + ";\r\n");
		sb.append("                return i2 - i1;\r\n");
		sb.append("            }\r\n");
		sb.append("        });\r\n");
		sb.append("        return " + beanNameLower + "s;\r\n");
		sb.append("    }\r\n");

		sb.append("\r\n");

		return sb.toString();
	}

	public  String generateDAO(ResultSetMetaData rsmd, String tableName) {
		String shortName = PinYin.getShortPinYin(tableName);
		String beanName = StrEx.upperFirst(shortName);

		StringBuffer sb = new StringBuffer();

		// default
		sb.append("    public  ").append(beanName).append("Dao ").append("Dao");
		sb.append("(){\r\n");
//		sb.append("        return AppContext." + beanName + "Dao(); \r\n");
		sb.append("        return "+shortName+"Dao; \r\n");
		
		//sb.append("//return new " + (beanName)+ "Dao(AppContext.ds()); \r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		return sb.toString();
	}

	public   String generateInsert(ResultSetMetaData rsmd,
			String tableName) throws SQLException {
		StringBuffer sb = new StringBuffer();

		String key = AutoIncrement.getAutoIncrement(rsmd);
		if (key == null)
			key = "";
		String javaType = JavaType.getType(rsmd, key);
		String beanName = StrEx.upperFirst(PinYin.getShortPinYin(tableName));
		String beanNameLower = beanName.toLowerCase();
		// insert
		sb.append("    public  " + beanName + " insert(" + beanName + " "
				+ beanNameLower + ") {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return insert(Dao, " + beanNameLower + ", Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// insert
		sb.append("    public  " + beanName + " insert(" + beanName + "Dao Dao, " + beanName + " "
				+ beanNameLower + ") {\r\n");
		sb.append("        return insert(Dao, " + beanNameLower + ", Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// insert
		sb.append("    public  " + beanName + " insert(" + beanName + " "
				+ beanNameLower + ", String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return insert(Dao, " + beanNameLower + ", TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// insert
		sb.append("    public  " + beanName + " insert(" + beanName + "Dao Dao, " + beanName
				+ " " + beanNameLower + ", String TABLENAME2) {\r\n");
		sb.append("        int n = Dao.insert(" + beanNameLower + ", TABLENAME2);\r\n");
		sb.append("        if(n <= 0)\r\n");
		sb.append("            return null;\r\n");
		sb.append("\r\n");
//		sb.append("        " + beanNameLower + "." + key + " = new " + javaType + "(n);\r\n");
		sb.append("        " + beanNameLower + "." + key + " = n;\r\n");
		sb.append("\r\n");
		sb.append("        if(!immediately)\r\n");
		sb.append("            put(" + beanNameLower + ");\r\n");
		sb.append("\r\n");
		sb.append("        return " + beanNameLower + ";\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");
		///////////////////////////////////////////////////////////////////////
		// insert2
		sb.append("    public  " + beanName + " insert2(" + beanName + " " + beanNameLower + ") {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return insert2(Dao, " + beanNameLower + ", Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// insert2
		sb.append("    public  " + beanName + " insert2(" + beanName + "Dao Dao, " + beanName
				+ " " + beanNameLower + ") {\r\n");
		sb.append("        return insert2(Dao, " + beanNameLower + ", Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// insert2
		sb.append("    public  " + beanName + " insert2(" + beanName
				+ " " + beanNameLower + ", String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return insert2(Dao, " + beanNameLower + ", TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// insert2
		sb.append("    public  " + beanName + " insert2(" + beanName + "Dao Dao, " + beanName
				+ " " + beanNameLower + ", String TABLENAME2) {\r\n");
		sb.append("        int n = Dao.insert2(" + beanNameLower + ", TABLENAME2);\r\n");
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
	public  String generateBatchInsert(ResultSetMetaData rsmd,
			String tableName) throws SQLException {
		StringBuffer sb = new StringBuffer();

		String key = AutoIncrement.getAutoIncrement(rsmd);
		if (key == null)
			key = "";
		// String javaType = JavaType.getType(rsmd, key);
		String beanName = StrEx.upperFirst(PinYin.getShortPinYin(tableName));
		String beanNameLower = beanName.toLowerCase();
		// insert
		sb.append("    public  int[] insert(List<" + beanName + "> " + beanNameLower + "s) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return insert(Dao, " + beanNameLower + "s, Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// insert
		sb.append("    public  int[] insert(" + beanName + "Dao Dao, List<" + beanName + "> "
				+ beanNameLower + "s) {\r\n");
		sb.append("        return insert(Dao, " + beanNameLower + "s, Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// insert
		sb.append("    public  int[] insert(List<" + beanName + "> "
				+ beanNameLower + "s, String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return insert(Dao, " + beanNameLower + "s, TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// insert
		sb.append("    public  int[] insert(" + beanName + "Dao Dao, List<" + beanName + "> "
				+ beanNameLower + "s, String TABLENAME2) {\r\n");
		sb.append("        if(immediately){\r\n");
		sb.append("            return Dao.insert(" + beanNameLower + "s, TABLENAME2);\r\n");
		sb.append("        }else{\r\n");
		sb.append("            int[] ret = new int[" + beanNameLower + "s.size()];\r\n");
		sb.append("            int n=0;\r\n");
		sb.append("            for(" + beanName + " " + beanNameLower + " : " + beanNameLower + "s){\r\n");
		sb.append("                " + beanNameLower + " = insert(Dao, " + beanNameLower + ", TABLENAME2);\r\n");
		sb.append("                ret[n++] = (" + beanNameLower + "!=null) ? 1 : 0;\r\n");
		sb.append("            }\r\n");
		sb.append("            return ret;\r\n");
		sb.append("        }\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		return sb.toString();
	}
	///////////////////////////////////////////////////////////////////////

	public  String generateDelete(ResultSetMetaData rsmd,
			String tableName) throws SQLException {
		StringBuffer sb = new StringBuffer();
		String key = AutoIncrement.getAutoIncrement(rsmd);
		if (key == null)
			return "";
		 String beanName = StrEx.upperFirst(PinYin.getShortPinYin(tableName));

		// delete
		String javaType = JavaType.getType(rsmd, key);
		sb.append("    public  int delete(" + javaType + " " + key + ") {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return delete(Dao, " + key + ", Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  int delete(" + beanName + "Dao Dao, " + javaType + " " + key
				+ ") {\r\n");
		sb.append("        return delete(Dao, " + key + ", Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  int delete(" + javaType + " " + key
				+ ", String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return delete(Dao, " + key + ", TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  int delete(" + beanName + "Dao Dao, " + javaType + " " + key
				+ ", String TABLENAME2) {\r\n");
		sb.append("        int n = Dao.deleteByKey(" + key + ", TABLENAME2);\r\n");
		sb.append("        if(n <= 0)\r\n");
		sb.append("            return 0;\r\n");
		sb.append("        if(!immediately)\r\n");
		sb.append("            vars.remove(" + key + ");\r\n");
		sb.append("        return n;\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		///////////////////////////////////////////////////////////////////////
		// sb.append("    public  int[] delete(" + javaType + "[] " + key
		// + "s) {\r\n");
		// sb.append("        return delete("+key+"s, Dao().TABLENAME);\r\n");
		// sb.append("    }\r\n");
		// sb.append("\r\n");
		sb.append("    public  int[] delete(" + javaType + "[] " + key
				+ "s) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return delete(Dao, " + key + "s, Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  int[] delete(" + beanName + "Dao Dao, " + javaType + "[] " + key
				+ "s) {\r\n");
		sb.append("        return delete(Dao, " + key + "s, Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  int[] delete(" + javaType + "[] " + key
				+ "s,String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return delete(Dao, " + key + "s, TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  int[] delete(" + beanName + "Dao Dao, " + javaType + "[] " + key
				+ "s,String TABLENAME2) {\r\n");
		sb.append("        if(immediately){\r\n");
		sb.append("            return Dao.deleteByKey(" + key + "s, TABLENAME2);\r\n");
		sb.append("        }else{\r\n");
		sb.append("            int[] ret = new int[" + key + "s.length];\r\n");
		sb.append("            int n=0;\r\n");
		sb.append("            for(" + javaType + " " + key + " : " + key + "s){\r\n");
		sb.append("                ret[n++] = delete(Dao, " + key + ", TABLENAME2);\r\n");
		sb.append("            }\r\n");
		sb.append("            return ret;\r\n");
		sb.append("        }\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		///////////////////////////////////////////////////////////////////////
		
		sb.append("    public  int delete2(" + javaType + " " + key + ") {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return delete2(Dao, " + key + ", Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  int delete2(" + beanName + "Dao Dao, " + javaType + " " + key
				+ ") {\r\n");
		sb.append("        return delete2(Dao, " + key + ", Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  int delete2(" + javaType + " " + key
				+ ", String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return delete2(Dao, " + key + ", TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  int delete2(final " + beanName + "Dao Dao, final " + javaType + " " + key
				+ ",final String TABLENAME2) {\r\n");
		sb.append("        SqlEx.execute4Fixed(new Runnable() {\r\n");
		sb.append("            public void run() {\r\n");
		sb.append("                try {\r\n");
		sb.append("                    Dao.deleteByKey(" + key + ", TABLENAME2);\r\n");
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

		sb.append("    public  int[] delete2(" + javaType + "[] "
				+ key + "s) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return delete2(Dao, " + key + "s, Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  int[] delete2(" + beanName + "Dao Dao, " + javaType + "[] "
				+ key + "s) {\r\n");
		sb.append("        return delete2(Dao, " + key + "s, Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  int[] delete2(" + javaType + "[] "
				+ key + "s, String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return delete2(Dao, " + key + "s, TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  int[] delete2(final " + beanName + "Dao Dao, final " + javaType + "[] "
				+ key + "s,final String TABLENAME2) {\r\n");
		sb.append("        if(immediately){\r\n");
		sb.append("            int[] ret = new int[" + key + "s.length];\r\n");
		sb.append("            SqlEx.execute4Fixed(new Runnable() {\r\n");
		sb.append("                public void run() {\r\n");
		sb.append("                    try {\r\n");
		sb.append("                        Dao.deleteByKey(" + key
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
		sb.append("                ret[i] = delete2(Dao, " + key
				+ ", TABLENAME2);\r\n");
		sb.append("            }\r\n");
		sb.append("            return ret;\r\n");
		sb.append("        }\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		return sb.toString();
	}
	///////////////////////////////////////////////////////////////////////

	public  String generateSelectAll(ResultSetMetaData rsmd, String tableName)
			throws SQLException {
		StringBuffer sb = new StringBuffer();
		String key = AutoIncrement.getAutoIncrement(rsmd);
		if (key == null)
			key = "";
		String shortName = PinYin.getShortPinYin(tableName);
		String beanName = StrEx.upperFirst(shortName);

		sb.append("    public  List<" + beanName + "> getAll() {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return getAll(Dao, Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  List<" + beanName + "> getAll(" + beanName + "Dao Dao) {\r\n");
		sb.append("        return getAll(Dao, Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  List<" + beanName + "> getAll(String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return getAll(Dao, TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  List<" + beanName + "> getAll(" + beanName + "Dao Dao, String TABLENAME2) {\r\n");
		sb.append("        List<" + beanName + "> ret = new Vector<" + beanName
				+ ">();\r\n");
		//sb.append("        if(immediately){\r\n");
		sb.append("            ret = Dao.selectAll(TABLENAME2);\r\n");
		//sb.append("        }else{\r\n");
		//sb.append("            if(isTimeout()){ reloadAll(Dao, TABLENAME2); }\r\n");
		//sb.append("            ret.addAll(vars.values());\r\n");
		//sb.append("        }\r\n");
		sb.append("        return sort(ret);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");
		///////////////////////////////////////////////////////////////////////

		sb.append("    public  List<" + beanName + "> getLast(int num) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return getLast(Dao, num, Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  List<" + beanName + "> getLast(" + beanName + "Dao Dao, int num) {\r\n");
		sb.append("        return getLast(Dao, num, Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  List<" + beanName + "> getLast(int num, String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return getLast(Dao, num, TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  List<" + beanName + "> getLast(" + beanName + "Dao Dao, int num, String TABLENAME2) {\r\n");
		sb.append("        List<" + beanName + "> ret = new Vector<" + beanName + ">();\r\n");
		//sb.append("        if(immediately){\r\n");
		sb.append("            ret = Dao.selectLast(num, TABLENAME2);\r\n");
		//sb.append("        }else{\r\n");
		//sb.append("            if(isTimeout()){ reloadAll(Dao, TABLENAME2); }\r\n");
		//sb.append("            ret.addAll(vars.values());\r\n");
		//sb.append("            ret = sortReverse(ret);\r\n");
		//sb.append("        }\r\n");
		//sb.append("        if(ret.size() > num){\r\n");
		//sb.append("            ret = ret.subList(0, num);\r\n");
		//sb.append("        }\r\n");
		sb.append("        return ret;\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		return sb.toString();
	}
	///////////////////////////////////////////////////////////////////////

	public  String generateSelectByPage(ResultSetMetaData rsmd, String tableName)
			throws SQLException {
		StringBuffer sb = new StringBuffer();
		String key = AutoIncrement.getAutoIncrement(rsmd);
		if (key == null)
			key = "";
		String shortName = PinYin.getShortPinYin(tableName);
		String beanName = StrEx.upperFirst(shortName);
		sb.append("    public  List<" + beanName + "> getByPage(int page, int size) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return getByPage(Dao, page, size, Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  List<" + beanName
				+ "> getByPage(" + beanName + "Dao Dao, int page, int size) {\r\n");
		sb.append("        return getByPage(Dao, page, size, Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  List<" + beanName
				+ "> getByPage(int page, int size, String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return getByPage(Dao, page, size, TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  List<" + beanName
				+ "> getByPage(" + beanName + "Dao Dao, int page, int size, String TABLENAME2) {\r\n");
		//sb.append("        if(immediately){\r\n");
		sb.append("            int begin = page * size;\r\n");
		sb.append("            int num = size;\r\n");
		sb.append("            return Dao.selectByPage(begin, num, TABLENAME2);\r\n");
//		sb.append("        }else{\r\n");
//		sb.append("            if(isTimeout()){ reloadAll(Dao, TABLENAME2); }\r\n");
//		sb.append("            List<" + beanName + "> v = getAll(Dao, TABLENAME2);\r\n");
//		sb.append("            return SqlEx.selectByPage(v, page, size);\r\n");
//		sb.append("        }\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");
		
		return sb.toString();
	}
	///////////////////////////////////////////////////////////////////////

	public  String generatePageCount(ResultSetMetaData rsmd, String tableName)
			throws SQLException {
		StringBuffer sb = new StringBuffer();
		String key = AutoIncrement.getAutoIncrement(rsmd);
		if (key == null)
			key = "";
		 String beanName = StrEx.upperFirst(PinYin.getShortPinYin(tableName));
		sb.append("    public  int pageCount(int size) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return pageCount(Dao, size, Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  int pageCount(" + beanName + "Dao Dao, int size) {\r\n");
		sb.append("        return pageCount(Dao, size, Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");

		sb.append("    public  int pageCount(int size, String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return pageCount(Dao, size, TABLENAME2);\r\n");
		sb.append("    }\r\n");

		sb.append("    public  int pageCount(" + beanName + "Dao Dao, int size, String TABLENAME2) {\r\n");
		sb.append("        int v = 0;\r\n");
		//sb.append("        if(immediately){\r\n");
		sb.append("            v = Dao.count(TABLENAME2);\r\n");
		//sb.append("        }else{\r\n");
		//sb.append("            if(isTimeout()){ reloadAll(Dao, TABLENAME2); }\r\n");
		//sb.append("            v = count(Dao, TABLENAME2);\r\n");
		//sb.append("        }\r\n");
		sb.append("        return SqlEx.pageCount(v, size);\r\n");
		sb.append("    }\r\n");
		
		sb.append("\r\n");
		return sb.toString();
	}
	///////////////////////////////////////////////////////////////////////

	//  String generateIndexCount(ResultSetMetaData rsmd, String
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
	// sb.append("    public  int countBy" + ukey + "(");
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
	// sb.append(", Dao().TABLENAME);\r\n");
	// sb.append("    }\r\n");
	// sb.append("\r\n");
	//
	// sb.append("    public  int countBy" + ukey + "(");
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
	// sb.append("            return Dao().countBy" + ukey + "(");
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

	public  String generateSelect(ResultSetMetaData rsmd, String tableName)
			throws SQLException {
		StringBuffer sb = new StringBuffer();

		String key = AutoIncrement.getAutoIncrement(rsmd);
		if (key == null)
			return "";

		String shortName = PinYin.getShortPinYin(tableName);
		String beanName = StrEx.upperFirst(shortName);

		String keyJavaType = JavaType.getType(rsmd, key);

		// Connection
		sb.append("    public  " + beanName + " getByKey(" + keyJavaType
				+ " " + key + ") {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return getByKey(Dao, " + key + ", Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  " + beanName + " getByKey(" + beanName + "Dao Dao, " + keyJavaType
				+ " " + key + ") {\r\n");
		sb.append("        return getByKey(Dao, " + key + ", Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  " + beanName + " getByKey(" + keyJavaType
				+ " " + key + ", String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return getByKey(Dao, " + key + ", TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  " + beanName + " getByKey(" + beanName + "Dao Dao, " + keyJavaType
				+ " " + key + ", String TABLENAME2) {\r\n");
		//sb.append("        if(immediately)\r\n");
		sb.append("            return Dao.selectByKey(" + key
				+ ", TABLENAME2);\r\n");
		//sb.append("        else{\r\n");
		//sb.append("            if(isTimeout()){ reloadAll(Dao, TABLENAME2); }\r\n");
		//sb.append("            return vars.get(" + key + ");\r\n");
		//sb.append("        }\r\n");
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

		sb.append("    public  List<" + beanName + "> getGtKey(" + jtype
				+ " " + key + ") {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return getGtKey(Dao, " + key + ", Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  List<" + beanName + "> getGtKey(" + beanName + "Dao Dao, " + jtype
				+ " " + key + ") {\r\n");
		sb.append("        return getGtKey(Dao, " + key + ", Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		sb.append("    public  List<" + beanName + "> getGtKey(" + jtype
				+ " " + key + ", String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return getGtKey(Dao, " + key + ", TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");
		
		sb.append("    public  List<" + beanName + "> getGtKey(" + beanName + "Dao Dao, " + jtype
				+ " " + key + ", String TABLENAME2) {\r\n");
		//sb.append("        if(immediately)\r\n");
		sb.append("            return Dao.selectGtKey(" + key
				+ ", TABLENAME2);\r\n");
//		sb.append("        else{\r\n");
//		sb.append("            if(isTimeout()){ reloadAll(Dao, TABLENAME2); }\r\n");
//		sb.append("            List<" + beanName + "> ret = newList();\r\n");
//		sb.append("            List<" + beanName + "> all = getAll(Dao, TABLENAME2);\r\n");
//		sb.append("            for(" + beanName + " e : all){\r\n");
//		sb.append("                " + jtype + " vid = e." + key + ";\r\n");
//		sb.append("                if(vid > " + key + ")\r\n");
//		sb.append("                    ret.add(e);\r\n");
//		sb.append("            }\r\n");
//		sb.append("            return ret;\r\n");
//		sb.append("        }\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		return sb.toString();
	}
	///////////////////////////////////////////////////////////////////////

	public  String generateIndex(ResultSetMetaData rsmd, String tableName,
			List<MyIndex> indexs) throws SQLException {
		StringBuffer sb = new StringBuffer();
		String key = AutoIncrement.getAutoIncrement(rsmd);
		if (key == null)
			return "";
		String shortName = PinYin.getShortPinYin(tableName);
		String beanName = StrEx.upperFirst(shortName);
		for (MyIndex i : indexs) {
			if (!isOnly(indexs, i))
				continue;
			String mz = PinYin.getShortPinYin(i.field);
			String jtype = i.getType(rsmd);
			String mz2 = StrEx.upperFirst(mz);
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
			sb.append("    public  " + beanName + " getBy" + mz2 + "("+ jtype + " " + mz + ") {\r\n");
			sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
			sb.append("    return getBy" + mz2 + "(Dao, " + mz + ", Dao.TABLENAME);\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");
			// getBy
			sb.append("    public  " + beanName + " getBy" + mz2 + "(" + beanName + "Dao Dao, "+ jtype + " " + mz + ") {\r\n");
			sb.append("    return getBy" + mz2 + "(Dao, " + mz + ", Dao.TABLENAME);\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");
			// getBy
			sb.append("    public  " + beanName + " getBy" + mz2 + "("+ jtype + " " + mz + ", String TABLENAME2) {\r\n");
			sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
			sb.append("    return getBy" + mz2 + "(Dao, " + mz + ", TABLENAME2);\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");
			sb.append("    public  " + beanName + " getBy" + mz2 + "(" + beanName + "Dao Dao, "+ jtype + " " + mz + ", String TABLENAME2) {\r\n");
			sb.append("            return Dao.selectBy" + mz2 + "(" + mz+ ", TABLENAME2);\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");
		}
		for (MyIndex i : indexs) {
			if (!isOnly(indexs, i))
				continue;
			String mz = PinYin.getShortPinYin(i.field);
			String jtype = i.getType(rsmd);
			String mz2 = StrEx.upperFirst(mz);
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
			sb.append("    public  int countBy" + mz2 + "(" + jtype+ " " + mz + ") {\r\n");
			sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
			sb.append("        return countBy" + mz2 + "(Dao, " + mz+ ", Dao.TABLENAME);\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");
			// countBy
			sb.append("    public  int countBy" + mz2 + "(" + beanName + "Dao Dao, " + jtype+ " " + mz + ") {\r\n");
			sb.append("        return countBy" + mz2 + "(Dao, " + mz+ ", Dao.TABLENAME);\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");
			// countBy
			sb.append("    public  int countBy" + mz2 + "(" + jtype+ " " + mz + ", String TABLENAME2) {\r\n");
			sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
			sb.append("        return countBy" + mz2 + "(Dao, " + mz+ ", TABLENAME2);\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");
			// countBy
			sb.append("    public  int countBy" + mz2 + "(" + beanName + "Dao Dao, " + jtype+ " " + mz + ", String TABLENAME2) {\r\n");
			sb.append("        if(immediately){\r\n");
			sb.append("            return Dao.countBy" + mz2 + "(" + mz+ ", TABLENAME2);\r\n");
			sb.append("        }else{\r\n");
			sb.append("            List<" + beanName + "> vs = getBy" + mz2+ "(Dao, " + mz + ", TABLENAME2);\r\n");
			sb.append("            return vs.size();\r\n");
			sb.append("        }\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");
			// getBy
			sb.append("    public  List<" + beanName + "> getBy" + mz2+ "(" + jtype + " " + mz + ") {\r\n");
			sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
			sb.append("        return getBy" + mz2 + "(Dao, " + mz + ", Dao.TABLENAME);\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");
			// getBy
			sb.append("    public  List<" + beanName + "> getBy" + mz2+ "(" + beanName + "Dao Dao, " + jtype + " " + mz + ") {\r\n");
			sb.append("        return getBy" + mz2 + "(Dao, " + mz + ", Dao.TABLENAME);\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");			
			// getBy
			sb.append("    public  List<" + beanName + "> getBy" + mz2+ "(" + jtype + " " + mz + ", String TABLENAME2) {\r\n");
			sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
			sb.append("        return getBy" + mz2 + "(Dao, " + mz + ", TABLENAME2);\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");			
			// getBy
			sb.append("    public  List<" + beanName + "> getBy" + mz2+ "(" + beanName + "Dao Dao, " + jtype + " " + mz + ", String TABLENAME2) {\r\n");
			sb.append("            return Dao.selectBy" + mz2 + "(" + mz+ ", TABLENAME2);\r\n");
			sb.append("    }\r\n");
			sb.append("\r\n");
		}
		return sb.toString();
	}
	///////////////////////////////////////////////////////////////////////

	public  String generateUpdate(ResultSetMetaData rsmd,
			String tableName) throws SQLException {

		StringBuffer sb = new StringBuffer();
		String key = AutoIncrement.getAutoIncrement(rsmd);
		String beanName = StrEx.upperFirst(PinYin.getShortPinYin(tableName));
		String beanNameLower = beanName.toLowerCase();
		if (key == null)
			return "";

		// update
		sb.append("    public  " + beanName + " update" + "(" + beanName
				+ " " + beanNameLower + ") {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return update(Dao, " + beanNameLower
				+ ", Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// update
		sb.append("    public  " + beanName + " update" + "(" + beanName + "Dao Dao, " + beanName
				+ " " + beanNameLower + ") {\r\n");
		sb.append("        return update(Dao, " + beanNameLower + ", Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// update
		sb.append("    public  " + beanName + " update" + "(" + beanName
				+ " " + beanNameLower + ", String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return update(Dao, " + beanNameLower + ", TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// update
		sb.append("    public  " + beanName + " update" + "(" + beanName + "Dao Dao, " + beanName
				+ " " + beanNameLower + ", String TABLENAME2) {\r\n");
		sb.append("        int n = Dao.updateByKey(" + beanNameLower
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
		sb.append("    public  int[] update" + "(List<" + beanName + "> "
				+ beanNameLower + "s) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return update(Dao, " + beanNameLower + "s, Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// update
		sb.append("    public  int[] update" + "(" + beanName + "Dao Dao, List<" + beanName + "> "
				+ beanNameLower + "s) {\r\n");
		sb.append("        return update(Dao, " + beanNameLower + "s, Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// update
		sb.append("    public  int[] update" + "(List<" + beanName + "> "
				+ beanNameLower + "s, String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return update(Dao, " + beanNameLower + "s, TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// update
		sb.append("    public  int[] update" + "(" + beanName + "Dao Dao, List<" + beanName + "> "
				+ beanNameLower + "s, String TABLENAME2) {\r\n");
		sb.append("        if(immediately){\r\n");
		sb.append("            return Dao.updateByKey(" + beanNameLower
				+ "s, TABLENAME2);\r\n");
		sb.append("        }else{\r\n");
		sb.append("            int[] ret = new int[" + beanNameLower
				+ "s.size()];\r\n");
		sb.append("            int i=0;\r\n");
		sb.append("            for(" + beanName + " " + beanNameLower + " : "
				+ beanNameLower + "s){\r\n");
		sb.append("                " + beanNameLower + " = update(Dao, "
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
		sb.append("    public  " + beanName + " update2" + "("
				+ beanName + " " + beanNameLower + ") {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return update2(Dao, " + beanNameLower + ", Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// update2
		sb.append("    public  " + beanName + " update2" + "(" + beanName + "Dao Dao, "
				+ beanName + " " + beanNameLower
				+ ") {\r\n");
		sb.append("        return update2(Dao, " + beanNameLower + ", Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// update2
		sb.append("    public  " + beanName + " update2" + "("
				+ beanName + " " + beanNameLower + ", String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return update2(Dao, " + beanNameLower + ", TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// update2
		sb.append("    public  " + beanName + " update2" + "(final " + beanName + "Dao Dao, final "
				+ beanName + " " + beanNameLower
				+ ",final String TABLENAME2) {\r\n");
		sb.append("        SqlEx.execute4Fixed(new Runnable() {\r\n");
		sb.append("            public void run() {\r\n");
		sb.append("                try {\r\n");
		sb.append("                    Dao.updateByKey(" + beanNameLower
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
		sb.append("    public  int[] update2" + "(List<" + beanName
				+ "> " + beanNameLower + "s) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return update2(Dao, " + beanNameLower
				+ "s, Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// update2
		sb.append("    public  int[] update2" + "(" + beanName + "Dao Dao, List<" + beanName
				+ "> " + beanNameLower + "s) {\r\n");
		sb.append("        return update2(Dao, " + beanNameLower + "s, Dao.TABLENAME);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// update2
		sb.append("    public  int[] update2" + "(List<" + beanName
				+ "> " + beanNameLower + "s, String TABLENAME2) {\r\n");
		sb.append("        " + beanName + "Dao Dao = Dao();\r\n");
		sb.append("        return update2(Dao, " + beanNameLower + "s, TABLENAME2);\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		// update2
		sb.append("    public  int[] update2" + "(final " + beanName + "Dao Dao, final List<" + beanName
				+ "> " + beanNameLower + "s, final String TABLENAME2) {\r\n");
		sb.append("        int[] ret = new int[" + beanNameLower
				+ "s.size()];\r\n");
		sb.append("        if(immediately){\r\n");
		sb.append("            SqlEx.execute4Fixed(new Runnable() {\r\n");
		sb.append("                public void run() {\r\n");
		sb.append("                    try {\r\n");
		sb.append("                        Dao.updateByKey(" + beanNameLower
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
		sb.append("                " + beanNameLower + " = update2(Dao, "
				+ beanNameLower + ", TABLENAME2);\r\n");
		sb.append("                ret[i++] = (" + beanNameLower
				+ " != null) ? 1 : 0;\r\n");
		sb.append("            }\r\n");
		sb.append("        }\r\n");
		sb.append("        return ret;\r\n");
		sb.append("    }\r\n");
		sb.append("\r\n");

		///////////////////////////////////////////////////////////////////////

//		sb.append("    public  boolean isTimeout() {\r\n");
//		sb.append("       if(TIMEOUT <= 0) return false;\r\n");
//		sb.append("       long l2 = System.currentTimeMillis();\r\n");
//		sb.append("       long t = l2 - LASTTIME;\r\n");
//		sb.append("       return (t > TIMEOUT);\r\n");
//		sb.append("    }\r\n\r\n");

		sb.append("    public  long difference(int l1, int l2) {\r\n");
		sb.append("        return l2 - l1;\r\n");
		sb.append("    }\r\n\r\n");

		sb.append("    public  long now() {\r\n");
		sb.append("        return System.currentTimeMillis();\r\n");
		sb.append("    }\r\n\r\n");

		sb.append("    public  Date time() {\r\n");
		sb.append("        return new java.util.Date();\r\n");
		sb.append("    }\r\n\r\n");

		sb.append("    public  Map newMap() {\r\n");
		sb.append("        return new HashMap();\r\n");
		sb.append("    }\r\n\r\n");

		sb.append("    public  List newList() {\r\n");
		sb.append("        return new Vector();\r\n");
		sb.append("    }\r\n");
		return sb.toString();
	}

	public  boolean isOnly(List<MyIndex> indexs, MyIndex i) {
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
