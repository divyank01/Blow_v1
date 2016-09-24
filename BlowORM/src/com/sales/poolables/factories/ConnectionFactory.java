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
package com.sales.poolables.factories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.sales.poolable.parsers.ORM_CONFIG_Parser;
import com.sales.pools.BlowFactory;
import com.sales.pools.ObjectPool;
import com.sales.pools.OrmConfigParserPool;

public final class ConnectionFactory extends BlowFactory<Connection>{

	
	
	@Override
	public Connection spawn() throws Exception {
		Connection con = null;
		ORM_CONFIG_Parser parser=ObjectPool.getConfig();
		try {
			Class.forName(parser.getOrm_config().getDriver().trim());
			con = DriverManager.getConnection(parser.getOrm_config().getUrl(),
					parser.getOrm_config().getUserName(),
					parser.getOrm_config().getPwd());
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ObjectPool.submit(parser);
		return con;
	}

	
	@Override
	public void kill(Connection obj) throws Exception {
		obj.close();
		obj=null;
	}
	
	
	@Override
	public boolean validate(Connection obj) throws Exception {
		try {
			if(!obj.isClosed())
				return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


	@Override
	protected void invalidateObject(Connection t) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
