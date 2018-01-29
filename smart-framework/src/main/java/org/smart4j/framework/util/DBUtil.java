package org.smart4j.framework.util;

import java.sql.Connection;
import java.sql.DriverManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class DBUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DBUtil.class);
	
	private static final String DRIVER = "com.mysql.jdbc.Driver";
	private static final String URL = "jdbc:mysql://localhost:3306/demo";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";
	
	private static Connection conn = null;
	
	//获取连接
	public static Connection getConnection(){
		try{
			Class.forName(DRIVER);
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		}catch(Exception e){
			LOGGER.error(e.getMessage(), e);
		}
		return conn;
	}
	
	//关闭连接
	public static void closeConnection(){
		try{
			if(conn != null){
				conn.close();
			}
		}catch(Exception e){
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	

}
