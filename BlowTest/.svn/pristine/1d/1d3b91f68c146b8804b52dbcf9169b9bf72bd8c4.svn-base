package com.sale.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.sales.poolable.parsers.ORM_CONFIG_Parser;
import com.sales.pools.OrmConfigParserPool;

public class ConnFactory extends BlowFactory<Connection> {

	@Override
	protected void kill(Connection t) {
		try {
			t.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	protected Connection spawn() {
		Connection con = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe",
					"divyank",
					"divyank99");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}

}
