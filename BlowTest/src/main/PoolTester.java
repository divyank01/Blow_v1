package main;

import com.sales.poolable.parsers.ORM_CONFIG_Parser;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser;
import com.sales.pools.ObjectPool;
import com.sales.pools.OrmConfigParserPool;
import com.sales.pools.OrmMappingPool;

public class PoolTester {

	public static void main(String...strings){
		try {
			ORM_MAPPINGS_Parser ob=ObjectPool.getMappings();
			
			ObjectPool.submit(ob);
			
			System.out.println(ob.getOrm_Mappings().getDataBaseInfo());
			//ORM_CONFIG_Parser p= OrmConfigParserPool.getInstance().borrowObject();
			//OrmConfigParserPool.getInstance().returnObject(p);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
