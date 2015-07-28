package com.sales.poolables.factories;

import org.apache.commons.pool.BasePoolableObjectFactory;

import com.sales.poolable.parsers.ORM_MAPPINGS_Parser;

public final class OrmMappingfFactory extends BasePoolableObjectFactory<ORM_MAPPINGS_Parser>{

	@Override
	public ORM_MAPPINGS_Parser makeObject() throws Exception {
		return ORM_MAPPINGS_Parser.getInstance();
	}

}
