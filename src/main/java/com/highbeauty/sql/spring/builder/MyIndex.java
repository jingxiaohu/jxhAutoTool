package com.highbeauty.sql.spring.builder;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

public class MyIndex {

	public int id; // ������
	public String mz; // ����
	public String field; // ������
	public boolean wy; // Ψһ
	
	public  String PrimaryKey;
	public String getType(ResultSetMetaData rsmd) throws SQLException{
		return JavaType.getType(rsmd, field);
	}
	
	@Override
	public String toString() {
		return "\nMyIndex [field=" + field + ", id=" + id + ", mz=" + mz
				+ ", wy=" + wy + "]";
	}
	
	public static final List<MyIndex> Indexs(Connection conn, String db, String table) throws SQLException{
		List<MyIndex> ret = new Vector<MyIndex>();
		DatabaseMetaData md = conn.getMetaData();
		ResultSet rs = md.getIndexInfo(db, null, table, false, true);
		ResultSet rs2 = md.getPrimaryKeys(db, null, table);
		rs2.next();
		String primarykey = rs2.getString("COLUMN_NAME");
		rs2.close();
		while (rs.next()) {
			MyIndex e = new MyIndex();
			e.id = rs.getInt(8);
			e.mz = rs.getString(6);
			e.field = rs.getString(9);
			e.wy = !rs.getBoolean(4);
			e.PrimaryKey =  primarykey; 
			if(e.mz.equals("PRIMARY"))
				continue;
			ret.add(e);
//            System.out.println("��ݿ���: "+ rs3.getString(1));  
//            System.out.println("��ģʽ: "+ rs3.getString(2));  
//            System.out.println("�����: "+ rs3.getString(3));  
//            System.out.println("����ֵ�Ƿ���Բ�Ψһ: "+ rs3.getString(4));  
//            System.out.println("�������: "+ rs3.getString(5));  
//            System.out.println("�������: "+ rs3.getString(6));  
//            System.out.println("��������: "+ rs3.getString(7));  
//            System.out.println("�����е������к�: "+ rs3.getString(8));  
//            System.out.println("�����: "+ rs3.getString(9));  
//            System.out.println("����������: "+ rs3.getString(10));  
//            System.out.println("TYPEΪ tableIndexStatisticʱ���Ǳ��е������������������Ψһֵ������: "+ rs3.getString(11));  
//            System.out.println("TYPEΪ tableIndexStatisicʱ�������ڱ��ҳ������������ڵ�ǰ�����ҳ��: "+ rs3.getString(12));  
//            System.out.println("����������: "+ rs3.getString(13));  
        }  
        rs.close(); 
        return ret;
	}
}
