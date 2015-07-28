package com.sales.poolables.factories;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.pool.BasePoolableObjectFactory;

import com.sales.poolable.parsers.ORM_CONFIG_Parser;
import com.sales.pools.OrmConfigParserPool;

public final class ConnectionFactory extends BasePoolableObjectFactory<Connection>{

	
	
	@Override
	public Connection makeObject() throws Exception {
		Connection con = null;
		ORM_CONFIG_Parser parser=OrmConfigParserPool.getInstance().borrowObject();
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(parser.getOrm_config().getUrl(),
					parser.getOrm_config().getUserName(),
					parser.getOrm_config().getPwd());
		} catch (Exception e) {
			e.printStackTrace();
		}
		OrmConfigParserPool.getInstance().returnObject(parser);
		return con;
	}

	@Override
	public void activateObject(Connection obj) throws Exception {
		super.activateObject(obj);
	}
	
	@Override
	public void destroyObject(Connection obj) throws Exception {
		super.destroyObject(obj);
	}
	
	@Override
	public void passivateObject(Connection obj) throws Exception {
		super.passivateObject(obj);
	}
	
	@Override
	public boolean validateObject(Connection obj) {
		return super.validateObject(obj);
	}
	
}
