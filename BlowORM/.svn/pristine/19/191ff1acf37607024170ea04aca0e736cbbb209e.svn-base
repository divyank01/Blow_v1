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
package com.sales.core;

import java.util.Map;

import com.sales.blow.exceptions.BlownException;
import com.sales.core.helper.SessionContainer;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser;
import com.sales.poolable.parsers.ORM_QUERY_Parser;
import com.sales.poolable.parsers.ORM_QUERY_Parser.Queries.Query;

public class StoredQueryHandler {
	
	private QuerryExecutor executor=QuerryExecutor.getExecutor();
	
	
	private StoredQueryHandler(){}
	private static StoredQueryHandler handler;
	
	protected static StoredQueryHandler getHandler() {
		if(handler==null)
			handler=new StoredQueryHandler();
		return handler;
	}
	
	protected Object getObjectFromSQL(String id,Map input,SessionContainer session) throws Exception{
		try{
			Query q=ORM_QUERY_Parser.getInstance().getQueries(id);
			if(q!=null){
				return executor.runSql(q, input,session);
			}else
				throw new BlownException("Query id not found in the mappings");
		}catch(Exception e){
			e.printStackTrace();
			throw new BlownException(e.getLocalizedMessage());
		}
	}
	 
}
