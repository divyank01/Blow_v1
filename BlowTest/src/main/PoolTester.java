package main;

import java.sql.Connection;

import com.sales.poolable.parsers.ORM_CONFIG_Parser;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser;
import com.sales.pools.ObjectPool;
import com.sales.pools.OrmConfigParserPool;
import com.sales.pools.OrmMappingPool;

public class PoolTester {

	public static void main(String...strings){
		try {
			Connection con=ObjectPool.getConnection();
			Connection con1=ObjectPool.getConnection();
			Connection con2=ObjectPool.getConnection();
			Connection con3=ObjectPool.getConnection();
			Connection con4=ObjectPool.getConnection();
			
			System.out.println(con);
			System.out.println(con1);
			System.out.println(con2);
			System.out.println(con3);
			System.out.println(con4);
			
			ObjectPool.submit(con);
			ObjectPool.submit(con1);
			ObjectPool.submit(con2);
			ObjectPool.submit(con3);
			ObjectPool.submit(con4);
			Connection con5=ObjectPool.getConnection();
			System.out.println(con5);
			ObjectPool.submit(con5);
			//ORM_CONFIG_Parser p= OrmConfigParserPool.getInstance().borrowObject();
			//OrmConfigParserPool.getInstance().returnObject(p);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
