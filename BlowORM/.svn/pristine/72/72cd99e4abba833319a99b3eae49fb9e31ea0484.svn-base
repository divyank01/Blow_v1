package com.sales.core;

import java.util.Map;

import com.sales.blow.exceptions.BlownException;
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
	
	protected Object getObjectFromSQL(String id,Map input) throws Exception{
		try{
			Query q=ORM_QUERY_Parser.getInstance().getQueries(id);
			String s=null;
			if(q!=null){
				s=q.getContent();
				return executor.runSql(s.trim(), q.getMappingObject(), q.getClassName());
			}else
				throw new BlownException("Query id not found in the mappings");
		}catch(Exception e){
			e.printStackTrace();
			throw new BlownException(e.getLocalizedMessage());
		}
	}
	 
}
