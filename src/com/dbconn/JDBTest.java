package com.dbconn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBTest {
	public static void main(String[] args) throws SQLException {
		ConnOrcl connOrcl = new ConnOrcl();
		Statement stmt = connOrcl.getStmt();
		ResultSet rs = null;
		try {
			String sql = "select * from \"MAPLAYOUT_MODLE\"";
			rs = stmt.executeQuery(sql);
			String temp="";
			while (rs.next()) {
				temp+=rs.getString(1)+",";
			}
			System.out.println(temp);
			connOrcl.close();
		} catch (Exception e) {

			System.out.println(e.getMessage());
		}
		}
}
