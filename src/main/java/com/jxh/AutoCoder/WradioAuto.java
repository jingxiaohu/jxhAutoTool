package com.jxh.AutoCoder;

import com.highbeauty.sql.spring.builder.*;


public class WradioAuto {
	public static void main(String[] args) throws Throwable {
		boolean src = false;
		boolean is_maven = true;

		//TODO 是否采用包装类 true :是  false :否
		boolean is_javabean_WrapperClass = true;
		//TODO 是否让DaoBean 名称变化  例如： user_info表 user_infoDao2
		boolean is_change_DaoName = false;

		//TODO 是否重新覆盖 DaoFactoryBuild true :是  false :否
		boolean is_cover_DaoFactory = false;

		String moduleName="";//当没有的时候就不加入到路径中去
		String pkg = "com.gjp.wz.springjdbc.";
		/*String[] tablenames = {"user_info","car_in_out","fault_record","park_coupon","park_device","park_info","pay_park","rental_charging_rule","user_moneyback","user_park_coupon","user_login_log",
				"sms_running","sms_validate","user_feedback","user_carcode","pay_month_park","china_area","park_heartbeat","intimes_pay","user_vc_act","park_userinfo","parkinfo_partner","user_cash_apply"};*/
		String[] tablenames = {"country_area"};
		String ip = "127.0.0.1";
		int port = 3306;
		String user = "root";
		String password = "root";
		String databaseName = "account2";
		ABuilder.AutoCoder(is_cover_DaoFactory,is_change_DaoName,is_javabean_WrapperClass,is_maven,src,moduleName, pkg, tablenames, ip, port, user, password, databaseName);
	}

}
