package com.sales.pools;

import java.sql.Connection;

import org.apache.commons.pool.PoolableObjectFactory;

import com.sales.poolable.parsers.ORM_CONFIG_Parser;
import com.sales.poolables.factories.OrmConfigParserFactory;

public class OrmConfigParserPool extends SimpleObjectPool<ORM_CONFIG_Parser, String> {

	private static OrmConfigParserPool pool;
	
	static{
		if(pool==null)
			pool=new OrmConfigParserPool(new OrmConfigParserFactory());
	}
	
	private OrmConfigParserPool(PoolableObjectFactory<ORM_CONFIG_Parser> factory){
		super(factory);
	}
	
	public static OrmConfigParserPool getInstance(){
		return pool;
	}
	
}
