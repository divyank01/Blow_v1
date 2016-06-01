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

	@Override
	public Connection borrowObject() throws Exception{
		Connection con=null;
		while(true){
			con=super.borrowObject();
			if(con==null)
				continue;
			else if(con.isClosed()){
				super.invalidateObject(con);
			}else{
				break;
			}
		}
		return con;
	}


	public static ConnectionPool getInstance(){
		return pool;
	}

}
