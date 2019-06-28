package com.txdb.gpmanage.core.gp.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class JDBCPostgresql {
	
	public static void testJdbc() {
		try {
			Class.forName("org.postgresql.Driver");
//			String url = "jdbc:postgresql://192.168.73.120:5432/test";
			String url = "jdbc:postgresql://10.167.35.7:5432/gpperfmon";
			Connection connection = DriverManager.getConnection(url, "gpmon", "123456");
			
			Statement stat = connection.createStatement();
			ResultSet rs = stat.executeQuery("select * from gp_segment_configuration");
			while (rs.next())
				System.out.println(rs.getString(10));
			
			rs.close();
			stat.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		testJdbc();
	}
}
