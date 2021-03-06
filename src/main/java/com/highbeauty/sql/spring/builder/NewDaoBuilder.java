package com.highbeauty.sql.spring.builder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.highbeauty.pinyin.PinYin;
import com.highbeauty.util.StringBufferUtils;

public class NewDaoBuilder {
 private static List<String> primarykey = null;//主键
// private String autoIndex = null;//自增索引
 
 public static void main(String[] args) throws Exception {
  String sql = "SELECT * FROM `建筑` LIMIT 1";
  String host = "192.168.2.241";
  String db = "fych";
  Connection conn = SqlEx.newMysqlConnection(host, db);
 
  ResultSet rs = SqlEx.executeQuery(conn, sql);
  NewDaoBuilder builder = new NewDaoBuilder();
  String xml = builder.build(conn, rs, "co.test.dao", "co.test.bean",null,false);
  System.out.println(xml);
 }
 public String build(Connection conn, ResultSet rs, String pkg,
   String beanPkg,Map<String,String> map_comment,boolean is_change_DaoName) throws Exception {
  ResultSetMetaData rsmd = rs.getMetaData();
  String tableName = rsmd.getTableName(1);
  Map<String, List<String>> indexs = IndexBuilder.getIndex2(conn, rsmd);
  //定义主键字段
  if(indexs != null && indexs.size()>0){
	  if(indexs.get("PRIMARY") != null && indexs.get("PRIMARY").size() > 0){
		  primarykey = indexs.get("PRIMARY");
	  }
  }
  
  StringBuffer sb = new StringBuffer();
  // import
  // sb.append("import java.io.*;");
  // sb.append("\r\n");
  if (pkg != null && pkg.length() > 0) {
   sb.append("package " + pkg + ";");
   sb.append("\r\n");
   sb.append("\r\n");
  }
//  sb.append("import org.apache.log4j.Logger;");
  sb.append("import org.slf4j.Logger;");
  sb.append("import org.slf4j.LoggerFactory;");
  sb.append("\r\n");
  sb.append("import java.util.*;");
  sb.append("\r\n");
  //sb.append("import java.text.*;");
  sb.append("\r\n");
  sb.append("import java.sql.*;");
  sb.append("\r\n");
  sb.append("import org.springframework.jdbc.core.*;");
  sb.append("\r\n");
  sb.append("import org.springframework.jdbc.core.namedparam.*;");
  sb.append("\r\n");
  sb.append("import org.springframework.jdbc.support.*;");
  sb.append("\r\n");
  sb.append("import ").append(beanPkg).append(".*;");
  //sb.append("import com.bowlong.text.*;");
  sb.append("\r\n");
  sb.append("import org.springframework.stereotype.Repository;");
  sb.append("\r\n");
  sb.append("import com.highbeauty.text.EasyTemplate;");
  sb.append("\r\n");
  sb.append("\r\n");
  // class
  sb.append("//" + tableName + "\r\n");
  //sb.append("@SuppressWarnings({\"unchecked\"})");
  sb.append("\r\n");

  //TODO is_change_DaoName  是否让DaoBean 名称变化  (例如： user_info表 user_infoDao2) true :是  false :否
  if(is_change_DaoName){
   sb.append("@Repository(\""+PinYin.getShortPinYin(tableName)+"Dao2\")");
  }else{
   sb.append("@Repository(\""+PinYin.getShortPinYin(tableName)+"Dao\")");
  }

  sb.append("\r\n");
  sb.append("public class ");
  sb.append(StrEx.upperFirst(PinYin.getShortPinYin(tableName)));
  sb.append("Dao");
  sb.append(" extends BaseDao");
  sb.append("{\r\n");
  sb.append("\r\n");
  //这里加入日志
  String classname = StrEx.upperFirst(PinYin.getShortPinYin(tableName))+"Dao";
  sb.append("    Logger log = LoggerFactory.getLogger("+classname+".class)").append(";");
  sb.append("\r\n");
  System.out.println("-----------------------");
  // default
  sb.append(generateDef(rsmd, tableName));
  // construct
  //sb.append(generateConstruct(rsmd, tableName));


  if(primarykey != null){
   // insert
   sb.append(generateInsert(rsmd, tableName));
   // batch insert
   sb.append(generateBatchInsert(rsmd, tableName));
   // batch insertPrimarykey
   sb.append(generateBatchInsertPrimarykey(rsmd, tableName));

   // selectAll
   sb.append(generateSelectAll(rsmd, tableName));
   // Select
   sb.append(generateSelect(rsmd, tableName));

  }

  // count
  sb.append(generateCount(rsmd, tableName));
  // selectByPage
  sb.append(generateSelectByPage(rsmd, tableName));
 
  if(primarykey != null){
	  // Update
	  sb.append(generateUpdate(rsmd, tableName));
	  // batch update
	  sb.append(generateBatchUpdate(rsmd, tableName));
	  // delete
	  sb.append(generateDelete(rsmd, tableName));
	  // batch delete
	  sb.append(generateBatchDelete(rsmd, tableName));
  }

  // create table
//  sb.append(generateCreateTable(conn, rs, rsmd, tableName));
  sb.append(generateCreateTable(conn, rs, rsmd, tableName,map_comment));
  // truncate
  sb.append(generateTruncate(rsmd, tableName));
  // repair
  sb.append(generateRepair(rsmd, tableName));
  // optimize
  sb.append(generateOptimize(rsmd, tableName));
  // execute
  sb.append(generateExecute(rsmd, tableName));
 
  sb.append("}\r\n");
  return sb.toString();
 }
 static String generateDef(ResultSetMetaData rsmd, String tableName)
   throws SQLException {
  StringBuffer sb = new StringBuffer();
  //sb.append("    static final SimpleDateFormat sdfMm = new SimpleDateFormat(\"yyyyMM\");\r\n");
  sb.append("\r\n");
  //sb.append("    static final SimpleDateFormat sdfDd = new SimpleDateFormat(\"yyyyMMdd\");\r\n");
  sb.append("\r\n");
 
  sb.append("\r\n");
  sb.append("    private  String TABLE = \"`" + tableName + "`\";\r\n");
  sb.append("\r\n");
 
  sb.append("    private  String TABLENAME = \"`" + tableName + "`\";\r\n");
  sb.append("\r\n");
  
  
  sb.append("    public  String getTABLE(){\r\n");
  sb.append("        return  TABLE;\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  
  sb.append("    public  String getTABLENAME(){\r\n");
  sb.append("        return  TABLENAME;\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  
  sb.append("    public  String TABLEMM(){\r\n");
  sb.append("        return ").append("TABLE + ").append("sdfMm.format(new java.util.Date());\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  sb.append("    public  String TABLEDD(){\r\n");
  sb.append("        return ").append("TABLE + ").append("sdfDd.format(new java.util.Date());\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
 
//  sb.append("    public javax.sql.DataSource ds;\r\n");
  sb.append("\r\n");
  String key = AutoIncrement.getAutoIncrement(rsmd);
  if (key == null){
    StringBuffer buffer_prk = new StringBuffer();
    for (String prk : primarykey) {
     buffer_prk.append(prk).append(",");
    }
    key = buffer_prk.deleteCharAt(buffer_prk.length() - 1).toString();
  }
  String fields = getFields(rsmd, key, true,primarykey);
  String fields2 = getFields(rsmd, key, false,primarykey);
  String fieldArrays = getFieldArrayString(rsmd, key);
 
  sb.append("    private  String[] carrays ="+fieldArrays + ";\r\n");
  sb.append("    private  String coulmns =\""+fields + "\";\r\n");
  sb.append("    private  String coulmns2 =\""+fields2+"\";\r\n");
  sb.append("\r\n");
  
  
  sb.append("    public  String[] getCarrays(){\r\n");
  sb.append("        return  carrays;\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  
  sb.append("    public  String getCoulmns(){\r\n");
  sb.append("        return  coulmns;\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  
  
  sb.append("    public  String getCoulmns2(){\r\n");
  sb.append("        return  coulmns2;\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  
  
  return sb.toString();
 }
 static String generateConstruct(ResultSetMetaData rsmd, String tableName) {
  StringBuffer sb = new StringBuffer();
  // default
//	 String shortTableName = PinYin.getShortPinYin(tableName);
//	 String UTableName = StrEx.upperFirst(shortTableName);
//	 sb.append("    //数据库操作DAO\r\n");
////	 sb.append("    public " + UTableName + "DAO" + "(javax.sql.DataSource ds){\r\n");
//	 sb.append("    public " + UTableName + "Dao" + "(){\r\n");
////	 sb.append("        this.ds = ds; \r\n");
////	 sb.append("        _np = new NamedParameterJdbcTemplate(ds); \r\n");
//	 sb.append("        _np = getJdbc(); \r\n");
//	 sb.append("    }\r\n");
//	 sb.append("\r\n");
  return sb.toString();
 }
 static String generateInsert(ResultSetMetaData rsmd, String tableName)
   throws SQLException {
  StringBuffer sb = new StringBuffer();
  String key = AutoIncrement.getAutoIncrement(rsmd);
  if (key == null){
   StringBuffer buffer_prk = new StringBuffer();
   for (String prk : primarykey) {
    buffer_prk.append(prk).append(",");
   }
   key = buffer_prk.deleteCharAt(buffer_prk.length() - 1).toString();
  }
  String beanName = StrEx.upperFirst(PinYin.getShortPinYin(tableName));
  // Connection
  String fields = getFields(rsmd, key, false,primarykey);
  String values = getValues(rsmd, key, false,primarykey);
  sb.append("    //添加数据\r\n");
  sb.append("    public long insert(" + beanName + " bean) throws SQLException{\r\n");
  sb.append("        return insert(bean, TABLENAME);\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  sb.append("    //添加数据\r\n");
  sb.append("    public long insert(" + beanName + " bean, String TABLENAME2) throws SQLException{\r\n");
  sb.append("        String sql;\r\n");
  sb.append("        try{\r\n");
  sb.append("            sql = \"INSERT INTO \"+TABLENAME2+\" (" + fields + ") VALUES (" + values + ")\";\r\n");
  sb.append("            SqlParameterSource ps = new BeanPropertySqlParameterSource(bean);\r\n");
  sb.append("            KeyHolder keyholder = new GeneratedKeyHolder();\r\n");
  sb.append("            _np.update(sql, ps, keyholder);\r\n");
  sb.append("            return keyholder.getKey().longValue();\r\n");
  sb.append("        }catch(Exception e){\r\n");
  sb.append("            //createTable(TABLENAME2);\r\n");
  sb.append("            log.error(\"insert\", e);").append("\r\n");
//	 sb.append("            return 0;\r\n");
  sb.append("            throw new SQLException(\"insert is error\", e);\r\n");
  sb.append("        }\r\n");
  // }else{
  // sb.append("return 1;");
  // }
  sb.append("    }\r\n");
  sb.append("\r\n");
 
  // Connection
  fields = getFields(rsmd, key, true,primarykey);
  values = getValues(rsmd, key, true,primarykey);
  sb.append("    //添加数据\r\n");
  sb.append("    public long insert_primarykey(" + beanName + " bean) throws SQLException{\r\n");
  sb.append("        return insert_primarykey(bean, TABLENAME);\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  sb.append("    //添加数据\r\n");
  sb.append("    public long insert_primarykey(" + beanName + " bean, String TABLENAME2) throws SQLException{\r\n");
  sb.append("        String sql;\r\n");
  sb.append("        try{\r\n");
  sb.append("            sql = \"INSERT INTO \"+TABLENAME2+\" (" + fields + ") VALUES (" + values + ")\";\r\n");
  sb.append("            SqlParameterSource ps = new BeanPropertySqlParameterSource(bean);\r\n");
  //sb.append("            KeyHolder keyholder = new GeneratedKeyHolder();\r\n");
//  sb.append("            _np.update(sql, ps, keyholder);\r\n");
//  sb.append("            return keyholder.getKey().longValue();\r\n");
  sb.append("            return _np.update(sql, ps);\r\n");
  sb.append("        }catch(Exception e){\r\n");
  sb.append("            //createTable(TABLENAME2);\r\n");
  sb.append("            log.error(\"insert_primarykey\", e);").append("\r\n");
//	 sb.append("            return 0;\r\n");
  sb.append("            throw new SQLException(\"insert2 is error\", e);\r\n");
  sb.append("        }\r\n");
  // }else{
  // sb.append("return 1;");
  // }
  sb.append("    }\r\n");
  sb.append("\r\n");
  return sb.toString();
 }


 /**
  * 批量插入 不带主键
  * @param rsmd
  * @param tableName
  * @return
  * @throws SQLException
  */
 static String generateBatchInsert(ResultSetMetaData rsmd, String tableName)
   throws SQLException {
  StringBuffer sb = new StringBuffer();
  String key = AutoIncrement.getAutoIncrement(rsmd);
  if (key == null){
   StringBuffer buffer_prk = new StringBuffer();
   for (String prk : primarykey) {
    buffer_prk.append(prk).append(",");
   }
   key = buffer_prk.deleteCharAt(buffer_prk.length() - 1).toString();
  }
  String beanName = StrEx.upperFirst(PinYin.getShortPinYin(tableName));
  // Connection
  String fields = getFields(rsmd, key, false,primarykey);
  String values = getQValues(rsmd, key, false,primarykey);
  sb.append("    //批量添加数据\r\n");
  sb.append("    public int[] insert(List<" + beanName + "> beans) throws SQLException{\r\n");
  sb.append("        return insert(beans, TABLENAME);\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
 
  sb.append("    //批量添加数据\r\n");
  sb.append("    public int[] insert(final List<" + beanName + "> beans, String TABLENAME2) throws SQLException{\r\n");
  sb.append("        String sql;\r\n");
  sb.append("        try{\r\n");
  sb.append("            sql = \"INSERT INTO \"+TABLENAME2+\" (" + fields +  ") VALUES (" + values + ")\";\r\n");
  sb.append("            return _np.getJdbcOperations().batchUpdate(sql, new BatchPreparedStatementSetter() {\r\n");
  sb.append("                @Override\r\n");
  sb.append("                public int getBatchSize() {\r\n");
  sb.append("                    return beans.size();\r\n");
  sb.append("                }\r\n");
  sb.append("                @Override\r\n");
  sb.append("                public void setValues(PreparedStatement ps, int i) throws SQLException {\r\n");
  sb.append("                    " + beanName + " bean = beans.get(i);\r\n");
  int count = rsmd.getColumnCount();
  for (int i = 1; i <= count; i++) {
   String f = rsmd.getColumnName(i);
   if (f.equals(key))
    continue;
   String s = "bean." + f;
   int columnType = rsmd.getColumnType(i);
   if (columnType == java.sql.Types.TIMESTAMP) {
    s = "new Timestamp(bean." + f + ".getTime())";
   }else if(columnType == java.sql.Types.DATE){
    s = "new java.sql.Date(bean." + f + ".getTime())";
   }
   sb.append("                    ps.").append(BatchOP.setOP(rsmd, i))
     .append("(").append(i - 1).append(", " + s + ");\r\n");
  }
  sb.append("                }\r\n");
  sb.append("            });\r\n");
  sb.append("        }catch(Exception e){\r\n");
  sb.append("            //createTable(TABLENAME2);\r\n");
  sb.append("            log.error(\"int[] insert\", e);").append("\r\n");
//	 sb.append("            return new int[0];\r\n");
  sb.append("            throw new SQLException(\"insert is error\", e);\r\n");
  sb.append("        }\r\n");
  // }else{
  // sb.append("return 1;");
  // }
  sb.append("    }\r\n");
  sb.append("\r\n");
 
  return sb.toString();
 }




 /**
  * 批量插入 带主键
  * @param rsmd
  * @param tableName
  * @return
  * @throws SQLException
  */
 static String generateBatchInsertPrimarykey(ResultSetMetaData rsmd, String tableName) throws SQLException {
  StringBuffer sb = new StringBuffer();
  String key = AutoIncrement.getAutoIncrement(rsmd);
  if (key == null) {
     StringBuffer buffer_prk = new StringBuffer();
     for (String prk : primarykey) {
      buffer_prk.append(prk).append(",");
     }
     key = buffer_prk.deleteCharAt(buffer_prk.length() - 1).toString();
  }
  String beanName = StrEx.upperFirst(PinYin.getShortPinYin(tableName));
  // Connection
  String fields = getFields(rsmd, key, true,primarykey);
  String values = getQValues(rsmd, key, true,primarykey);
  sb.append("    //批量添加数据\r\n");
  sb.append("    public int[] insertPrimarykey(List<" + beanName + "> beans) throws SQLException{\r\n");
  sb.append("        return insert(beans, TABLENAME);\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");

  sb.append("    //批量添加数据\r\n");
  sb.append("    public int[] insertPrimarykey(final List<" + beanName + "> beans, String TABLENAME2) throws SQLException{\r\n");
  sb.append("        String sql;\r\n");
  sb.append("        try{\r\n");
  sb.append("            sql = \"INSERT INTO \"+TABLENAME2+\" (" + fields +  ") VALUES (" + values + ")\";\r\n");
  sb.append("            return _np.getJdbcOperations().batchUpdate(sql, new BatchPreparedStatementSetter() {\r\n");
  sb.append("                @Override\r\n");
  sb.append("                public int getBatchSize() {\r\n");
  sb.append("                    return beans.size();\r\n");
  sb.append("                }\r\n");
  sb.append("                @Override\r\n");
  sb.append("                public void setValues(PreparedStatement ps, int i) throws SQLException {\r\n");
  sb.append("                    " + beanName + " bean = beans.get(i);\r\n");
  int count = rsmd.getColumnCount();
  for (int i = 1; i <= count; i++) {
   String f = rsmd.getColumnName(i);
   String s = "bean." + f;
   int columnType = rsmd.getColumnType(i);
   if (columnType == java.sql.Types.TIMESTAMP) {
    s = "new Timestamp(bean." + f + ".getTime())";
   }else if(columnType == java.sql.Types.DATE){
    s = "new java.sql.Date(bean." + f + ".getTime())";
   }
   sb.append("                    ps.").append(BatchOP.setOP(rsmd, i))
           .append("(").append(i - 1).append(", " + s + ");\r\n");
  }
  sb.append("                }\r\n");
  sb.append("            });\r\n");
  sb.append("        }catch(Exception e){\r\n");
  sb.append("            //createTable(TABLENAME2);\r\n");
  sb.append("            log.error(\"int[] insert\", e);").append("\r\n");
//	 sb.append("            return new int[0];\r\n");
  sb.append("            throw new SQLException(\"insert is error\", e);\r\n");
  sb.append("        }\r\n");
  // }else{
  // sb.append("return 1;");
  // }
  sb.append("    }\r\n");
  sb.append("\r\n");

  return sb.toString();
 }







 static String generateDelete(ResultSetMetaData rsmd, String tableName)
   throws SQLException {
  StringBuffer sb = new StringBuffer();
  String key = AutoIncrement.getAutoIncrement(rsmd);


  if (key == null){
   StringBuffer buffer_prk = new StringBuffer();
   for (String prk : primarykey) {
    buffer_prk.append(prk).append(",");
   }
   key = buffer_prk.deleteCharAt(buffer_prk.length() - 1).toString();
  }
  StringBuffer paramsTypeBuff = new StringBuffer();
  StringBuffer paramsTypeBuff_delete = new StringBuffer();
  StringBuffer paramsTypeBuff_delete2 = new StringBuffer();
  StringBuffer paramsTypeBuff_delete_params = new StringBuffer();
  StringBuffer paramsStrBuff = new StringBuffer();
  StringBuffer whereBuff_big = new StringBuffer();
  StringBuffer whereBuff = new StringBuffer();
  //传递占位符
  StringBuffer whereBuff_param = new StringBuffer();
  for (String prk : primarykey) {
   String keyJavaType = JavaType.getType(rsmd, prk);
   paramsTypeBuff.append(keyJavaType+" "+prk).append(",");
   paramsStrBuff.append(prk).append(" ").append(",");
   whereBuff_big.append(prk+">:"+prk).append(" and ");
   whereBuff.append(prk+"=:"+prk).append(" and ");
   paramsTypeBuff_delete.append(keyJavaType+"[] "+prk+"s ").append(",");
   paramsTypeBuff_delete2.append("final "+keyJavaType+"[] "+prk+"s ").append(",");
   paramsTypeBuff_delete_params.append(prk+"s ").append(",");
   whereBuff_param.append(prk+"=?").append(" and ");
  }



  sb.append("    //删除单条数据\r\n");
  sb.append("    public int deleteByKey(" + StringBufferUtils.replaceStr_Last(paramsTypeBuff,",") + ") throws SQLException{\r\n");
  sb.append("        return deleteByKey(" + StringBufferUtils.replaceStr_Last(paramsStrBuff,",") + ", TABLENAME);\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  sb.append("    //删除单条数据\r\n");
  sb.append("    public int deleteByKey(" + StringBufferUtils.replaceStr_Last(paramsTypeBuff,",") + ", String TABLENAME2) throws SQLException{\r\n");
  sb.append("        String sql;\r\n");
  sb.append("        try{\r\n");
  sb.append("            sql = \"DELETE FROM \"+TABLENAME2+\" WHERE " + StringBufferUtils.replaceStr_Last(whereBuff_param," and ") + "\";\r\n");
  sb.append("            Map<String,Object> param = new HashMap<String,Object>();\r\n");
  for (String prk : primarykey) {
   sb.append("            param.put(\""+prk+"\", "+prk+");\r\n");
  }
  sb.append("            return _np.update(sql, param);\r\n");
  sb.append("        }catch(Exception e){\r\n");
  sb.append("            log.error(\"deleteByKey\", e);").append("\r\n");
//	 sb.append("            return 0;\r\n");
  sb.append("            throw new SQLException(\"deleteByKey is error\", e);\r\n");
  sb.append("        }\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  return sb.toString();
 }
 static String generateBatchDelete(ResultSetMetaData rsmd, String tableName)
   throws SQLException {
  StringBuffer sb = new StringBuffer();
  String key = AutoIncrement.getAutoIncrement(rsmd);


  if (key == null){
   StringBuffer buffer_prk = new StringBuffer();
   for (String prk : primarykey) {
    buffer_prk.append(prk).append(",");
   }
   key = buffer_prk.deleteCharAt(buffer_prk.length() - 1).toString();
  }
  StringBuffer paramsTypeBuff = new StringBuffer();
  StringBuffer paramsTypeBuff_delete = new StringBuffer();
  StringBuffer paramsTypeBuff_delete2 = new StringBuffer();
  StringBuffer paramsTypeBuff_delete_params = new StringBuffer();
  StringBuffer paramsStrBuff = new StringBuffer();
  StringBuffer whereBuff_big = new StringBuffer();
  StringBuffer whereBuff = new StringBuffer();
  //传递占位符
  StringBuffer whereBuff_param = new StringBuffer();
  for (String prk : primarykey) {
   String keyJavaType = JavaType.getType(rsmd, prk);
   paramsTypeBuff.append(keyJavaType+" "+prk).append(",");
   paramsStrBuff.append(prk).append(" ").append(",");
   whereBuff_big.append(prk+">:"+prk).append(" and ");
   whereBuff.append(prk+"=:"+prk).append(" and ");
   paramsTypeBuff_delete.append(keyJavaType+"[] "+prk+"s ").append(",");
   paramsTypeBuff_delete2.append("final "+keyJavaType+"[] "+prk+"s ").append(",");
   paramsTypeBuff_delete_params.append(prk+"s ").append(",");
   whereBuff_param.append(prk+"=?").append(" and ");
  }


  sb.append("    //批量删除数据\r\n");
  sb.append("    public int[] deleteByKey("+StringBufferUtils.replaceStr_Last(paramsTypeBuff_delete,",")+") throws SQLException{\r\n");
  sb.append("        return deleteByKey("+StringBufferUtils.replaceStr_Last(paramsTypeBuff_delete_params,",")+", TABLENAME);\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  sb.append("    //批量删除数据\r\n");
  sb.append("    public int[] deleteByKey("+StringBufferUtils.replaceStr_Last(paramsTypeBuff_delete2,",")+", String TABLENAME2) throws SQLException{\r\n");
  sb.append("        String sql;\r\n");
  sb.append("        try{\r\n");
  sb.append("            sql = \"DELETE FROM \"+TABLENAME2+\" WHERE "+StringBufferUtils.replaceStr_Last(whereBuff_param," and ")+"\";\r\n");
  sb.append("            return _np.getJdbcOperations().batchUpdate(sql, new BatchPreparedStatementSetter() {\r\n");
  sb.append("                @Override\r\n");
  sb.append("                public int getBatchSize() {\r\n");
  sb.append("                    return "+primarykey.get(0)+"s.length;\r\n");
  sb.append("                }\r\n");
  sb.append("                @Override\r\n");
  sb.append("                public void setValues(PreparedStatement ps, int i) throws SQLException {\r\n");

  for (int i = 0; i < primarykey.size(); i++) {
   sb.append("                    ps."+BatchOP.setOP(rsmd, primarykey.get(i))+"("+(i+1)+" , "+primarykey.get(i)+"s[i]);\r\n");
  }
//  sb.append("                    ps."+BatchOP.setOP(rsmd, primarykey)+"(1 , keys[i]);\r\n");


  sb.append("                }\r\n");
  sb.append("            });\r\n");
  sb.append("        }catch(Exception e){\r\n");
  sb.append("            log.error(\"int[] deleteByKey\", e);").append("\r\n");
//	 sb.append("            return new int[0];\r\n");
  sb.append("            throw new SQLException(\"deleteByKey is error\", e);\r\n");
  sb.append("        }\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  return sb.toString();
 }
 static String generateSelectAll(ResultSetMetaData rsmd, String tableName)
   throws SQLException {
  StringBuffer sb = new StringBuffer();
  String key = AutoIncrement.getAutoIncrement(rsmd);
  if (key == null){

   StringBuffer buffer_prk = new StringBuffer();
   for (String prk : primarykey) {
     buffer_prk.append(prk).append(",");
   }
   key = buffer_prk.deleteCharAt(buffer_prk.length() - 1).toString();

  }
  String beanName = StrEx.upperFirst(PinYin.getShortPinYin(tableName));
  String fields = getFields(rsmd, key, true,primarykey);

   StringBuffer paramsTypeBuff = new StringBuffer();
   StringBuffer paramsStrBuff = new StringBuffer();
   StringBuffer whereBuff_big = new StringBuffer();
   StringBuffer whereBuff = new StringBuffer();
   for (String prk : primarykey) {
    String keyJavaType = JavaType.getType(rsmd, key);
    paramsTypeBuff.append(keyJavaType+" "+prk).append(",");
    paramsStrBuff.append(prk).append(" ").append(",");
    whereBuff_big.append(prk+">:"+prk).append(" and ");
    whereBuff.append(prk+"=:"+prk).append(" and ");
   }
   sb.append("    //查询所有数据\r\n");
   sb.append("    public List<" + beanName + "> selectAll() {\r\n");
   sb.append("        return selectAll(TABLENAME);\r\n");
   sb.append("    }\r\n");
   sb.append("\r\n");
   sb.append("    //查询所有数据\r\n");
   sb.append("    public List<" + beanName + "> selectAll(String TABLENAME2) {\r\n");
   sb.append("        String sql;\r\n");
   sb.append("        try{\r\n");
   sb.append("            sql = \"SELECT "+fields+" FROM \"+TABLENAME2+\" ORDER BY "+StringBufferUtils.replaceStr_Last(paramsStrBuff,",")+"\";\r\n");
   sb.append("            return _np.getJdbcOperations().query(sql, new BeanPropertyRowMapper<"+beanName+">(" + beanName + ".class));\r\n");
   sb.append("        }catch(Exception e){\r\n");
   sb.append("            //createTable(TABLENAME2);\r\n");
   sb.append("            log.error(\"selectAll\", e);").append("\r\n");
   sb.append("            return new ArrayList<"+beanName+">();\r\n");
   sb.append("        }\r\n");
   sb.append("    }\r\n");
   sb.append("\r\n");
   sb.append("    //查询最新数据\r\n");
   sb.append("    public List<" + beanName + "> selectLast(int num) {\r\n");
   sb.append("        return selectLast(num, TABLENAME);\r\n");
   sb.append("    }\r\n");
   sb.append("\r\n");
   sb.append("    //查询所有数据\r\n");
   sb.append("    public List<" + beanName + "> selectLast(int num ,String TABLENAME2) {\r\n");
   sb.append("        String sql;\r\n");
   sb.append("        try{\r\n");
   sb.append("            sql = \"SELECT "+fields+" FROM \"+TABLENAME2+\" ORDER BY "+StringBufferUtils.replaceStr_Last(paramsTypeBuff,",")+" DESC LIMIT \"+num+\"\" ;\r\n");
   sb.append("            return _np.getJdbcOperations().query(sql, new BeanPropertyRowMapper<"+beanName+">(" + beanName + ".class));\r\n");
   sb.append("        }catch(Exception e){\r\n");
   sb.append("            //createTable(TABLENAME2);\r\n");
   sb.append("            log.error(\"selectLast\", e);").append("\r\n");
   sb.append("            return new ArrayList<"+beanName+">();\r\n");
   sb.append("        }\r\n");
   sb.append("    }\r\n");
   sb.append("\r\n");

   return sb.toString();





 }
 static String generateSelect(ResultSetMetaData rsmd, String tableName)
   throws SQLException {
  StringBuffer sb = new StringBuffer();
  String key = AutoIncrement.getAutoIncrement(rsmd);
  if (key == null){

   StringBuffer buffer_prk = new StringBuffer();
   for (String prk : primarykey) {
    buffer_prk.append(prk).append(",");
   }
   key = buffer_prk.deleteCharAt(buffer_prk.length() - 1).toString();

  }
  String beanName = StrEx.upperFirst(PinYin.getShortPinYin(tableName));
  String fields = getFields(rsmd, key, true,primarykey);


   StringBuffer paramsTypeBuff = new StringBuffer();
   StringBuffer paramsStrBuff = new StringBuffer();
   StringBuffer whereBuff_big = new StringBuffer();
   StringBuffer whereBuff = new StringBuffer();
   for (String prk : primarykey) {
    String keyJavaType = JavaType.getType(rsmd, prk);
    paramsTypeBuff.append(keyJavaType+" "+prk).append(",");
    paramsStrBuff.append(prk).append(" ").append(",");
    whereBuff_big.append(prk+">:"+prk).append(" and ");
    whereBuff.append(prk+"=:"+prk).append(" and ");
   }

   sb.append("    //根据主键查询\r\n");
   sb.append("    public List<" + beanName + "> selectGtKey("+ StringBufferUtils.replaceStr_Last(paramsTypeBuff,",")+") {\r\n");
   sb.append("        return selectGtKey(" + StringBufferUtils.replaceStr_Last(paramsStrBuff,",") + ", TABLENAME);\r\n");
   sb.append("    }\r\n");
   sb.append("\r\n");
   sb.append("    //根据主键查询\r\n");
   sb.append("    public List<" + beanName + "> selectGtKey("+StringBufferUtils.replaceStr_Last(paramsTypeBuff,",")+", String TABLENAME2) {\r\n");
   sb.append("        String sql;\r\n");
   sb.append("        try{\r\n");
   sb.append("            sql=\"SELECT "+fields+" FROM \"+TABLENAME2+\" WHERE "+StringBufferUtils.replaceStr_Last(whereBuff_big," and ")+"\";\r\n");
   sb.append("            Map<String,Object> param = new HashMap<String,Object>();\r\n");
   for (String prk : primarykey) {
    sb.append("            param.put(\""+prk+"\", "+prk+");\r\n");
   }
   sb.append("            return _np.query(sql, param, new BeanPropertyRowMapper<"+beanName+">("+ beanName + ".class));\r\n");
   sb.append("        }catch(Exception e){\r\n");
   sb.append("            //createTable(TABLENAME2);\r\n");
   sb.append("            log.error(\"selectGtKey\", e);").append("\r\n");
   sb.append("            return new ArrayList<"+beanName+">();\r\n");
   sb.append("        }\r\n");
   sb.append("    }\r\n");
   sb.append("\r\n");
   // Connection
   sb.append("    //根据主键查询\r\n");
   sb.append("    public " + beanName + " selectByKey("+StringBufferUtils.replaceStr_Last(paramsTypeBuff,",")+") {\r\n");
   sb.append("        return selectByKey(" + StringBufferUtils.replaceStr_Last(paramsStrBuff,",") + ", TABLENAME);\r\n");
   sb.append("    }\r\n");
   sb.append("\r\n");
   sb.append("    //根据主键查询\r\n");
   sb.append("    public " + beanName + " selectByKey("+StringBufferUtils.replaceStr_Last(paramsTypeBuff,",")+", String TABLENAME2) {\r\n");
   sb.append("        String sql;\r\n");
   sb.append("        try{\r\n");
   sb.append("            sql=\"SELECT "+fields+" FROM \"+TABLENAME2+\" WHERE "+StringBufferUtils.replaceStr_Last(whereBuff," and ")+"\";\r\n");
   sb.append("            Map<String,Object> param = new HashMap<String,Object>();\r\n");
   for (String prk : primarykey) {
   sb.append("            param.put(\""+prk+"\", "+prk+");\r\n");
   }
   sb.append("            List<" + beanName+ "> list =  _np.query(sql, param, new BeanPropertyRowMapper<"+beanName+">(" + beanName + ".class));\r\n");
   sb.append("            return (list == null || list.size() == 0) ? null : list.get(0);\r\n");
   sb.append("        }catch(Exception e){\r\n");
   sb.append("            //createTable(TABLENAME2);\r\n");
   sb.append("            log.error(\"selectByKey "+StringBufferUtils.replaceStr_Last(paramsTypeBuff,",")+"\",e);").append("\r\n");
   sb.append("            return null;\r\n");
   sb.append("        }\r\n");
   sb.append("    }\r\n");
   sb.append("\r\n");
   return sb.toString();
  //}


 }




 static String generateSelectByIndex(ResultSetMetaData rsmd,
   String tableName, List<String> indexs, MyIndex mi)
   throws SQLException {
  if (indexs.size() < 1)
   return "";
  String beanName = StrEx.upperFirst(PinYin.getShortPinYin(tableName));
  StringBuffer sb = new StringBuffer();
  String pk = AutoIncrement.getAutoIncrement(rsmd);
  if (pk == null)
   pk = "";
  StringBuffer ukey = new StringBuffer();
  int ii = 0;
  for (String key : indexs) {
   key = indexs.get(ii++);
   ukey.append(StrEx.upperFirst(PinYin.getShortPinYin(key)));
  }
  boolean wy = false;
  if (mi != null) {
   wy = mi.wy;
  }
  String fname = "selectBy" + ukey;
  String fields = getFields(rsmd, pk, true,primarykey);
 
  sb.append("    //根据索引"+ukey+"查询\r\n");
  if (wy) {
   sb.append("    public " + beanName + " ");
  } else {
   sb.append("    public List<" + beanName + "> ");
  }
  sb.append(fname);
  sb.append("(");
  ii = 0;
  for (String key : indexs) {
   key = indexs.get(ii++);
   String keyJavaType = JavaType.getType(rsmd, key);
   
   if(keyJavaType.equals("java.util.Date")){
    return "";
   }
   if (ii <= 1) {
    sb.append("");
   } else {
    sb.append(", ");
   }
   sb.append(keyJavaType);
   sb.append(" ");
   sb.append(PinYin.getShortPinYin(key));
  }
  sb.append(") {\r\n");
  sb.append("        return "+fname+"(");
  ii = 0;
  for (String key : indexs) {
   key = indexs.get(ii++);
   if (ii <= 1) {
    sb.append("");
   } else {
    sb.append(", ");
   }
   sb.append(PinYin.getShortPinYin(key));
  }
 
  sb.append(", TABLENAME);\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
 
  sb.append("    //根据索引"+ukey+"查询\r\n");
  if (wy) {
   sb.append("    public " + beanName + " ");
  } else {
   sb.append("    public List<" + beanName + "> ");
  }
  sb.append(fname);
  sb.append("(");
  ii = 0;
  for (String key : indexs) {
   key = indexs.get(ii++);
   String keyJavaType = JavaType.getType(rsmd, key);
   
   if(keyJavaType.equals("java.util.Date")){
    return "";
   }
   if (ii <= 1) {
    sb.append("");
   } else {
    sb.append(", ");
   }
   sb.append(keyJavaType);
   sb.append(" ");
   sb.append(PinYin.getShortPinYin(key));
  }
  sb.append(", String TABLENAME2) {\r\n");
  sb.append("        try{\r\n");
  sb.append("            String sql;\r\n");
  sb.append("            sql=\"SELECT " + fields + " FROM \"+TABLENAME2+\" WHERE ");
  ii = 0;
  for (String key : indexs) {
   key = indexs.get(ii++);
   if (ii > 1) {
    sb.append(" AND ");
   }
   sb.append(key);
   sb.append("=:");
   sb.append(key);
  }
  // sb.append(key).append("=:").append(key).append("\";");
  sb.append("\";\r\n");
  sb.append("            Map<String,Object> param = new HashMap<String,Object>();\r\n");
  for (String key : indexs) {
   String shortKey = PinYin.getShortPinYin(key);
   sb.append("            param.put(\"" + key + "\", " + shortKey
     + ");\r\n");
  }
  if (wy) {
   sb.append("            return ("
       + beanName
       + ")_np.queryForObject(sql, param, new BeanPropertyRowMapper<"+beanName+">("
       + beanName + ".class));\r\n");
  } else {
   sb.append("            return _np.query(sql, param, new BeanPropertyRowMapper<"+beanName+">("
       + beanName + ".class));\r\n");
  }
  sb.append("        }catch(Exception e){\r\n");
  sb.append("            //createTable(TABLENAME2);\r\n");
  if (wy) {
   sb.append("            return null;\r\n");
  } else {
   sb.append("            log.error(\"generateSelectByIndex\",e);").append("\r\n");
   sb.append("            return new Vector<"+beanName+">();\r\n");
  }
  sb.append("        }\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  return sb.toString();
 }




 static String generateCount(ResultSetMetaData rsmd, String tableName) {
  StringBuffer sb = new StringBuffer();
  sb.append("    //所有数据总数\r\n");
  sb.append("    public int count() {\r\n");
  sb.append("        return count(TABLENAME);\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  sb.append("    //所有数据总数\r\n");
//  sb.append("    @SuppressWarnings(\"deprecation\")\r\n");
  sb.append("    public int count(String TABLENAME2) {\r\n");
  sb.append("        String sql;\r\n");
  sb.append("        try{\r\n");
  sb.append("            sql=\"SELECT COUNT(*) FROM \"+TABLENAME2+\"\";\r\n");
  sb.append("            return _np.getJdbcOperations().queryForObject(sql,Integer.class);\r\n");
  sb.append("        }catch(Exception e){\r\n");
  sb.append("            //createTable(TABLENAME2);\r\n");
  sb.append("            log.error(\"count\",e);").append("\r\n");
  sb.append("            return 0;\r\n");
  sb.append("        }\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  return sb.toString();
 }




 static String generateIndexCount(ResultSetMetaData rsmd, String tableName,
   List<String> indexs, MyIndex mi) throws SQLException {
  StringBuffer ukey = new StringBuffer();
  int ii = 0;
  for (String key : indexs) {
   key = indexs.get(ii++);
   ukey.append(StrEx.upperFirst(PinYin.getShortPinYin(key)));
  }
 
  StringBuffer sb = new StringBuffer();
 
  sb.append("    //根据索引"+ukey+"统计数据\r\n");
 
  sb.append("    public int countBy"+ukey+"(");
  ii = 0;
  for (String key : indexs) {
   key = indexs.get(ii++);
   String shortKey = PinYin.getShortPinYin(key);
   String keyJavaType = JavaType.getType(rsmd, key);
   
   if(keyJavaType.equals("java.util.Date")){
    return "";
   }
   if (ii <= 1) {
    sb.append("");
   } else {
    sb.append(", ");
   }
   sb.append(keyJavaType);
   sb.append(" ");
   sb.append(shortKey);
  }
  sb.append(") {\r\n");
  sb.append("        return  countBy"+ukey+"(");
  ii = 0;
  for (String key : indexs) {
   key = indexs.get(ii++);
   String shortKey = PinYin.getShortPinYin(key);
   if (ii <= 1) {
    sb.append("");
   } else {
    sb.append(", ");
   }
   sb.append(shortKey);
  }
  sb.append(", TABLENAME);\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  sb.append("    //根据索引"+ukey+"统计数据\r\n");
  sb.append("    @SuppressWarnings(\"deprecation\")\r\n");
  sb.append("    public int countBy"+ukey+"(");
  ii = 0;
  for (String key : indexs) {
   key = indexs.get(ii++);
   String shortKey = PinYin.getShortPinYin(key);
   String keyJavaType = JavaType.getType(rsmd, key);
   
   if(keyJavaType.equals("java.util.Date")){
    return "";
   }
   if (ii <= 1) {
    sb.append("");
   } else {
    sb.append(", ");
   }
   sb.append(keyJavaType);
   sb.append(" ");
   sb.append(shortKey);
  }
  sb.append(", String TABLENAME2) {\r\n");
  sb.append("        try{\r\n");
  sb.append("            String sql;\r\n");
  sb.append("            sql=\"SELECT COUNT(*) FROM \"+TABLENAME2+\" WHERE ");
  ii = 0;
  for (String key : indexs) {
   key = indexs.get(ii++);
   String shortKey = PinYin.getShortPinYin(key);
   String jType = JavaType.getType(rsmd, key);
   if (ii > 1) {
    sb.append(" + \" AND ");
   }
   sb.append(key);
   if(jType.equals("String")){
    sb.append(" LIKE \'%\" + ");
    sb.append(shortKey);
    sb.append(" + \"%\'");
    sb.append("\"");
   }else{
    sb.append("=\" + ");
    sb.append(shortKey);
    sb.append("");
   }
  }
  sb.append(";\r\n");
  sb.append("            return _np.getJdbcOperations().queryForInt(sql);\r\n");
  sb.append("        }catch(Exception e){\r\n");
  sb.append("            //createTable(TABLENAME2);\r\n");
  sb.append("            log.error(\"generateIndexCount\",e);").append("\r\n");
  sb.append("            return 0;\r\n");
  sb.append("        }\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  return sb.toString();
 }


 static String generateSelectByPage(ResultSetMetaData rsmd, String tableName)
   throws SQLException {
  StringBuffer sb = new StringBuffer();
  String pk = AutoIncrement.getAutoIncrement(rsmd);
  if (pk == null)
   pk = "";
  String beanName = StrEx.upperFirst(PinYin.getShortPinYin(tableName));
  String fields = getFields(rsmd, pk, true,primarykey);
  sb.append("    //分页查询\r\n");
  sb.append("    public List<" + beanName + "> selectByPage(int begin, int num) {\r\n");
  sb.append("        return selectByPage(begin, num, TABLENAME);\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  sb.append("    //分页查询\r\n");
  sb.append("    public List<" + beanName + "> selectByPage(int begin, int num, String TABLENAME2) {\r\n");
  sb.append("        try{\r\n");
  sb.append("            String sql;\r\n");
  sb.append("            sql = \"SELECT " + fields + " FROM \"+TABLENAME2+\" LIMIT \"+begin+\", \"+num+\"\";\r\n");
  sb.append("            return _np.getJdbcOperations().query(sql,new BeanPropertyRowMapper<"+beanName+">(" + beanName + ".class));\r\n");
  sb.append("        }catch(Exception e){\r\n");
  sb.append("            //createTable(TABLENAME2);\r\n");
  sb.append("            log.error(\"selectByPage\",e);").append("\r\n");
  sb.append("            return new ArrayList<"+beanName+">();\r\n");
  sb.append("        }\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  return sb.toString();
 }


 /**
  * 单个更新
  * @param rsmd
  * @param tableName
  * @return
  * @throws SQLException
  */
 static String generateUpdate(ResultSetMetaData rsmd, String tableName)
   throws SQLException {
  StringBuffer sb = new StringBuffer();
  String key = AutoIncrement.getAutoIncrement(rsmd);
  String beanName = StrEx.upperFirst(PinYin.getShortPinYin(tableName));
  if (key == null){
   StringBuffer buffer_prk = new StringBuffer();
   for (String prk : primarykey) {
    buffer_prk.append(prk).append(",");
   }
   key = buffer_prk.deleteCharAt(buffer_prk.length() - 1).toString();
  }
  StringBuffer paramsTypeBuff = new StringBuffer();
  StringBuffer paramsStrBuff = new StringBuffer();
  StringBuffer whereBuff_big = new StringBuffer();
  StringBuffer whereBuff = new StringBuffer();
  for (String prk : primarykey) {
   String keyJavaType = JavaType.getType(rsmd, prk);
   paramsTypeBuff.append(keyJavaType+" "+prk).append(",");
   paramsStrBuff.append(prk).append(" ").append(",");
   whereBuff_big.append(prk+">:"+prk).append(" and ");
   whereBuff.append(prk+"=:"+prk).append(" and ");
  }


  sb.append("    //修改数据\r\n");
  sb.append("    public int updateByKey(" + beanName + " bean) {\r\n");
  sb.append("        return updateByKey(bean, TABLENAME);\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");

  sb.append("    //修改数据\r\n");
  sb.append("    public int updateByKey(" + beanName + " bean, String TABLENAME2) {\r\n");
  sb.append("        try{\r\n");
  sb.append("            String sql;\r\n");
  sb.append("            sql = \"UPDATE \"+TABLENAME2+\" SET ");
  int count = rsmd.getColumnCount();
  for (int i = 1; i <= count; i++) {
   String f = rsmd.getColumnName(i);
   if (f.equals(key))
    continue;
   sb.append(f);
   sb.append("=:");
   sb.append(f);
   if (i < count) {
    sb.append(",");
   }
  }
  sb.append(" WHERE "+StringBufferUtils.replaceStr_Last(whereBuff," and ")+"\";\r\n");
  sb.append("            SqlParameterSource ps = new BeanPropertySqlParameterSource(bean);\r\n");
  sb.append("            return _np.update(sql, ps);\r\n");
  sb.append("        }catch(Exception e){\r\n");
  sb.append("            log.error(\"updateByKey\",e);").append("\r\n");
  sb.append("            return 0;\r\n");
  sb.append("        }\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");



  sb.append("    //修改数据依靠版本号乐观锁\r\n");
  sb.append("    public int updateByKeyAndVersion(" + beanName + " bean,long version) {\r\n");
  sb.append("        return updateByKey(bean, TABLENAME,version,\"version\");\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");

  sb.append("    //修改数据依靠版本号乐观锁\r\n");
  sb.append("    public int updateByKey(" + beanName + " bean,long version,String version_name) {\r\n");
  sb.append("        return updateByKey(bean, TABLENAME,version,version_name);\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");


  sb.append("    //修改数据依靠版本号乐观锁\r\n");
  sb.append("    public int updateByKey(" + beanName + " bean, String TABLENAME2,Long version,String version_name) {\r\n");
  sb.append("        try{\r\n");
  sb.append("            String sql;\r\n");
  sb.append("            sql = \"UPDATE \"+TABLENAME2+\" SET ");
  count = rsmd.getColumnCount();
  for (int i = 1; i <= count; i++) {
   String f = rsmd.getColumnName(i);
   if (f.equals(key))
    continue;
   sb.append(f);
   sb.append("=:");
   sb.append(f);
   if (i < count) {
    sb.append(",");
   }
  }
  sb.append(" WHERE "+StringBufferUtils.replaceStr_Last(whereBuff," and ")+"\";\r\n");

  sb.append("            if(version != null ){  \r\n");
  sb.append("                sql += \" and  \"+version_name+\"=\"+version.longValue();\r\n");
  sb.append("             }\r\n");


  sb.append("            SqlParameterSource ps = new BeanPropertySqlParameterSource(bean);\r\n");
  sb.append("            return _np.update(sql, ps);\r\n");
  sb.append("        }catch(Exception e){\r\n");
  sb.append("            log.error(\"updateByKey\",e);").append("\r\n");
  sb.append("            return 0;\r\n");
  sb.append("        }\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");





  return sb.toString();
 }


 /**
  * 批量更新
  * @param rsmd
  * @param tableName
  * @return
  * @throws SQLException
  */
 static String generateBatchUpdate(ResultSetMetaData rsmd, String tableName)
   throws SQLException {
  StringBuffer sb = new StringBuffer();
  String key = AutoIncrement.getAutoIncrement(rsmd);
  String beanName = StrEx.upperFirst(PinYin.getShortPinYin(tableName));




  if (key == null){
   StringBuffer buffer_prk = new StringBuffer();
   for (String prk : primarykey) {
    buffer_prk.append(prk).append(",");
   }
   key = buffer_prk.deleteCharAt(buffer_prk.length() - 1).toString();
  }
  StringBuffer paramsTypeBuff = new StringBuffer();
  StringBuffer paramsStrBuff = new StringBuffer();
  StringBuffer whereBuff_big = new StringBuffer();
  StringBuffer whereBuff = new StringBuffer();
  //传递占位符
  StringBuffer whereBuff_param = new StringBuffer();
  for (String prk : primarykey) {
   String keyJavaType = JavaType.getType(rsmd, prk);
   paramsTypeBuff.append(keyJavaType+" "+prk).append(",");
   paramsStrBuff.append(prk).append(" ").append(",");
   whereBuff_big.append(prk+">:"+prk).append(" and ");
   whereBuff.append(prk+"=:"+prk).append(" and ");
   whereBuff_param.append(prk+"=?").append(" and ");
  }




  sb.append("    //批量修改数据\r\n");
  sb.append("    public int[] updateByKey (final List<" + beanName + "> beans) throws SQLException{\r\n");
  sb.append("        return updateByKey(beans, TABLENAME);\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  sb.append("    //批量修改数据\r\n");
  sb.append("    public int[] updateByKey (final List<" + beanName + "> beans, String TABLENAME2) throws SQLException{\r\n");
  sb.append("        try{\r\n");
  sb.append("            String sql;\r\n");
  sb.append("            sql = \"UPDATE \"+TABLENAME2+\" SET ");
  int count = rsmd.getColumnCount();
  for (int i = 1; i <= count; i++) {
   String f = rsmd.getColumnName(i);
   if (primarykey.contains(f))
    continue;
   sb.append(f);
   sb.append("=?");
   if (i < count) {
    sb.append(",");
   }
  }
  sb.append(" WHERE "+StringBufferUtils.replaceStr_Last(whereBuff_param," and ")+"\";\r\n");
  sb.append("            return _np.getJdbcOperations().batchUpdate(sql, new BatchPreparedStatementSetter() {\r\n");
  sb.append("                @Override\r\n");
  sb.append("                public int getBatchSize() {\r\n");
  sb.append("                    return beans.size();\r\n");
  sb.append("                }\r\n");
  sb.append("                @Override\r\n");
  sb.append("                public void setValues(PreparedStatement ps, int i) throws SQLException {\r\n");
  sb.append("                    "+beanName+" bean = beans.get(i);\r\n");


  /**
   * 先拼装 列字段
   */
  int count_jj=0;
  for (int i = 1; i <= count; i++) {
      String f = rsmd.getColumnName(i);
      if (primarykey.contains(f)){
       count_jj++;
       continue;
      }

      String s = "bean." + f;
      int columnType = rsmd.getColumnType(i);
      if (columnType == java.sql.Types.TIMESTAMP) {
       s = "new Timestamp(bean." + f + ".getTime())";
      }else if(columnType == java.sql.Types.DATE){
       s = "new java.sql.Date(bean." + f + ".getTime())";
      }
      //如果表中主键列字段排序 放在后面 则特殊处理
      sb.append("                    ps."+BatchOP.setOP(rsmd, i)+"(").append(i - count_jj).append(", " + s + ");\r\n");

  }

  /**
   * 再拼装 WHERE 后面的 主键 条件
   */
   for (int i = 0; i < primarykey.size(); i++) {
    String s = "bean." + primarykey.get(i);
    int index = (count - primarykey.size()) + i +1 ;
    sb.append("                    ps."+BatchOP.setOP(rsmd, primarykey.get(i))+"("+index+", " + s + ");\r\n");
   }



  sb.append("                }\r\n");
  sb.append("            });\r\n");
  sb.append("        }catch(Exception e){\r\n");
  sb.append("            log.error(\"int[] updateByKey\",e);").append("\r\n");
//	 sb.append("            return new int[0];\r\n");
  sb.append("            throw new SQLException(\"updateByKey is error\", e);\r\n");
  sb.append("        }\r\n");
  // }else{
  // sb.append("return 1;");
  // }
  sb.append("    }\r\n");
  sb.append("\r\n");
  return sb.toString();
 }

 
 
 static String generateCreateTable(Connection conn, ResultSet rs,
		   ResultSetMetaData rsmd, String tableName,Map<String,String>  map_comment) throws Exception {
		  String createSql = SqlEx.createMysqlTable_Comment(conn, rs, tableName,map_comment, 1, 1);
		  String[] ss = createSql.split("\n");
		  StringBuffer sb2 = new StringBuffer();
		  int i = 0;
		  for (String s : ss) {
		   if(i > 0)
		    sb2.append("                ");
		   sb2.append("\"");
		   sb2.append(s);
		   sb2.append("\"");
		   i ++;
		   if(i < ss.length){
		    sb2.append(" +");
		    sb2.append("\n ");
		   }
		  }
		  StringBuffer sb = new StringBuffer();
		  sb.append("    //创建表\r\n");
		  sb.append("    public void createTable(String TABLENAME2) throws SQLException{\r\n");
		  sb.append("        try{\r\n");
		  sb.append("            String sql;\r\n");
		  sb.append("            sql = "+sb2.toString()+";\r\n");
		  sb.append("            Map<String,String> params = new HashMap<String,String>();\r\n");
		  sb.append("            params.put(\"TABLENAME\", TABLENAME2);\r\n");
		  sb.append("            sql  = EasyTemplate.make(sql, params);\r\n");
		  sb.append("            _np.getJdbcOperations().execute(sql);\r\n");
		  sb.append("        }catch(Exception e){\r\n");
		  sb.append("            log.error(\"createTable\",e);").append("\r\n");
		  sb.append("            throw new SQLException(\"createTable is error\", e);\r\n");
		  sb.append("        }\r\n");
		  sb.append("    }\r\n");
		  sb.append("\r\n");
		  return sb.toString();
		 }
 
 
 static String generateTruncate(ResultSetMetaData rsmd, String tableName) {
  StringBuffer sb = new StringBuffer();
  sb.append("    //清空表\r\n");
  sb.append("    public void truncate() throws SQLException{\r\n");
  sb.append("        truncate(TABLENAME);\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  sb.append("    //清空表\r\n");
  sb.append("    public void truncate(String TABLENAME2) throws SQLException{\r\n");
  sb.append("        try{\r\n");
  sb.append("            String sql;\r\n");
  sb.append("            sql=\"TRUNCATE TABLE \"+TABLENAME2+\"\";\r\n");
  sb.append("            _np.getJdbcOperations().execute(sql);\r\n");
  sb.append("        }catch(Exception e){\r\n");
  sb.append("            log.error(\"truncate\",e);").append("\r\n");
  sb.append("            throw new SQLException(\"truncate is error\", e);\r\n");
  sb.append("        }\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  return sb.toString();
 }
 static String generateRepair(ResultSetMetaData rsmd, String tableName) {
  StringBuffer sb = new StringBuffer();
  sb.append("    //修复表\r\n");
  sb.append("    public void repair(){\r\n");
  sb.append("        repair(TABLENAME);\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  sb.append("    //修复表\r\n");
  sb.append("    public void repair(String TABLENAME2){\r\n");
  sb.append("        try{\r\n");
  sb.append("            String sql;\r\n");
  sb.append("            sql=\"REPAIR TABLE \"+TABLENAME2+\"\";\r\n");
  sb.append("            _np.getJdbcOperations().execute(sql);\r\n");
  sb.append("        }catch(Exception e){\r\n");
  sb.append("            log.error(\"repair\",e);").append("\r\n");
  sb.append("        }\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  return sb.toString();
 }
 static String generateOptimize(ResultSetMetaData rsmd, String tableName) {
  StringBuffer sb = new StringBuffer();
  sb.append("    //优化表\r\n");
  sb.append("    public void optimize(){\r\n");
  sb.append("        optimize(TABLENAME);\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
 
  sb.append("    //优化表\r\n");
  sb.append("    public void optimize(String TABLENAME2){\r\n");
  sb.append("        try{\r\n");
  sb.append("            String sql;\r\n");
  sb.append("            sql=\"OPTIMIZE TABLE \"+TABLENAME2+\"\";\r\n");
  sb.append("            _np.getJdbcOperations().execute(sql);\r\n");
  sb.append("        }catch(Exception e){\r\n");
  sb.append("            log.error(\"optimize\",e);").append("\r\n");
  sb.append("        }\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  return sb.toString();
 }
 static String generateExecute(ResultSetMetaData rsmd, String tableName) {
  StringBuffer sb = new StringBuffer();
  sb.append("    //执行sql\r\n");
  sb.append("    @Override\r\n");
  sb.append("    public void execute(String sql) throws SQLException{\r\n");
  sb.append("        try{\r\n");
  sb.append("            _np.getJdbcOperations().execute(sql);\r\n");
  sb.append("        }catch(Exception e){\r\n");
  sb.append("            log.error(\"execute\",e);").append("\r\n");
  sb.append("            throw new SQLException(\"execute is error\", e);\r\n");
  sb.append("        }\r\n");
  sb.append("    }\r\n");
  sb.append("\r\n");
  return sb.toString();
 }
 
 
 public static String getFields(ResultSetMetaData rsmd, String key,
   boolean bkey,List<String> primarykey) throws SQLException {
  StringBuffer fields = new StringBuffer();
  int count = rsmd.getColumnCount();
  for (int i = 1; i <= count; i++) {
   String columnName = rsmd.getColumnName(i);

   if(null != primarykey && primarykey.contains(columnName) && primarykey.size() == 1 && !bkey){
       continue;
   }else{
    if (key != null && key.equals(columnName) && !bkey)
       continue;
   }
   fields.append(columnName);
   if (i < count) {
    fields.append(",");
   }
  }
  return fields.toString();
 }
 public static String[] getFieldArrays(ResultSetMetaData rsmd, String key,
   boolean bkey) throws SQLException {
  List<String> fields = new Vector<String>();
  int count = rsmd.getColumnCount();
  for (int i = 1; i <= count; i++) {
   String columnName = rsmd.getColumnName(i);
   if (key != null && key.equals(columnName) && !bkey)
    continue;
   fields.add(columnName);
  }
  return fields.toArray(new String[1]);
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
 public static String getValues(ResultSetMetaData rsmd, String key,
   boolean bkey,List<String> primarykey) throws SQLException {
  StringBuffer values = new StringBuffer();
  int count = rsmd.getColumnCount();
  for (int i = 1; i <= count; i++) {
   String columnName = rsmd.getColumnName(i);
   if(null != primarykey && primarykey.contains(columnName) && primarykey.size() == 1 && !bkey){
    continue;
   }else{
    if (key != null && key.equals(columnName) && !bkey)
     continue;
   }
   values.append(":").append(columnName);
   if (i < count) {
    values.append(",");
   }
  }
  return values.toString();
 }
 public static String getQValues(ResultSetMetaData rsmd, String key,
   boolean bkey,List<String> primarykey) throws SQLException {
  StringBuffer values = new StringBuffer();
  int count = rsmd.getColumnCount();
  for (int i = 1; i <= count; i++) {
   String columnName = rsmd.getColumnName(i);
   if(null != primarykey && primarykey.contains(columnName) && primarykey.size() == 1 && !bkey){
    continue;
   }else{
    if (key != null && key.equals(columnName) && !bkey)
     continue;
   }
   values.append("?");
   if (i < count) {
    values.append(",");
   }
  }
  return values.toString();
 }
}
