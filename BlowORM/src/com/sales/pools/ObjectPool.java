/**
  *  BLOW-ORM is an open source ORM for java and its currently under development.
  *
  *  Copyright (C) 2016  @author Divyank Sharma
  *
  *  This program is free software: you can redistribute it and/or modify
  *  it under the terms of the GNU General Public License as published by
  *  the Free Software Foundation, either version 3 of the License, or
  *  (at your option) any later version.
  *
  *  This program is distributed in the hope that it will be useful,
  *  but WITHOUT ANY WARRANTY; without even the implied warranty of
  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  *  GNU General Public License for more details.
  *
  *  You should have received a copy of the GNU General Public License
  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  *  
  *  
  *  In Addition to it if you find any bugs or encounter any issue you need to notify me.
  *  I appreciate any suggestions to improve it.
  *  @mailto: divyank01@gmail.com
  */
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
