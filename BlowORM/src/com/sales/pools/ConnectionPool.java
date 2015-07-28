package com.sales.pools;

import java.sql.Connection;

import org.apache.commons.pool.PoolableObjectFactory;

import com.sales.poolables.factories.ConnectionFactory;

public class ConnectionPool extends SimpleObjectPool<Connection, String> {

	private static ConnectionPool pool;
	
	static{
		if(pool==null)
			pool=new ConnectionPool(new ConnectionFactory());
	}
	
	
	private ConnectionPool(PoolableObjectFactory<Connection> factory){
		super(factory);
	}

	public static ConnectionPool newInstance(){
		return pool;
	}
	
}
