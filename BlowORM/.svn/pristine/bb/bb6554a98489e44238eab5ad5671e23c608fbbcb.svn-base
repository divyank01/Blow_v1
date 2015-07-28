package com.sales.pools;

import org.apache.commons.pool.PoolableObjectFactory;

import com.sales.poolable.parsers.ORM_MAPPINGS_Parser;
import com.sales.poolables.factories.OrmMappingfFactory;

public class OrmMappingPool extends SimpleObjectPool<ORM_MAPPINGS_Parser, String> {

	private static OrmMappingPool pool;
	
	/*static{
		if(pool==null)
			pool=new OrmMappingPool(new OrmMappingfFactory());
	}*/
	
	private OrmMappingPool(PoolableObjectFactory<ORM_MAPPINGS_Parser> factory) {
		super(factory);
	}

	public static OrmMappingPool getInstance(){
		if(pool==null)
			pool=new OrmMappingPool(new OrmMappingfFactory());
		return pool;
	}
}
