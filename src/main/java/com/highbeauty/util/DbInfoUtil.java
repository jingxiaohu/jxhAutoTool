package com.highbeauty.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
  
/** 
 *  
 * <p>Description: 获取数据库基本信息的工具类</p> 
 *  
 * @author qxl 
 * @date 2016年7月22日 下午1:00:34 
 */  
public class DbInfoUtil {  
      
    /** 
     * 根据数据库的连接参数，获取指定表的基本信息：字段名、字段类型、字段注释 
     * @param driver 数据库连接驱动 
     * @param url 数据库连接url 
     * @param user  数据库登陆用户名 
     * @param pwd 数据库登陆密码 
     * @param table 表名 
     * @return Map集合 
     */  
    public static Map<String,String> getTableInfo(String driver,String url,String user,String pwd,String table){  
    	Map<String,String> map = new HashMap<String,String>(); 
          
        Connection conn = null;       
        DatabaseMetaData dbmd = null;  
          
        try {  
            conn = getConnections(driver,url,user,pwd);  
              
            dbmd = conn.getMetaData();  
            ResultSet resultSet = dbmd.getTables(null, "%", table, new String[] { "TABLE" });  
              
            while (resultSet.next()) {  
                String tableName=resultSet.getString("TABLE_NAME");  
                System.out.println(tableName);  
                  
                if(tableName.equals(table)){  
                    ResultSet rs = conn.getMetaData().getColumns(null, getSchema(conn),tableName.toUpperCase(), "%");  
  
                    while(rs.next()){  
//                        System.out.println("字段名："+rs.getString("COLUMN_NAME")+"--字段注释："+rs.getString("REMARKS")+"--字段数据类型："+rs.getString("TYPE_NAME"));
                    	Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                        Matcher m = p.matcher(rs.getString("REMARKS"));
                        String dest = m.replaceAll("");
                    	String str_remark = "//"+rs.getString("TYPE_NAME")+"    "+dest.trim();
                    	System.out.println(str_remark);
                        String colName = rs.getString("COLUMN_NAME");  
                        map.put(colName, str_remark);  
                    }  
                }  
            }  
        } catch (SQLException e) {  
            e.printStackTrace();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }finally{  
            try {  
                conn.close();  
            } catch (SQLException e) {  
                e.printStackTrace();  
            }  
        }  
          
        return map;  
    }  
      
    
    /** 
     * 根据数据库的连接参数，获取指定表的基本信息：字段名、字段类型、字段注释 
     * @param driver 数据库连接驱动 
     * @param url 数据库连接url 
     * @param user  数据库登陆用户名 
     * @param pwd 数据库登陆密码 
     * @param table 表名 
     * @return Map集合 
     */  
    public static Map<String,String> getTableInfo2(String driver,String url,String user,String pwd,String table){  
    	Map<String,String> map = new HashMap<String,String>(); 
        Connection conn = null;  
        Statement stmt = null;
        try {  
            conn = getConnections(driver,url,user,pwd);  
            stmt = conn.createStatement();  
            ResultSet rs = stmt.executeQuery("show full columns from " + "`" + table + "`");
            System.out.println("【"+table+"】");  
            while (rs.next()) {  
//              System.out.println("字段名："+rs.getString("Field")+"--字段注释："+rs.getString("Comment")+"--字段数据类型："+rs.getString("Type"));
	        	Pattern p = Pattern.compile("\\s*|\t|\r|\n");
	            Matcher m = p.matcher(rs.getString("Comment"));
	            String dest = m.replaceAll("");
	        	String str_remark = "//"+rs.getString("Type")+"    "+dest.trim();
	        	System.out.println(str_remark);
	            String colName = rs.getString("Field");  
	            map.put(colName, str_remark);
                //TODO 这里获取字段的默认值 Default
                String default_value = rs.getString("Default");
                if("".equalsIgnoreCase(default_value)){
                    map.put(colName+"_default_value", "\"\"");
                }else{
                    map.put(colName+"_default_value", default_value);
                }

            }  
        } catch (SQLException e) {  
            e.printStackTrace();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }finally{  
            try { 
            	if(conn != null)
                   conn.close(); 
            	if(stmt != null)
            		stmt.close();
            } catch (SQLException e) {  
                e.printStackTrace();  
            }  
        }  
          
        return map;  
    }  
    /** 
     * 根据数据库的连接参数，获取指定表的基本信息：字段名、字段类型、字段注释 
     * @param driver 数据库连接驱动 
     * @param url 数据库连接url 
     * @param user  数据库登陆用户名 
     * @param pwd 数据库登陆密码 
     * @param table 表名 
     * @return Map集合 
     */  
    public static Map<String,String> getTableInfoDOC(String driver,String url,String user,String pwd,String table){  
    	Map<String,String> map = new HashMap<String,String>(); 
        Connection conn = null;  
        Statement stmt = null;
        try {  
            conn = getConnections(driver,url,user,pwd);  
            stmt = conn.createStatement();  
            ResultSet rs = stmt.executeQuery("show full columns from " + "`" + table + "`");
            while (rs.next()) {  
//              System.out.println("字段名："+rs.getString("Field")+"--字段注释："+rs.getString("Comment")+"--字段数据类型："+rs.getString("Type"));
	        	Pattern p = Pattern.compile("\\s*|\t|\r|\n");
	            Matcher m = p.matcher(rs.getString("Comment"));
	            String dest = m.replaceAll("");
	        	String str_remark = dest.trim();
	        	System.out.println(str_remark);
	            String colName = rs.getString("Field");  
	            map.put(colName, str_remark);
            }
        } catch (SQLException e) {  
            e.printStackTrace();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }finally{  
            try { 
            	if(conn != null)
                   conn.close(); 
            	if(stmt != null)
            		stmt.close();
            } catch (SQLException e) {  
                e.printStackTrace();  
            }  
        }  
          
        return map;  
    }  
    
