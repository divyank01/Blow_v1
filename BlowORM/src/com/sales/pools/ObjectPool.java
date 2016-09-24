package com.sales.pools;

import java.sql.Connection;

import com.sales.poolable.parsers.ORM_CONFIG_Parser;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser;
import com.sales.poolable.parsers.ORM_QUERY_Parser;

public class ObjectPool {

	public static Connection getConnection() throws Exception{
		return ConnectionPool.getInstance().borrowObject();
	}

	public static ORM_CONFIG_Parser getConfig() throws Exception{
		return OrmConfigParserPool.getInstance().borrowObject();
	}

	public static ORM_MAPPINGS_Parser getMappings() throws Exception{
		return OrmMappingPool.getInstance().borrowObject();
	}

	public static ORM_QUERY_Parser getQueries() throws Exception{
		return null;
	}

	public static void submit(Connection connection) throws Exception{
		ConnectionPool.getInstance().returnObject(connection);
	}

	public static void submit(ORM_CONFIG_Parser config) throws Exception{
		OrmConfigParserPool.getInstance().returnObject(config);
	}

	public static void submit(ORM_MAPPINGS_Parser mapping) throws Exception{
		OrmMappingPool.getInstance().returnObject(mapping);
	}

	public static void submit(ORM_QUERY_Parser query) throws Exception{

	}
}
