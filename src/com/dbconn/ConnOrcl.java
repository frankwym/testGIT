package com.dbconn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 链接数据库
 * @author qtq
 * @version 2.0
 */
public class ConnOrcl {
	private static ConnOrcl connOrcl = null;
	private Connection conn = null;
	
	// 几个数据库变量
	private Statement stmt = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	// 初始化连接
	public ConnOrcl() {
		MyJdbc myJdbc = new MyJdbc();
		String url = myJdbc.getUrl();
		String user = myJdbc.getUser();
		String password = myJdbc.getPassword();
		// System.out.println(url);
		// System.out.println(user);
		// System.out.println(password);
		try {
			Class.forName(myJdbc.getDriver()).newInstance(); // 与url指定的数据源建立连接
			conn = DriverManager.getConnection(url, user, password); // 采用Statement进行查询
			stmt = conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ConnOrcl(String configPath) {
		MyJdbc myJdbc = new MyJdbc(configPath);
		String url = myJdbc.getUrl();
		String user = myJdbc.getUser();
		String password = myJdbc.getPassword();
		try {
			Class.forName(myJdbc.getDriver()).newInstance(); // 与url指定的数据源建立连接
			conn = DriverManager.getConnection(url, user, password); // 采用Statement进行查询
			stmt = conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Statement getStmt() {
		return stmt;
	}

	public Connection getConnection(){
		return conn;
	}
	
	public void close() {
		try {
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ConnOrcl getInstance(){
		connOrcl = new ConnOrcl();
		return connOrcl;
	}
	/**
	 * 选择数据表
	 * @param tableName
	 * @param columns
	 * @param whereClause
	 * @return
	 */
	public List<JSONObject> selectTable(String tableName, String[] columns, String whereClause) {
		String sql = "select";
		//    	Logger log=Logger.getLogger(com.zy.mapfactory.datamining.data.JDataConnection.class.getName());
		for (int i = 0; i < columns.length; i++) {
			sql +=  " " + columns[i] + ",";
		}
		sql = sql.substring(0, sql.length()-1);
		sql += " from " + tableName + " where " + whereClause;
		//    	System.out.println(sql);
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		
		try {
			preparedStatement = conn.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				JSONObject json = new JSONObject();
				for (int i = 0; i < columns.length; i++) {
					String string = "";
					Object obj = resultSet.getObject(columns[i]);
					if(obj != null)  string = obj.toString();
					json.put(columns[i], string);
				}		   

				jsonList.add(json);
			}
		} catch (SQLException e) {
			//    		log.info("获取基础地图信息失败..." + e.getMessage());
		} finally {
//			destroyConn();
		}
		return jsonList;	
	}

	
	public static void main(String[] args){
		JSONObject json = new JSONObject();
		json.put("1", "2");
		System.out.println(json.get("1"));
		
	}
	
	/**
	 * 向表中添加一条记录
	 * 
	 * @param tableName
	 * @param columns
	 * @param values
	 */
	public boolean insert2Table(String tableName, String columns, String values) {

		boolean result = false;
		Logger log=Logger.getLogger(ConnOrcl.class.getName());
		String sql = "insert into " + tableName + "(" + columns + ") values (" + values + ")";
		ConnOrcl connOrcl = new ConnOrcl();
		Statement stmt = connOrcl.getStmt();

		try {
			stmt.executeQuery(sql);
			log.info("插入记录成功...");
			result = true;

		} catch (SQLException e) {
			log.info("插入记录失败..." + e.getMessage());
		} finally {
			connOrcl.close();
		}
		return result;
	}
	/**
	 * 向表中插入记录
	 * 
	 * @param tableName
	 * @param values
	 * @return
	 */
	public boolean insert2Table(String tableName, String columns, String[] values) {
		//Logger log=Logger.getLogger(com.zy.mapfactory.datamining.data.JDataConnection.class.getName());
		boolean result = false;
		String sql = "insert into " + tableName + "(" + columns + ") values (";
		try {
			for (int i = 0; i < values.length; i++) {
				sql += " ?,";
			}
			sql = sql.substring(0, sql.length() - 1) + ")";

			preparedStatement = conn.prepareStatement(sql);
			for (int i = 0; i < values.length; i++) {
				preparedStatement.setString(i + 1, values[i]);
			}

			preparedStatement.execute();

			result = true;
			//log.info("插入记录成功...");

		} catch (SQLException e) {
			//log.info("插入记录失败..." + e.getMessage());
			System.out.println("插入记录失败..." + e.getMessage());
		} finally {
//			destroyConn();
		}


		return result;
	}

	/**将MarkerStr存储为CLOB   林斌  3.01
	 * @param tablename
	 * @param MapID
	 * @param markerString
	 */
	public boolean saveMarkerAsClob(String tableName,String mapID,String MarkerSTR)
	{
		boolean result = false;

		//String sql = "update SZCARTOGRAPHY.IMAGE_MAP set MARKER_STR = empty_clob() where MAPID='wangcong01'";
		String sql = "update " + tableName + " set MARKER_STR = empty_clob() where MAPID=" + "'" +mapID + "'";

		try {
			conn.setAutoCommit(false);
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.executeUpdate();
			conn.commit();
			result = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			result = false;

		}finally
		{
//			destroyConn();
		}



		try {
			conn.setAutoCommit(false);
			resultSet = null;
			java.sql.Clob clob = null;
			String sqlclob = "Select MARKER_STR FROM " + tableName + " Where MAPID='" + mapID + "' FOR Update";
			preparedStatement = conn.prepareStatement(sqlclob);
			resultSet = preparedStatement.executeQuery();
			if(resultSet.next())
			{
				clob = resultSet.getClob("MARKER_STR");
			}
			java.io.Writer  writer = clob.setCharacterStream(1);
			writer.write(MarkerSTR);
			writer.flush();
			writer.close();
			resultSet.close();
			conn.commit();
		} catch (Exception e) {
			// TODO: handle exception

			System.out.println(e.getMessage().toString());
		}finally{
//			destroyConn();
		} 	
		return result;
	}
	/**将态势标注存储为CLOB   林斌  4.28
	 * @param tablename
	 * @param MapID
	 * @param dynamicSymbolSTR
	 */
	public boolean saveDynamicSymbolAsClob(String tableName,String mapID,String dynamicSymbolSTR)
	{
		boolean result = false;

		//String sql = "update SZCARTOGRAPHY.IMAGE_MAP set MARKER_STR = empty_clob() where MAPID='wangcong01'";
		String sql = "update " + tableName + " set DYNAMIC_SYMBOL = empty_clob() where MAPID=" + "'" +mapID + "'";

		try {
			conn.setAutoCommit(false);
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.executeUpdate();
			conn.commit();
			result = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			result = false;

		}finally
		{
//			destroyConn();
		}


		try {
			conn.setAutoCommit(false);
			resultSet = null;
			java.sql.Clob clob = null;
			String sqlclob = "Select DYNAMIC_SYMBOL FROM " + tableName + " Where MAPID='" + mapID + "' FOR Update";
			preparedStatement = conn.prepareStatement(sqlclob);
			resultSet = preparedStatement.executeQuery();
			if(resultSet.next())
			{
				clob = resultSet.getClob("DYNAMIC_SYMBOL");
			}
			java.io.Writer  writer = clob.setCharacterStream(1);
			writer.write(dynamicSymbolSTR);
			writer.flush();
			writer.close();
			resultSet.close();
			conn.commit();
		} catch (Exception e) {
			// TODO: handle exception

			System.out.println(e.getMessage().toString());
		}finally{
//			destroyConn();
		}

		return result;
	}

	/**
	 * 更新表数据
	 * 
	 * @param tableName
	 * @param setClause
	 * @param whereClause
	 */
	public boolean updateTable(String tableName, String setClause, String whereClause) {
		boolean result = false;
		String sql = "update " + tableName + " set " + setClause + " where " + whereClause;

		try {
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.executeUpdate();

			conn.commit();
			result = true;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
//			destroyConn();
		}

		return result;
	}
	
	public boolean deleteFromTable(String tableName, String whereClause) {
		boolean bRes = false;
		String sql = "delete from " + tableName + " where " + whereClause;
		Logger log=Logger.getLogger(ConnOrcl.class.getName());
	
			try {
				preparedStatement = conn.prepareStatement(sql);
				preparedStatement.execute();
				bRes = true;
			} catch (SQLException e) {
				log.info("删除记录失败..." + e.getMessage());
			} finally {
//				destroyConn();
			}
			return bRes;
			}

	/**
	 * 释放Conn
	 */
	public void destroyConn() {
		Logger log=Logger.getLogger(ConnOrcl.class.getName());
		if (resultSet != null) {
			try {
				resultSet.close();
				resultSet = null;
			} catch (SQLException e) {
				log.info(("释放resultSet失败..." + e.getMessage()));
			}
		}

		if (preparedStatement != null) {
			try {
				preparedStatement.close();
				preparedStatement = null;
			} catch (SQLException e) {
				log.info(("释放preparedStatement失败..." + e.getMessage()));
			}
		}

		if (conn != null) {
			try {
				conn.close();
				conn = null;
			} catch (SQLException e) {
				log.info(("释放connection失败..." + e.getMessage()));
			}
		}
	}

	/**
	 *  读取MARKER_STR   linbin3.01
	 *  @param tablename
	 *  @param mapID
	 */

	public String getMarkerString(String tableName,String MAPID)
	{
		String markerString = null;
		String sql = "Select MARKER_STR FROM " + tableName + " Where MAPID='" + MAPID + "'";
		ConnOrcl con = ConnOrcl.getInstance();
		Connection myCon = con.getConnection();

		PreparedStatement myPreparedStatement = null;
		ResultSet rs = null;
		java.sql.Clob clob = null;
		try {
			myCon.setAutoCommit(false);
			myPreparedStatement = myCon.prepareStatement(sql);			
			rs = myPreparedStatement.executeQuery();
			if (rs.next()) {
				clob = rs.getClob("MARKER_STR");
			}

			if (null==clob||0==clob.length()) {
				markerString = "";
			}
			else {
				markerString = clob.getSubString((long)1,(int)clob.length());
			}
			//rs.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("失败");
			System.out.println(e.getMessage().toString());
		} finally {
			try {
				rs.close();
				myPreparedStatement.close();
				myCon.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		return markerString;
	}
	
	/**
	 *  读取MARKER_STR   linbin3.01
	 *  @param tablename
	 *  @param mapID
	 */

	public String getDyanmicSymbolSTR(String tableName,String MAPID)
	{
		String markerString = null;
		String sql = "Select DYNAMIC_SYMBOL FROM " + tableName + " Where MAPID='" + MAPID + "'";

		ConnOrcl con = ConnOrcl.getInstance();
		Connection myCon = con.getConnection();
		PreparedStatement myPreparedStatement = null;
		ResultSet rs = null;
		java.sql.Clob clob = null;
			try {
				myCon.setAutoCommit(false);
				myPreparedStatement = myCon.prepareStatement(sql);
				rs = myPreparedStatement.executeQuery();
				if (rs.next()) {
					clob = rs.getClob("DYNAMIC_SYMBOL");
				}

				if (null==clob||0==clob.length()) {
					markerString = "";
				}
				else {
					markerString = clob.getSubString((long)1,(int)clob.length());
				}
				//rs.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("失败");
				System.out.println(e.getMessage().toString());
			} finally {
				try {
					rs.close();
					myPreparedStatement.close();
					myCon.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		return markerString;
	}
	
	///获取统计报表数据
		public String getDataReport(String tableName,String[] fieldENG_Arr,String[] fieldCN_Arr)
		{
			String data = "";
			String field = "";
			@SuppressWarnings("unused")
			JSONArray jsonArr = new JSONArray();
			for(int i=0;i<fieldENG_Arr.length;i++)
			{
				field = field+fieldENG_Arr[i]+",";
			}
			field = field.substring(0,field.length()-1);

			String sql = "SELECT "+field+" FROM \"SZCARTOGRAPHY\"."+"\""+tableName+"\"";

			
				try{
					preparedStatement = conn.prepareStatement(sql);
					resultSet = preparedStatement.executeQuery();

					while(resultSet.next())
					{
						JSONObject object = new JSONObject();
						for(int i=0;i<fieldENG_Arr.length;i++)
						{
							String string = "";
							Object obj = resultSet.getObject(fieldENG_Arr[i]);
							if(obj != null)  string = obj.toString();
							object.put(fieldCN_Arr[i], string);
						}
						data += object+"@";
					}
				}

				catch (SQLException e) {
					//        		log.info("获取基础地图信息失败..." + e.getMessage());
				} finally {
					destroyConn();
				}

			data.substring(0, data.length()-1);
			return data;
		}
}
