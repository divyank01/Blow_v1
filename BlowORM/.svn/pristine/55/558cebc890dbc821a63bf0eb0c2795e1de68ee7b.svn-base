package com.sales.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS.Maps.Attributes;
import com.sales.pools.ConnectionPool;

public class QuerryExecutor {

	private QuerryBuilder querryBuilder;

	private QuerryExecutor(){}
	protected static QuerryExecutor getExecutor(){
		return new QuerryExecutor();
	}
	
	protected boolean executeInsertOrUpdate(Object obj,ORM_MAPPINGS mappings,Map m) throws Exception{
		m.put(obj, null);
		Connection con=null;
		con=ConnectionPool.newInstance().borrowObject();
		querryBuilder=QuerryBuilder.newInstance();
		PreparedStatement stmt=null;
		if(countOfRecord(con, mappings, obj)==0){
			/*
			 * insert code will go here
			 */
			stmt=querryBuilder.createInsertQuerry(mappings, obj,con);
		}else{
			/*
			 * update code will go here
			 */
			stmt=querryBuilder.createUpadteQuerry(mappings, obj,con);		
		}
		stmt.execute();
		stmt.close();
		
		
		Map<String, Attributes> aMap=mappings.getMapForClass(obj.getClass().getCanonicalName()).getAttributeMap();
		Iterator<String> iter=aMap.keySet().iterator();
		while (iter.hasNext()) {
			String attr=iter.next();
			if(aMap.get(attr).isFk()){
				Object ob1=obj.getClass().getMethod(getterForField(aMap.get(attr).getName()), null).invoke(obj, null);
				if (ob1 instanceof Collection<?>){
					Iterator it=((Collection)ob1).iterator();
					while(it.hasNext()){
						executeInsertOrUpdate(it.next(), mappings, m);
					}
				}
				else{
					if(ob1!=null && !m.containsKey(ob1))
						executeInsertOrUpdate(ob1, mappings, m);
				}
			}
		}
		
		
		ConnectionPool.newInstance().returnObject(con);
		return false;
	}
	
	private int countOfRecord(Connection con,ORM_MAPPINGS mappings,Object obj) throws Exception{
		ResultSet rs=querryBuilder.getCountForPk(mappings, obj, con).executeQuery();
		int retVal=0;
		while(rs.next()){
			retVal=rs.getInt(1);
		}
		rs.close();
		return retVal;
	}
	
	private String getterForField(String field) {
		String retVal=(String.valueOf(field.charAt(0)).toUpperCase())+field.substring(1);
		return "get"+retVal;
	}

}
