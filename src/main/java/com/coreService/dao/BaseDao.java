package com.coreService.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.coreService.util.Constants;

public class BaseDao {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(BaseDao.class);
	private final static String DRIVER=Constants.DB_DRIVER;
	private final static String URL=Constants.DB_URL;
	private final static String USERNAME=Constants.DB_USERNAME;
	private final static String PASSWORD=Constants.DB_PASSWORD;
	
	private Connection conn;
	private PreparedStatement pst;
	
	static{
		try{
			Class.forName(DRIVER);
		}catch(ClassNotFoundException e){
			logger.info("loading "+DRIVER+" driver error "+e.getMessage());
		}
	}
	
	private void getConnection(){
		try{
			conn=DriverManager.getConnection(URL,USERNAME,PASSWORD);
		}catch(SQLException e){
			logger.info("get connection error "+e.getMessage());
		}
	}
	
	public void executeSQL(String sql,Object[]params){
		if(sql==null||sql.trim().equals("")){
			logger.info("sql is null");
			return;
		}
		else{
			getConnection();
			try{
				pst=conn.prepareStatement(sql);
				if(params==null){
					params=new Object[0];
				}
				for(int i=0;i<params.length;i++){
					pst.setObject(i+1, params[i]);
				}
				pst.execute();
			}catch(SQLException e){
				logger.info("execute sql error:"+sql+"\t"+e.getMessage());
				close();
			}
		}
	}
	
	public ResultSet getResultSet(){
		try{
			return pst.getResultSet();
		}catch(SQLException e){
			logger.info("get resultset error "+e.getMessage());
			close();
		}
		return null;
	}
	
	public int getUpdateCount(){
		try{
			return pst.getUpdateCount();
		}catch(SQLException e){
			logger.info("get update count error "+e.getMessage());
			close();
		}finally{
			close();
		}
		return 0;
	}
	
	public void close(){
		if(pst!=null){
			try{
				pst.close();
			}catch(SQLException e){
				logger.info("close pst error "+e.getMessage());
			}
		}
		if(conn!=null){
			try{
				conn.close();
			}catch(SQLException e){
				logger.info("close conn error "+e.getMessage());
			}
		}
	}
}
