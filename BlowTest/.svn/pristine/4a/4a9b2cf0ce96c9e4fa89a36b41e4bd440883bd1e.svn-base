package main;

import com.sales.poolable.parsers.ORM_CONFIG_Parser;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser;
import com.sales.pools.OrmConfigParserPool;
import com.sales.pools.OrmMappingPool;

public class PoolTester {

	public static void main(String...strings){
		try {
			ORM_MAPPINGS_Parser ob=OrmMappingPool.getInstance().borrowObject();
			
			OrmMappingPool.getInstance().returnObject(ob);
			
			//ORM_CONFIG_Parser p= OrmConfigParserPool.getInstance().borrowObject();
			//OrmConfigParserPool.getInstance().returnObject(p);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