    private static String changeDbType(String dbType) {  
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
  
    //获取连接  
    private static Connection getConnections(String driver,String url,String user,String pwd) throws Exception {  
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
      
    //其他数据库不需要这个方法 oracle和db2需要  
    private static String getSchema(Connection conn) throws Exception {  
        String schema;  
        schema = conn.getMetaData().getUserName();  
        if ((schema == null) || (schema.length() == 0)) {  
            throw new Exception("ORACLE数据库模式不允许为空");  
        }  
        return schema.toUpperCase().toString();  
  
    }  
  
    public static Map<String,String>  returnRemarkInfo(String ip,int port,String db,String user,String pwd,boolean reconnect,String encoding,String tablename) {  
          
        //这里是Oracle连接方法  
          
//        String driver = "oracle.jdbc.driver.OracleDriver";  
//        String url = "jdbc:oracle:thin:@192.168.12.44:1521:orcl";  
//        String user = "bdc";  
//        String pwd = "bdc123";  
//        //String table = "FZ_USER_T";  
//        String table = "FZ_USER_T";  
          
        //mysql  
        
/*        String driver = "com.mysql.jdbc.Driver"; 
//        String user = "root"; 
//        String pwd = "root"; 
        String url = "jdbc:mysql://"+ip+"/stopcar" 
                + "?useUnicode=true&characterEncoding=UTF-8"; 
        String table = "user_info"; */
    	
    	 String driver = ("com.mysql.jdbc.Driver");
		 String s = "jdbc:mysql://%s:%d/%s?autoReconnect=%s&characterEncoding=%s";
		 String url = String.format(s, ip, port, db,String.valueOf(reconnect), encoding);
         System.out.println(url);
          
      return  getTableInfo2(driver,url,user,pwd,tablename);  
    }  
      
    
    public static Map<String,String>  returnRemarkInfoDOC(String ip,int port,String db,String user,String pwd,boolean reconnect,String encoding,String tablename) {  
    	 String driver = ("com.mysql.jdbc.Driver");
		 String s = "jdbc:mysql://%s:%d/%s?autoReconnect=%s&characterEncoding=%s";
		 String url = String.format(s, ip, port, db,String.valueOf(reconnect), encoding);
         System.out.println(url);
          
      return  getTableInfoDOC(driver,url,user,pwd,tablename);  
    } 
    
    public static void main(String[] args) {
      String user = "root"; 
      String pwd = "root";
      String ip = "127.0.0.1";
      int port = 3306;
      String db = "stopcar";
      boolean reconnect = true;
      String encoding = "UTF-8";
      String tablename = "user_info";
      returnRemarkInfo(ip, port, db, user, pwd, reconnect, encoding, tablename);
	}

}  