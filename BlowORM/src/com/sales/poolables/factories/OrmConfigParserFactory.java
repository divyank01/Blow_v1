package com.sales.poolables.factories;

import org.apache.commons.pool.BasePoolableObjectFactory;

import com.sales.poolable.parsers.ORM_CONFIG_Parser;

public final class OrmConfigParserFactory extends BasePoolableObjectFactory<ORM_CONFIG_Parser>{

	
	@Override
	public ORM_CONFIG_Parser makeObject() throws Exception {
		
		return ORM_CONFIG_Parser.getInstance();
	}
	
}
