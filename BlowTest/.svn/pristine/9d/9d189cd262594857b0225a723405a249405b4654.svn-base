package com.sale.util;

import java.sql.Connection;

public class ConnPool extends BlowPool<Connection> {

    private static ConnPool pool;
	private ConnPool(BlowFactory<Connection> factory) {
		super(factory);
	}
	
	public static ConnPool getPool(){
		if(pool==null)
			pool= new ConnPool(new ConnFactory());
		return pool;
	}

}
